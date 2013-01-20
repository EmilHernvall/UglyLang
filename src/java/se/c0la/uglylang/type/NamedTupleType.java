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
            System.out.println("not named tuple");
            return false;
        }

        if (other == this) {
            return true;
        }

        NamedTupleType otherTuple = (NamedTupleType)other;
        for (String field : parameters.keySet()) {
            Type type = parameters.get(field);
            Type otherType = otherTuple.parameters.get(field);
            if (otherType == null) {
                return false;
            }

            if ("self".equals(type.getName())) {
                System.out.println("encountered self");
                System.out.println("\tthis: " + this.getName());
                System.out.println("\ttype: " + type.getName());
                System.out.println("\totherType: " + otherType.getName());
                type = this;
            }
            /*if ("self".equals(otherType.getName())) {
                otherType = this;
            }*/

            if (!type.isCompatible(otherType)) {
                System.out.println("mismatch");
                System.out.println("\ttype: " + type.getName());
                System.out.println("\totherType: " + otherType.getName());
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
