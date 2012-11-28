package se.c0la.uglylang.ast;

public class Variable extends Node
{
    private String name;

    public Variable(String name)
    {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "var:" + this.name;
    }
}

