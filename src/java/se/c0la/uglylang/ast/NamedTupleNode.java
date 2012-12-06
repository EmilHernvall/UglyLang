package se.c0la.uglylang.ast;

import java.util.Map;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class NamedTupleNode implements Expression
{
    private Map<String, Expression> values;

    public NamedTupleNode(Map<String, Expression> values)
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
        for (Expression node : values.values()) {
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
        for (Map.Entry<String, Expression> node : values.entrySet()) {
            buf.append(delim);
            buf.append(node.getKey());
            buf.append(": ");
            buf.append(node.getValue());
            delim = ", ";
        }

        return buf.toString();
    }
}
