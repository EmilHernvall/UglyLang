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
public class ArrayAllocateInstruction implements Instruction
{
    public ArrayAllocateInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.ARRAY_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
