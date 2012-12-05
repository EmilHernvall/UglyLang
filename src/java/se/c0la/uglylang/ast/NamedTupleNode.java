package se.c0la.uglylang.ast;

import java.util.Map;

public class NamedTupleNode extends Node
{
    private Map<String, Node> values;

    public NamedTupleNode(Map<String, Node> values)
    {
        this.values = values;
    }

    @Override
    public void accept(Visitor visitor)
    {
        for (Node node : values.values()) {
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
        for (Map.Entry<String, Node> node : values.entrySet()) {
            buf.append(delim);
            buf.append(node.getKey());
            buf.append(": ");
            buf.append(node.getValue());
            delim = ", ";
        }

        return buf.toString();
    }
}
