package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

/**
 * Pops an integer of the stack and pushes an array of that size.
 **/
public class ArraySetInstruction implements Instruction
{
    public ArraySetInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.ARRAY_SET; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
