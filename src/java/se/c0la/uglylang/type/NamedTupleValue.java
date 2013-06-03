package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class NamedTupleValue extends AbstractValue<NamedTupleType>
{
    private NamedTupleType type;
    private Map<String, Value> fieldMap;

    public NamedTupleValue(NamedTupleType type)
    {
        this.type = type;
        this.fieldMap = new HashMap<String, Value>();
    }

    public Set<String> getFields() { return fieldMap.keySet(); }

    public Value getField(String field)
    {
        return fieldMap.get(field);
    }

    public void setField(String field, Value value)
    {
        fieldMap.put(field, value);
    }

    @Override
    public NamedTupleType getType() { return type; }

    @Override
    public String toString()
    {
        return "NamedTuple " + type.getName();
    }
}
