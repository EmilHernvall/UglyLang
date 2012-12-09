package se.c0la.uglylang.type;

public class StringType implements Type
{
    @Override
    public boolean isCompatible(Type other)
    {
        return other instanceof StringType;
    }

    @Override
    public String getName() { return "string"; }
}
