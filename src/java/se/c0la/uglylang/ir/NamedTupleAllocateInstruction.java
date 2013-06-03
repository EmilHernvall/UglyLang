package se.c0la.uglylang.ir;

import java.util.Map;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.NamedTupleType;

public class NamedTupleAllocateInstruction implements Instruction
{
    private NamedTupleType type;

    public NamedTupleAllocateInstruction(NamedTupleType type)
    {
        this.type = type;
    }

    public void setType(NamedTupleType t) { this.type = t; }
    public NamedTupleType getType() { return type; }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName();
    }
}
