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

    fun isClassCallable(string: String): Boolean {
        try {
            Class.forName(string).kotlin
        } catch (ex: ClassNotFoundException) {
            println("It broke!")
            return false
        }
        return true
    }

    /*fun getSpy(c: Class<*>): Spy {
        return Spy(c)
    }

    @Throws(ClassNotFoundException::class)
    fun getSpy(className: String) {
        return Spy(className)
    }

    @Throws(ClassNotFoundException::class)
    private constructor(className: Class<*>) : this(Class.forName(className.canonicalName)) {
    }*/

    fun getInstruction(label: String): Instruction {


        val ins = scan()


        // get class by its name
        try {
            Class.forName("sml.instructions." + ins.capitalize() + "Instruction").kotlin.toString()
        }catch (e: ClassNotFoundException) {
            return NoOpInstruction(label, line)
        }

        val kclass = Class.forName("sml.instructions." + ins.capitalize() + "Instruction").kotlin



        println("class " + kclass.qualifiedName)

        println("coonns " + kclass.constructors)

        println("beleza " + kclass.declaredMemberProperties.forEach { e -> println("here " + e.returnType)})

        // Empty array to collect the classes parameters
        val paramArray = ArrayList<Any>()

        // label added to array as it is a common constructor to all the classes
        paramArray.add(label)

        //println("lll with label " + lll)

        // Loop to the declared properties members of the class selected to build the arg array depending on the
        //   class return type
        var counter = 0
        kclass.declaredMemberProperties.forEach { p ->
            println("pRet " + p.returnType)
            if (p.returnType.toString() == "kotlin.Int") {
                if (kclass.toString() == "class sml.instructions.BnzInstruction") {
                    val specialGetStrCase = scan()
                    paramArray.add(specialGetStrCase)
                    counter += 1
                } else {
                    println("rddd " + p.returnType.toString())
                    val tmp = scanInt()
                    println("tmp " + tmp)
                    paramArray.add(tmp)
                    println("arr " + paramArray)
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
                    println("tmp1 " + tmp1)

                    paramArray.add(tmp1.toString())
                    println("arr1 " + paramArray)
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
