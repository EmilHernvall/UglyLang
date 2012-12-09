package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class NamedTupleGetInstruction implements Instruction
{
    private String field;

    public NamedTupleGetInstruction(String field)
    {
        this.field = field;
    }

    public String getField() { return field; }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_GET; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + field;
    }
}
