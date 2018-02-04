package sml.instructions

import sml.Instruction
import sml.Machine

/**
 * Describes the SML ADD instruction
 *
 */
class AddInstruction(label: String, val result: Int, val op1: Int, val op2: Int) : Instruction(label, "add") {

    override fun execute(m: Machine) {

        // Code provided was returning wrong results...

        //val value1 = m.registers.getRegister(op1)
        //val value2 = m.registers.getRegister(op2)

        //val total = value1 + value2

        m.registers.setRegister(result, op1 + op2)
    }

    override fun toString(): String {
        return super.toString() + " " + op1 + " + " + op2 + " to " + result
    }
}
