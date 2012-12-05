package se.c0la.uglylang.ast;

import java.util.List;

public class ArrayNode extends Node
{
    private List<Node> values;

    public ArrayNode(List<Node> values)
    {
        this.values = values;
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
