package sml.instructions

import sml.Instruction
import sml.Machine



class OutInstruction(label: String, val register: Int) : Instruction(label, "out") {

    override fun execute(m: Machine) {
        val value = m.registers.getRegister(register)
        println("Register $register value is $value")
    }

    override fun toString(): String {
        return super.toString() + " register " + register
    }
}
