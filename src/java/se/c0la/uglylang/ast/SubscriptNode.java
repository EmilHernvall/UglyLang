package se.c0la.uglylang.ast;

public class SubscriptNode extends Node
{
    private String tuple;
    private String key;
    private boolean assignTarget;

    public SubscriptNode(String tuple, String key, boolean assignTarget)
    {
        this.tuple = tuple;
        this.key = key;
        this.assignTarget = assignTarget;
    }

    public String getTuple() { return tuple; }
    public String getKey() { return key; }
    public boolean isAssignTarget() { return assignTarget; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.tuple + "." + this.key;
    }
}
