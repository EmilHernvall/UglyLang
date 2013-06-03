package se.c0la.uglylang.type;

import java.util.Set;

public class CompoundTerminalType extends AbstractType
{
    private String name;

    public CompoundTerminalType(String name)
    {
        this.name = name;
    }

    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        if (!(other instanceof CompoundTerminalType)) {
            return false;
        }

        return name.equals(((CompoundTerminalType)other).name);
    }

    @Override
    public String getName(Set<Type> seenTypes)
    {
        return name;
    }
}
