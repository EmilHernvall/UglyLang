package se.c0la.uglylang.ast;

public class StringConstant extends BaseObject
{
    private String value;

    public StringConstant(String value)
    {
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}

