package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class IndexNode implements Expression
{
    private Expression var;
    private Expression index;
    private boolean assignTarget;

    public IndexNode(Expression var, Expression index, boolean assignTarget)
    {
        this.var = var;
        this.index = index;
        this.assignTarget = assignTarget;
    }

    public Node getVariable() { return var; }
    public Node getIndex() { return index; }
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
        index.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.var + "[" + this.index + "]";
    }
}
