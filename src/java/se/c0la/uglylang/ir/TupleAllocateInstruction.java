package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class TupleAllocateInstruction implements Instruction
{
    public TupleAllocateInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.TUPLE_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
