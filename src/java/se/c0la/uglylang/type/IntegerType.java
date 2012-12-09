package se.c0la.uglylang.type;

public class IntegerType implements Type
{
    @Override
    public boolean isCompatible(Type other)
    {
        return other instanceof IntegerType;
    }

    @Override
    public String getName() { return "int"; }
}
