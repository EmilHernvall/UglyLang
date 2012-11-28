package se.c0la.uglylang.ast;

import java.util.*;

public class FunctionObject extends BaseObject
{
    private String name;
    private List<Node> params;

    public FunctionObject(String name, List<Node> params)
    {
        this.name = name;
        this.params = params;
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

