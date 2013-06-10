package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.IntegerType;
import se.c0la.uglylang.type.TypeException;

public class IntegerConstant extends AbstractNode implements Expression
{
    private int value;

    public IntegerConstant(String value)
    {
        this.value = Integer.parseInt(value);
    }

    public int getValue() { return value; }

    @Override
    public Type inferType()
    throws TypeException
    {
        return new IntegerType();
    }

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

