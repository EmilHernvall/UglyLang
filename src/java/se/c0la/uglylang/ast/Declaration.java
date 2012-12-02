package se.c0la.uglylang.ast;

import se.c0la.uglylang.Type;

public class Declaration extends Node
{
    private Type type;
    private String name;

    public Declaration(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Type getType() { return type; }
    public String getName() { return name; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", type, name);
    }
}

