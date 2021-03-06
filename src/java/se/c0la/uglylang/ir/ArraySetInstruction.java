package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

/**
 * Pops three values of the stack in the following order:
 * 1. index
 * 2. value
 * 3. array
 *
 * The index position of array is set to value. The array is
 * pushed back on the stack.
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
