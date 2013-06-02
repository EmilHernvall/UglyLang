package se.c0la.uglylang.type;

import java.util.Map;
import java.util.Set;

public class CompoundType extends AbstractType
{
    private String name;
    private Map<String, Type> subTypes;

    public CompoundType(String name, Map<String, Type> subTypes)
    {
        this.name = name;
        this.subTypes = subTypes;
    }

    public Type getSubType(String name)
    {
        return subTypes.get(name);
    }

    @Override
    public String getName(Set<Type> seenTypes)
    {
        if (seenTypes.contains(this)) {
            return name;
        }

        seenTypes.add(this);

        StringBuilder buffer = new StringBuilder();
        buffer.append("(");
        String delim = "";
        for (Map.Entry<String, Type> entry : subTypes.entrySet()) {
            buffer.append(delim);
            buffer.append(entry.getKey().toString());
            buffer.append(" ");
            buffer.append(entry.getValue().getName(seenTypes));
            delim = ", ";
        }
        buffer.append(")");

        return buffer.toString();
    }

    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        if (other == this) {
            return true;
        }

        for (Type type : subTypes.values()) {
            if (type.isCompatible(other)) {
                return true;
            }
        }

        return false;
    }
}
