package se.c0la.uglylang.type;

import java.util.Set;
import java.util.HashSet;

public abstract class AbstractType implements Type
{
    @Override
    public String getName()
    {
        return getName(new HashSet<Type>());
    }

    public abstract String getName(Set<Type> seenTypes);

    public boolean isCompatible(Type other)
    {
        return isCompatible(other, new HashSet<Type>());
    }

    public abstract boolean isCompatible(Type other, Set<Type> seenTypes);

    @Override
    public boolean hasField(String name)
    {
        return false;
    }

    @Override
    public Type getField(String field)
    {
        return null;
    }
}
