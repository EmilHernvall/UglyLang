package se.c0la.uglylang.ir;

import java.util.Map;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ObjectType;

public class ObjectAllocateInstruction implements Instruction
{
    private ObjectType type;

    public ObjectAllocateInstruction(ObjectType type)
    {
        this.type = type;
    }

    public void setType(ObjectType t) { this.type = t; }
    public ObjectType getType() { return type; }

    @Override
    public OpCode getOpCode() { return OpCode.OBJECT_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName();
    }
}
