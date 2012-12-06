package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class Variable implements Expression
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
    public Type inferType()
    throws TypeException
    {
        throw new UnsupportedOperationException();
    }

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
