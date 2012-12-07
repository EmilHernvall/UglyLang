package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class NamedTupleGetInstruction implements Instruction
{
    private int idx;

    public NamedTupleGetInstruction(int idx)
    {
        this.idx = idx;
    }

    private int getIndex() { return idx; }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_GET; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + idx;
    }
}
