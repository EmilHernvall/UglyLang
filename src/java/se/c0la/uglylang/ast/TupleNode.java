package se.c0la.uglylang.ast;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TupleType;
import se.c0la.uglylang.type.TypeException;

public class TupleNode implements Expression
{
    private List<Expression> values;

    public TupleNode(List<Expression> values)
    {
        this.values = values;
    }

    public int getValueCount() { return values.size(); }

    @Override
    public Type inferType()
    throws TypeException
    {
        List<Type> parameters = new ArrayList<Type>();
        for (Expression expr : values) {
            parameters.add(expr.inferType());
        }

        return new TupleType(parameters);
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);

        int i = 0;
        TupleSetNode setNode = null;
        for (Node node : values) {
            node.accept(visitor);

            setNode = new TupleSetNode(i);
            setNode.accept(visitor);

            i++;
        }
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
