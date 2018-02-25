package sml.instructions

import sml.Instruction
import sml.Machine
import java.awt.Label

/**
 * Describes the SML Bnz instruction
 *
 */

class BnzInstruction(label: String, val result: Int, private val L2: String) : Instruction(label, "bnz") {

    override fun execute(m: Machine) {
        val value = m.registers.getRegister(result)
        //println("Value is " + value)
        //println("Label is " + label)
        //println("L2 is " + L2)
        if (value > 0) {
            m.pc = m.labels.getLabels().indexOf(L2)

        }
    }

    override fun toString(): String {
        return super.toString() + " register " + result + " label = " + L2
    }
}
