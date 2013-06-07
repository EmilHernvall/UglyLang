package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class ObjectValue extends AbstractValue<ObjectType>
{
    public final static ObjectValue EMPTY = new ObjectValue(ObjectType.EMPTY);

    private ObjectType type;
    private Map<String, Value> fieldMap;
    private boolean empty;

    public ObjectValue(ObjectType type)
    {
        this.type = type;
        this.fieldMap = new HashMap<String, Value>();
    }

    public Set<String> getFields() { return fieldMap.keySet(); }

    @Override
    public Value getField(String field)
    {
        return fieldMap.get(field);
    }

    public void setField(String field, Value value)
    {
        fieldMap.put(field, value);
    }

    @Override
    public ObjectType getType() { return type; }

    @Override
    public String toString()
    {
        return "Object " + type.getName();
    }
}
