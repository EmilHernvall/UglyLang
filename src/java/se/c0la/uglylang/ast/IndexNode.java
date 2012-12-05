package se.c0la.uglylang.ast;

public class IndexNode extends Node
{
    private Node var;
    private Node index;
    private boolean assignTarget;

    public IndexNode(Node var, Node index, boolean assignTarget)
    {
        this.var = var;
        this.index = index;
        this.assignTarget = assignTarget;
    }

    public Node getVariable() { return var; }
    public Node getIndex() { return index; }
    public boolean isAssignTarget() { return assignTarget; }

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
