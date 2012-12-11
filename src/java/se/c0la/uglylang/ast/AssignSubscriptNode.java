package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class AssignSubscriptNode implements Node
{
    private Expression a;
    private String field;
    private Expression b;

    public AssignSubscriptNode(Expression a, String field, Expression b)
    {
        this.a = a;
        this.b = b;
        this.field = field;
    }

    public String getField() { return field; }

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
        return a.toString() + "." + field + " = " + b.toString();
    }
}
