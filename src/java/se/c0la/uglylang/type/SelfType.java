package se.c0la.uglylang.type;

public final class SelfType implements Type
{
    @Override
    public boolean isCompatible(Type other)
    {
        return other instanceof SelfType;
    }

    @Override
    public String getName() { return "self"; }
}
