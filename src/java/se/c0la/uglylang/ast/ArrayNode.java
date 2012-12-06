package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class ArrayNode implements Expression
{
    private List<Node> values;

    public ArrayNode(List<Node> values)
    {
        this.values = values;
    }

    public int getSize() { return values.size(); }

    @Override
    public Type inferType()
    throws TypeException
    {
        throw new UnsupportedOperationException();
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

        ArrayEndNode endNode = new ArrayEndNode();
        endNode.accept(visitor);
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
