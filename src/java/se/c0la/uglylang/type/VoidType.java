package se.c0la.uglylang.type;

import java.util.Set;

public final class VoidType extends AbstractType
{
    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return other instanceof VoidType;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "void"; }
}
