package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ArrayType;
import se.c0la.uglylang.type.TypeException;

public class ArrayNode implements Expression
{
    private List<Expression> values;

    public ArrayNode(List<Expression> values)
    {
        this.values = values;
    }

    public int getSize() { return values.size(); }

    @Override
    public ArrayType inferType()
    throws TypeException
    {
        Type type = null;
        for (Expression expr : values) {
            Type cmp = expr.inferType();
            if (type == null) {
                type = cmp;
                continue;
            }

            if (!type.getName().equals(cmp.getName())) {
                throw new TypeException("All values in array must have same type.");
            }
        }

        return new ArrayType(type);
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);

        int i = 0;
        ArraySetNode setNode = null;
        for (Node node : values) {
            setNode = new ArraySetNode(i);
            node.accept(visitor);
            setNode.accept(visitor);
            i++;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        String delim = "";
        for (Node node : values) {
            buf.append(delim);
            buf.append(node.toString());
            delim = ", ";
        }
        buf.append("]");

        return buf.toString();
    }
}
