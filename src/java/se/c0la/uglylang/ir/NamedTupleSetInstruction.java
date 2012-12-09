package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class NamedTupleSetInstruction implements Instruction
{
    private String field;

    public NamedTupleSetInstruction(String field)
    {
        this.field = field;
    }

    public String getField() { return field; }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_SET; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + field;
    }
}
