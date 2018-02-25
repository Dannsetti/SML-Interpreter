package sml

import sml.instructions.BnzInstruction
import sml.instructions.NoOpInstruction
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName


/*
 * The machine language interpreter
 */
data class Machine(var pc: Int, val noOfRegisters: Int) {
    // The labels in the SML program, in the order in which
    // they appear (are defined) in the program

    val labels: Labels

    // The SML program, consisting of prog.size() instructions, each
    // of class sml.Instruction (or one of its subclasses)
    val prog: MutableList<Instruction>

    // The registers of the SML machine
    val registers: Registers

    // The program counter; it contains the index (in prog) of
    // the next instruction to be executed.

    init {
        labels = Labels()
        prog = ArrayList<Instruction>()
        registers = Registers(noOfRegisters)
    }

    /**
     * Print the program
     */
    override fun toString(): String {
        val s = StringBuffer()
        for (i in 0 until prog.size)
            s.append(prog[i].toString() + "\n")
        return s.toString()
    }

    /**
     * Execute the program in prog, beginning at instruction 0.
     * Precondition: the program and its labels have been store properly.
     */
    fun execute() {
        while (pc < prog.size) {
            val ins = prog[pc++]
            ins.execute(this)
        }
    }

    // root of files
    private val PATH = System.getProperty("user.dir") + "/"
    // input line of file
    private var line: String = ""

    /**
     * translate the small program in the file into lab (the labels) and prog (the program)
     * return "no errors were detected"
     */
    fun readAndTranslate(file: String): Boolean {
        val fileName = PATH + file // source file of SML code
        return try {
            Scanner(File(fileName)).use { sc ->
                // Scanner attached to the file chosen by the user
                labels.reset()
                prog.clear()

                // Each iteration processes line and reads the next line into line
                while (sc.hasNext()) {
                    line = sc.nextLine()
                    // Store the label in label
                    val label = scan()

                    if (label.length > 0) {
                        labels.addLabel(label)
                        prog.add(getInstruction(label))
                    }
                }
            }
            true
        } catch (ioE: IOException) {
            println("File: IO error " + ioE.message)
            false
        }
    }


    /**
     * line should consist of an MML instruction, with its label already removed.
     * Translate line into an instruction with label label and return the instruction
     */

    fun getInstruction(label: String): Instruction {

        val ins = scan()


        // get class by its name
        try {
            Class.forName("sml.instructions." + ins.capitalize() + "Instruction").kotlin.toString()
        }catch (e: ClassNotFoundException) {
            return NoOpInstruction(label, line)
        }

        val kclass = Class.forName("sml.instructions." + ins.capitalize() + "Instruction").kotlin


        // Empty array to collect the classes parameters
        val paramArray = ArrayList<Any>()

        // label added to array as it is a common constructor to all the classes
        paramArray.add(label)


        // Loop to the declared properties members of the class selected to build the arg array depending on the
        //   class return type

        var counter = 0
        kclass.declaredMemberProperties.forEach { p ->

            if (p.returnType.toString() == "kotlin.Int") {
                if (kclass.toString() == "class sml.instructions.BnzInstruction") {
                    val specialGetStrCase = scan()
                    paramArray.add(specialGetStrCase)
                    counter += 1

                } else {
                    val tmp = scanInt()
                    paramArray.add(tmp)
                    counter += 1
                }

            } else {

                if (counter == 0 && kclass.toString() == "class sml.instructions.BnzInstruction") {
                    val specialGetIntCase = scanInt()
                    paramArray.add(specialGetIntCase)
                    counter += 1
                }

                else if (kclass.toString() == "class sml.instructions.NoOpInstruction") {
                    val specialCaseLine = line
                    paramArray.add(specialCaseLine)
                    counter += 1

                } else {
                    val tmp1 = scan()
                    paramArray.add(tmp1.toString())
                    counter += 1
                }
            }
        }


        // Cast ArrayList to Array
        val finalArgsArray = paramArray.toArray()

        // Command to find the selected class parameters to be called
        val kclassCaller = kclass.constructors.find { it.parameters.isNotEmpty() } ?: throw RuntimeException("No compatible constructor")

        return kclassCaller.call(*finalArgsArray) as Instruction

    }

    /*
     * Return the first word of line and remove it from line. If there is no
     * word, return ""
     */
    private fun scan(): String {
        line = line.trim { it <= ' ' }
        if (line.length == 0)
            return ""

        var i = 0
        while (i < line.length && line[i] != ' ' && line[i] != '\t') {
            i = i + 1
        }
        val word = line.substring(0, i)
        line = line.substring(i)
        return word
    }

    /**
     * Return the first word of line as an integer. If there is
     * any error, return the maximum int
     */
    private fun scanInt(): Int {
        val word = scan()
        if (word.length == 0) {
            return Integer.MAX_VALUE
        }

        return try {
            Integer.parseInt(word)
        } catch (e: NumberFormatException) {
            Integer.MAX_VALUE
        }
    }
}
