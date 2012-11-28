package se.c0la.uglylang.ast;

public class IntegerConstant extends BaseObject
{
    private int value;

    public IntegerConstant(String value)
    {
        this.value = Integer.parseInt(value);
    }

    public int getValue() { return value; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%d", this.value);
    }
}

