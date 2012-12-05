package se.c0la.uglylang.ast;

public class SubscriptNode extends Node
{
    private Node var;
    private String key;
    private boolean assignTarget;

    public SubscriptNode(Node var, String key, boolean assignTarget)
    {
        this.var = var;
        this.key = key;
        this.assignTarget = assignTarget;
    }

    public Node getVar() { return var; }
    public String getKey() { return key; }
    public boolean isAssignTarget() { return assignTarget; }

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
