package se.c0la.uglylang.ast;

public class IntegerObject extends BaseObject
{
    private int value;

    public IntegerObject(String value)
    {
        this.value = Integer.parseInt(value);
    }

    @Override
    public String toString()
    {
        return String.format("%d", this.value);
    }
}

