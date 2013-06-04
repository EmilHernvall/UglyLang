package se.c0la.uglylang.ast;

import java.util.Map;
import java.util.LinkedHashMap;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ObjectType;
import se.c0la.uglylang.type.TypeException;

public class ObjectNode implements Expression
{
    private Map<String, Expression> values;

    public ObjectNode(Map<String, Expression> values)
    {
        this.values = values;
    }

    @Override
    public ObjectType inferType()
    throws TypeException
    {
        Map<String, Type> parameters = new LinkedHashMap<String, Type>();
        for (Map.Entry<String, Expression> entry : values.entrySet()) {
            parameters.put(entry.getKey(), entry.getValue().inferType());
        }

        return new ObjectType(parameters);
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        visitor.addScopeFlag(Visitor.Flag.OBJECT);

        ObjectSetNode setNode = null;
        for (Map.Entry<String, Expression> entry : values.entrySet()) {
            String field = entry.getKey();
            Expression expr = entry.getValue();
            expr.accept(visitor);

            try {
                Type type = expr.inferType();

                setNode = new ObjectSetNode(field, type);
                setNode.accept(visitor);
            } catch (TypeException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            ObjectEndNode endNode = new ObjectEndNode(inferType());
            endNode.accept(visitor);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }
        visitor.removeScopeFlag(Visitor.Flag.OBJECT);
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
