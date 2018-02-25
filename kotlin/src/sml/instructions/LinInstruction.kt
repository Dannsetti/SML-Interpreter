package sml.instructions

import sml.Instruction
import sml.Machine

class LinInstruction(label: String, val result: Int, val value: Int) : Instruction(label, "lin") {

    override fun execute(m: Machine) {
        m.registers.setRegister(result, value)
    }

    override fun toString(): String {
        return super.toString() + " register " + result + " value is " + value
    }
}
