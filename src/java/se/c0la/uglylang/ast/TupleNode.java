package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class TupleNode implements Expression
{
    private List<Expression> values;

    public TupleNode(List<Expression> values)
    {
        this.values = values;
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void accept(Visitor visitor)
    {
        for (Node node : values) {
            node.accept(visitor);
        }

        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        String delim = "";
        for (Expression node : values) {
            buf.append(delim);
            buf.append(node.toString());
            delim = ", ";
        }
        buf.append(")");

        return buf.toString();
    }
}
