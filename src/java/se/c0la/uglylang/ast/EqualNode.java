package se.c0la.uglylang.ast;

public class EqualNode extends Node
{
    private Node a;
    private Node b;

    public EqualNode(Node a, Node b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString()
    {
        return "(" + a + " == " + b + ")";
    }
}
