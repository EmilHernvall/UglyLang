package se.c0la.uglylang.type;

import java.util.Map;

public class NamedTupleType implements Type
{
    private Map<String, Type> parameters;

    public NamedTupleType(Map<String, Type> parameters)
    {
        this.parameters = parameters;
    }

    public Map<String, Type> getParameters() { return parameters; }

    public boolean hasField(String name)
    {
        for (String field : parameters.keySet()) {
            if (name.equals(field)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCompatible(Type other)
    {
        if (!(other instanceof NamedTupleType)) {
            return false;
        }

        NamedTupleType otherTuple = (NamedTupleType)other;
        for (String field : parameters.keySet()) {
            Type type = parameters.get(field);
            Type otherType = otherTuple.parameters.get(field);
            if (otherType == null) {
                return false;
            }

            if (!type.isCompatible(otherType)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        String delim = "";
        for (Map.Entry<String, Type> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Type param = entry.getValue();

            buf.append(delim);
            buf.append(key);
            buf.append(":");
            buf.append(param.getName());
            delim = ",";
        }
        buf.append(")");

        return buf.toString();
    }

    @Override
    public String toString() { return getName(); }
}
