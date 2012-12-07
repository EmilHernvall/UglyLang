package se.c0la.uglylang.ast;

import java.util.Map;
import java.util.LinkedHashMap;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.NamedTupleType;
import se.c0la.uglylang.type.TypeException;

public class NamedTupleNode implements Expression
{
    private Map<String, Expression> values;

    public NamedTupleNode(Map<String, Expression> values)
    {
        this.values = values;
    }

    @Override
    public NamedTupleType inferType()
    throws TypeException
    {
        Map<String, Type> parameters = new LinkedHashMap<String, Type>();
        for (Map.Entry<String, Expression> entry : values.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().inferType());
        }

        return new NamedTupleType(parameters);
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);

        NamedTupleSetNode setNode = null;
        for (Map.Entry<String, Expression> entry : values.entrySet()) {
            String field = entry.getKey();
            Expression expr = entry.getValue();
            expr.accept(visitor);

            try {
                Type type = expr.inferType();

                setNode = new NamedTupleSetNode(field, type);
                setNode.accept(visitor);
            } catch (TypeException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            NamedTupleEndNode endNode = new NamedTupleEndNode(inferType());
            endNode.accept(visitor);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }
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
