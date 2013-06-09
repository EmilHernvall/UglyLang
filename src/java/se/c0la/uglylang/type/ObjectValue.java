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
        this.fieldMap = new LinkedHashMap<String, Value>();
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
    public Value equalOp(Value b)
    {
        /*System.out.println(System.identityHashCode(b));
        ObjectValue val = (ObjectValue)b;
        for (String fld : fieldMap.keySet()) {
            Value fst = getField(fld);
            Value snd = val.getField(fld);
            if (snd == null) {
                return BooleanValue.FALSE;
            }
            if (!fst.equalOp(snd).equals(BooleanValue.TRUE)) {
                return BooleanValue.FALSE;
            }
        }

        return BooleanValue.TRUE;*/

        return BooleanValue.fromBool(this == b);
    }

    @Override
    public Value notEqualOp(Value b)
    {
        return BooleanValue.negate(equalOp(b));
    }

    @Override
    public ObjectType getType() { return type; }

    @Override
    public String toString()
    {
        return "Object " + type.getName();
    }
}
