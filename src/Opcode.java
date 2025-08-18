/**
* Definitions of opcodes. LSB is at array index 0.
*
* DO NOT MODIFY THIS FILE.
*
*/

public class Opcode {
    /** Opcode for ADD operation */
    public static final boolean[] ADD = {false,false,false,true,true,false,true,false,false,false,true};

    /** Opcode for SUB operation */
    public static final boolean[] SUB = {false,false,false,true,true,false,true,false,false,true,true};

    /** Opcode for AND operation */
    public static final boolean[] AND = {false,false,false,false,true,false,true,false,false,false,true};

    /** Opcode for ORR operation */
    public static final boolean[] ORR = {false,false,false,false,true,false,true,false,true,false,true};

    /** Opcode for LDR operation */
    public static final boolean[] LDR = {false,true,false,false,false,false,true,true,true,true,true};

    /** Opcode for STR operation */
    public static final boolean[] STR = {false,false,false,false,false,false,true,true,true,true,true};

    /** Opcode for CBZ operation */
    public static final boolean[] CBZ = {false,false,true,false,true,true,false,true};

    /** Opcode for B operation */
    public static final boolean[] B = {true,false,true,false,false,false};

    /** Opcode for HLT operation */
    public static final boolean[] HLT = {false,true,false,false,false,true,false,true,false,true,true};
}
