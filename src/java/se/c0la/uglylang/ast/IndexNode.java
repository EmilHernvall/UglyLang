package se.c0la.uglylang.ast;

public class IndexNode extends Node
{
    private String var;
    private Node index;
    private boolean assignTarget;

    public IndexNode(String var, Node index, boolean assignTarget)
    {
        this.var = var;
        this.index = index;
        this.assignTarget = assignTarget;
    }

    public String getVariable() { return var; }
    public Node getIndex() { return index; }
    public boolean isAssignTarget() { return assignTarget; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.var + "[" + this.index + "]";
    }
}
