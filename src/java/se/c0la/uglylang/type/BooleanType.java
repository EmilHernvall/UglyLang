package se.c0la.uglylang.type;

public class BooleanType implements Type
{
    @Override
    public boolean isCompatible(Type other)
    {
        return other instanceof BooleanType;
    }

    @Override
    public String getName() { return "boolean"; }
}
