package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class NamedTupleAllocateInstruction implements Instruction
{
    public NamedTupleAllocateInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
