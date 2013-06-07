package se.c0la.uglylang.type;

import java.util.Set;

public class IntegerType extends AbstractType
{
    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return other instanceof IntegerType;
    }

    @Override
    public boolean hasField(String name)
    {
        return "str".equals(name);
    }

    @Override
    public Type getField(String field)
    {
        if ("str".equals(field)) {
            return new StringType();
        }

        return null;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "int"; }
}
