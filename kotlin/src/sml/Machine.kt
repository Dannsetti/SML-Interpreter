package sml

//import jdk.tools.jlink.internal.plugins.ClassForNamePlugin
import sml.instructions.AddInstruction
import sml.instructions.LinInstruction
//import sun.plugin2.liveconnect.JavaClass
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass
import kotlin.collections.ArrayList
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*


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




    fun getInstruction(label: String): Instruction {


        val ins = scan()

        val kclass = Class.forName("sml.instructions." + ins.capitalize() + "Instruction").kotlin

        println(kclass.qualifiedName)


        val lll = ArrayList<Any>()

        lll.add(label)

        println("lll with label " + lll)

        kclass.declaredMemberProperties.forEach { i ->
            if (i.returnType.toString() == "kotlin.Int") {
                val tm = scanInt()
                lll.add(tm)
            } else {
                val tm1 = scan()
                lll.add(tm1)
            }
        }

        val arr = lll.toArray()


        val linConst = kclass.constructors.find { it.parameters.size > 0 } ?: throw RuntimeException("No compatible constructor")


        return linConst.call(*arr) as Instruction

    }

    /**
     * line should consist of an MML instruction, with its label already removed.
     * Translate line into an instruction with label label and return the instruction
     */
//    fun getInstruction(label: String): Instruction {
//        val s1: Int // Possible operands of the instruction
//        val s2: Int
//        val r: Int
//        val l2: String
//
//        println(getInstructions(label))
//
//
//        // Instructions classes constructions to replace the explict call inse of the switch
//
//        val addConst = AddInstruction::class.constructors.find { it.parameters.size == 4 } ?: throw RuntimeException("No compatible constructor")
//        val linConst = LinInstruction::class.constructors.find { it.parameters.size == 3 } ?: throw RuntimeException("No compatible constructor")
//        val mulConst = MultiInstruction::class.constructors.find { it.parameters.size == 4 } ?: throw RuntimeException("No compatible constructor")
//        val subConst = SubInstruction::class.constructors.find { it.parameters.size == 4 } ?: throw RuntimeException("No compatible constructor")
//        val divConst = DivInstruction::class.constructors.find { it.parameters.size == 4 } ?: throw RuntimeException("No compatible constructor")
//        val outConst = OutInstruction::class.constructors.find { it.parameters.size == 2 } ?: throw RuntimeException("No compatible constructor")
//        val bnzConst = BnzInstruction::class.constructors.find { it.parameters.size == 3 } ?: throw RuntimeException("No compatible constructor")
//        val noOpConst = NoOpInstruction::class.constructors.find { it.parameters.size == 2 } ?: throw RuntimeException("No compatible constructor")
//
//        val ins = scan()
//
//        return when (ins) { // replace with reflection
//            "add" -> {
//                //r = scanInt()
//                //s1 = scanInt()
//                //s2 = scanInt()
//                //val p = printConstructors()
//                //AddInstruction(label, r, s1, s2)*//*
//                addConst.call(label, scanInt(), scanInt(), scanInt())
//
//            }
//            "lin" -> {
//                //r = scanInt()
//                //s1 = scanInt()
//                //LinInstruction(label, r, s1)
//                linConst.call(label, scanInt(), scanInt())
//            }
//            "mul" -> {
//                //r = scanInt()
//                //s1 = scanInt()
//                //s2 = scanInt()
//                //MultiInstruction(label, r, s1, s2)
//                mulConst.call(label, scanInt(), scanInt(), scanInt())
//            }
//            "sub" -> {
//                //r = scanInt()
//                //s1 = scanInt()
//                //s2 = scanInt()
//                //SubInstruction(label, r, s1, s2)
//                subConst.call(label, scanInt(), scanInt(), scanInt())
//            }
//            "div" -> {
//                //r = scanInt()
//                //s1 = scanInt()
//                //s2 = scanInt()
//                //DivInstruction(label, r, s1, s2)
//                divConst.call(label, scanInt(), scanInt(), scanInt())
//            }
//            "out" -> {
//                //s1 = scanInt()
//                //OutInstruction(label, s1)
//                outConst.call(label, scanInt())
//            }
//            "bnz" -> {
//                //s1 = scanInt()
//                //l2 = scan()
//                //BnzInstruction(label, s1, l2)
//                bnzConst.call(label, scanInt(), scan())
//            }
//
//        // You will have to write code here for the other instructions
//            else -> {
//                //NoOpInstruction(label, line)
//                noOpConst.call(label, line)
//            }
//        }
//    }

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
