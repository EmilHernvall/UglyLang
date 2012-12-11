package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.ArrayType;

/**
 * Pops an integer of the stack and pushes an array of that size.
 **/
public class ArrayAllocateInstruction implements Instruction
{
    private ArrayType type;

    public ArrayAllocateInstruction(ArrayType type)
    {
        this.type = type;
    }

    public ArrayType getType() { return type; }

    @Override
    public OpCode getOpCode() { return OpCode.ARRAY_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName();
    }
}
