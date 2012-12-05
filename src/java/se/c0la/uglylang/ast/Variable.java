package se.c0la.uglylang.ast;

public class Variable extends Node
{
    private String name;
    private boolean assignTarget;

    public Variable(String name, boolean assignTarget)
    {
        this.name = name;
        this.assignTarget = assignTarget;
    }

    public String getName() { return name; }
    public boolean isAssignTarget() { return assignTarget; }

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
