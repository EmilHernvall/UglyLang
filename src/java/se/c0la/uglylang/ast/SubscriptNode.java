package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class SubscriptNode implements Expression
{
    private Expression var;
    private String key;
    private boolean assignTarget;

    public SubscriptNode(Expression var, String key, boolean assignTarget)
    {
        this.var = var;
        this.key = key;
        this.assignTarget = assignTarget;
    }

    public Expression getVar() { return var; }
    public String getKey() { return key; }
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
        var.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.var + "." + this.key;
    }
}
