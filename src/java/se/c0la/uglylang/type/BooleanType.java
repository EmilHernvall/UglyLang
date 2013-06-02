package se.c0la.uglylang.type;

import java.util.Set;

public class BooleanType extends AbstractType
{
    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return other instanceof BooleanType;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "boolean"; }
}
