package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ObjectGetInstruction implements Instruction
{
    private String field;

    public ObjectGetInstruction(String field)
    {
        this.field = field;
    }

    public String getField() { return field; }

    @Override
    public OpCode getOpCode() { return OpCode.OBJECT_GET; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + field;
    }
}
