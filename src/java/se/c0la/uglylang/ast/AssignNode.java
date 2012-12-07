package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class AssignNode implements Node
{
    private Node a;
    private Expression b;

    public AssignNode(Node a, Expression b)
    {
        this.a = a;
        this.b = b;
    }

    public Type getExprType() throws TypeException { return b.inferType(); }

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
        return "(" + a + " = " + b.toString().trim() + ")";
    }
}

