package se.c0la.uglylang.type;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class ObjectType extends AbstractType
{
    public final static ObjectType EMPTY =
        new ObjectType(new HashMap<String, Type>());

    private Map<String, Type> parameters;

    public ObjectType(Map<String, Type> parameters)
    {
        this.parameters = parameters;
    }

    public Map<String, Type> getParameters() { return parameters; }

    @Override
    public Type getField(String field)
    {
        return parameters.get(field);
    }

    @Override
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
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        if (!(other instanceof ObjectType)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        ObjectType otherObj = (ObjectType)other;
        for (String field : parameters.keySet()) {
            Type type = parameters.get(field);
            Type otherType = otherObj.parameters.get(field);
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

            if (!type.isCompatible(otherType, seenTypes)) {
                System.out.println("mismatch");
                System.out.println("\ttype: " + type.getName());
                System.out.println("\totherType: " + otherType.getName());
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName(Set<Type> seenTypes)
    {
        seenTypes.add(this);

        StringBuilder buf = new StringBuilder();
        buf.append("(");
        String delim = "";
        for (Map.Entry<String, Type> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Type param = entry.getValue();

            buf.append(delim);
            buf.append(key);
            buf.append(":");
            if (param != null) {
                buf.append(param.getName(seenTypes));
            } else {
                buf.append("N/A");
            }
            delim = ",";
        }
        buf.append(")");

        return buf.toString();
    }

    @Override
    public String toString() { return getName(); }
}
