package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class NamedTupleValue extends AbstractValue<NamedTupleType>
{
    private NamedTupleType type;
    private Map<String, Symbol> fieldMap;

    public NamedTupleValue(NamedTupleType type)
    {
        this.type = type;
        this.fieldMap = new LinkedHashMap<String, Symbol>();
    }

    public void setField(String field, Symbol sym)
    {
        fieldMap.put(field, sym);
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
