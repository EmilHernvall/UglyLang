package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class AssignNode extends AbstractNode implements Expression
{
    private Expression a;
    private Expression b;

    public AssignNode(Expression a, Expression b)
    {
        this.a = a;
        this.b = b;
    }

    public int findMaxSeq()
    {
        if (a instanceof SubscriptNode) {
            return ((SubscriptNode)a).getSeq();
        }
        else if (a instanceof IndexNode) {
            return ((IndexNode)a).getSeq();
        }
        else if (a instanceof Variable) {
            return ((Variable)a).getSeq();
        }

        throw new IllegalStateException("Assignments can only be variabesl, " +
                "indexes and subscripts.");
    }

    public Type inferType()
    throws TypeException
    {
        Type fst = a.inferType();
        Type snd = b.inferType();
        if (!fst.isCompatible(snd)) {
            throw new TypeException("Invalid assignment: " +
                fst.getName() + " != " + snd.getName());
        }

        return fst;
    }

    @Override
    public void accept(Visitor visitor)
    {
        b.accept(visitor);

        visitor.visit(this);

        visitor.addFlag(Visitor.Flag.ASSIGN);
        a.accept(visitor);
        visitor.removeFlag(Visitor.Flag.ASSIGN);
    }

    @Override
    public String toString()
    {
        return "(" + a + " = " + b.toString().trim() + ")";
    }
}
