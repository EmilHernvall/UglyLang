package se.c0la.uglylang.type;

public final class VoidType implements Type
{
    @Override
    public boolean isCompatible(Type other)
    {
        return other instanceof VoidType;
    }

    @Override
    public String getName() { return "void"; }
}
