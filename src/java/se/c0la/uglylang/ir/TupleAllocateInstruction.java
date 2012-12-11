package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.TupleType;

public class TupleAllocateInstruction implements Instruction
{
    private TupleType type;

    public TupleAllocateInstruction(TupleType type)
    {
        this.type = type;
    }

    public TupleType getType() { return type; }

    @Override
    public OpCode getOpCode() { return OpCode.TUPLE_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName();
    }
}
