package sml.instructions

import sml.Instruction
import sml.Machine


/**
 * Describes the SML Divisor instruction
 *
 */

class DivInstruction(label: String, val result: Int, val op1: Int, val op2: Int) : Instruction(label, "div") {

    override fun execute(m: Machine) {
        //val value1 = m.registers.getRegister(op1)
        //val value2 = m.registers.getRegister(op2)
        m.registers.setRegister(result, op1 / op2)
    }

    override fun toString(): String {
        return super.toString() + " " + op1 + " / " + op2 + " to " + result
    }

}