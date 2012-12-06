package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class FunctionCall implements Expression
{
    private String name;
    private List<Expression> params;

    public FunctionCall(String name, List<Expression> params)
    {
        this.name = name;
        this.params = params;
    }

    public String getFunctionName() { return name; }
    public int getParamCount() { return params.size(); }

    @Override
    public Type inferType()
    throws TypeException
    {
        throw new UnsupportedOperationException();
    }

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

