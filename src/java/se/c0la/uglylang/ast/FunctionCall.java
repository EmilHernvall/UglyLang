package se.c0la.uglylang.ast;

import java.util.*;

public class FunctionCall extends BaseObject
{
    private String name;
    private List<Node> params;

    public FunctionCall(String name, List<Node> params)
    {
        this.name = name;
        this.params = params;
    }

    public String getFunctionName() { return name; }
    public int getParamCount() { return params.size(); }

    @Override
    public void accept(Visitor visitor)
    {
        for (Node node : params) {
            node.accept(visitor);
        }

        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append("(");

        String delim = "";
        for (Node node : params) {
            buffer.append(delim);
            buffer.append(node.toString());
            delim = ", ";
        }
        buffer.append(")");

        return buffer.toString();
    }
}

