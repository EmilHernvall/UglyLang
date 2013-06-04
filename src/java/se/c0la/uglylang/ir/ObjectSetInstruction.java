package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ObjectSetInstruction implements Instruction
{
    private String field;

    public ObjectSetInstruction(String field)
    {
        this.field = field;
    }

    public String getField() { return field; }

    @Override
    public OpCode getOpCode() { return OpCode.OBJECT_SET; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + field;
    }
}
