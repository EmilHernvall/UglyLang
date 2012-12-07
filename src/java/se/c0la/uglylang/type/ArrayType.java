package se.c0la.uglylang.type;

import java.util.List;

public class ArrayType implements Type
{
    private Type type;

    public ArrayType(Type type)
    {
        this.type = type;
    }

    public Type getType() { return type; }

    @Override
    public String getName()
    {
        return type.getName() + "[]";
    }

    @Override
    public String toString() { return getName(); }
}
