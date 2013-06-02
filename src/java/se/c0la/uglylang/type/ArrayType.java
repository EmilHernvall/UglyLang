package se.c0la.uglylang.type;

import java.util.List;
import java.util.Set;

public class ArrayType extends AbstractType
{
    private Type type;

    public ArrayType(Type type)
    {
        this.type = type;
    }

    public Type getType() { return type; }

    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        if (!(other instanceof ArrayType)) {
            return false;
        }

        ArrayType otherArr = (ArrayType)other;
        return type.isCompatible(otherArr.type);
    }

    @Override
    public String getName(Set<Type> seenTypes)
    {
        return type.getName(seenTypes) + "[]";
    }

    @Override
    public String toString() { return getName(); }
}
