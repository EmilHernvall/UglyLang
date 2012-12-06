package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class AndNode implements Expression
{
    private Expression a;
    private Expression b;

    public AndNode(Expression a, Expression b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        Type fst = a.inferType();
        Type snd = b.inferType();

        if (!fst.getName().equals(snd.getName())) {
            throw new TypeException("Types " + fst.getName() + " and " +
                    snd.getName() + " does not match.");
        }

        return fst;
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
        return "(" + a + " and " + b + ")";
    }
}
