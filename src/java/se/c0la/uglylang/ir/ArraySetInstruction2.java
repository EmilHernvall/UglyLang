package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

/**
 * Pops three values of the stack in the following order:
 * 1. index
 * 2. array
 * 3. value
 *
 * The index position of array is set to value.
 **/
public class ArraySetInstruction2 implements Instruction
{
    public ArraySetInstruction2()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.ARRAY_SET2; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
