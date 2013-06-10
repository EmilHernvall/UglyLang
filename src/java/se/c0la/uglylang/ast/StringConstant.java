package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.StringType;
import se.c0la.uglylang.type.TypeException;

public class StringConstant extends AbstractNode implements Expression
{
    private String value;

    public StringConstant(String value)
    {
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public Type inferType()
    throws TypeException
    {
        return new StringType();
    }

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

