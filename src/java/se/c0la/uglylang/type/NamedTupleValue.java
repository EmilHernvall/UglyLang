package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class NamedTupleValue extends AbstractValue<NamedTupleType>
{
    private NamedTupleType type;
    private Map<String, Symbol> fieldMap;

    public NamedTupleValue(NamedTupleType type, Map<String, Symbol> fields)
    {
        this.type = type;
        this.fieldMap = fields;
    }

    public Symbol getField(String field)
    {
        return fieldMap.get(field);
    }

    @Override
    public NamedTupleType getType() { return type; }

    @Override
    public String toString()
    {
        return "NamedTuple " + type.getName();
    }
}
