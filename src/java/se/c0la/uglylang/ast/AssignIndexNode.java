package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class AssignIndexNode implements Node
{
    private Variable a;
    private Expression b;
    private Expression c;

    public AssignIndexNode(Variable a, Expression b, Expression c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Variable getVariable() { return a; }

    @Override
    public void accept(Visitor visitor)
    {
        a.accept(visitor);
        c.accept(visitor);
        b.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return a.toString() + "[" + b.toString() + "] = " + c.toString();
    }
}
