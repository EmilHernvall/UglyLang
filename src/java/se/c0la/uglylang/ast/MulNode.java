package se.c0la.uglylang.ast;

public class MulNode extends Node
{
    private Node a;
    private Node b;

    public MulNode(Node a, Node b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public void accept(Visitor visitor)
    {
        a.accept(visitor);
        b.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "(" + a + " * " + b + ")";
    }
}

