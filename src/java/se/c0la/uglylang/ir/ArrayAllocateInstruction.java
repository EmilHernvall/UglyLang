package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

/**
 * Pops an integer of the stack and pushes an array of that size.
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
