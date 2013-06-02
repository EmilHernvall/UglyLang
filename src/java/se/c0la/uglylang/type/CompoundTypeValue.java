package se.c0la.uglylang.type;

public class CompoundTypeValue extends AbstractValue
{
    private CompoundType type;
    private String name;

    public CompoundTypeValue(CompoundType type, String name)
    {
        this.type = type;
        this.name = name;
    }

    @Override
    public CompoundType getType() { return type; }

    @Override
    public String toString()
    {
        return this.name;
    }
}
