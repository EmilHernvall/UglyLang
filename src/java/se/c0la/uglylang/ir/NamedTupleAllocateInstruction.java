package se.c0la.uglylang.ir;

import java.util.Map;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.NamedTupleType;

public class NamedTupleAllocateInstruction implements Instruction
{
    private NamedTupleType type;
    private Map<String, Symbol> fields;

    public NamedTupleAllocateInstruction(NamedTupleType type,
            Map<String, Symbol> fields)
    {
        this.type = type;
        this.fields = fields;
    }

    public NamedTupleType getType() { return type; }
    public Map<String, Symbol> getFieldsMap() { return fields; }

    @Override
    public OpCode getOpCode() { return OpCode.NTUPLE_ALLOCATE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + type.getName();
    }
}
