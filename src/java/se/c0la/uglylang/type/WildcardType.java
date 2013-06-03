package se.c0la.uglylang.type;

import java.util.Set;

public class WildcardType extends AbstractType
{
    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return true;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "wildcard"; }
}
