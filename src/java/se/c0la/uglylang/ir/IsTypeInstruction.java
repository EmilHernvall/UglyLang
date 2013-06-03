package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;

public class IsTypeInstruction implements Instruction
{
    private Type type;

    public IsTypeInstruction(Type type)
    {
        this.type = type;
    }

    public Type getType() { return type; }

    @Override
    public OpCode getOpCode() { return OpCode.ISTYPE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName().toString();
    }
}
