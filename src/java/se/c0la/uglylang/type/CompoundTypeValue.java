package se.c0la.uglylang.type;

public class CompoundTypeValue extends AbstractValue
{
    private CompoundTerminalType type;

    public CompoundTypeValue(CompoundTerminalType type)
    {
        this.type = type;
    }

    @Override
    public CompoundTerminalType getType() { return type; }

    @Override
    public String toString()
    {
        return type.getName();
    }
}
