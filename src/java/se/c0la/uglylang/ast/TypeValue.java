package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.CompoundType;
import se.c0la.uglylang.type.TypeException;

public class TypeValue implements Expression
{
    private CompoundType type;
    private String name;

    public TypeValue(CompoundType type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public CompoundType getType() { return type; }
    public String getName() { return name; }

    @Override
    public Type inferType()
    throws TypeException
    {
        return type;
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
