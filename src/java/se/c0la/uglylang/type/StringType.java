package se.c0la.uglylang.type;

import java.util.Set;

public class StringType extends AbstractType
{
    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return other instanceof StringType;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "string"; }
}
