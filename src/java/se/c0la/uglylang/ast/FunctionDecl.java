package se.c0la.uglylang.ast;

import java.util.*;

public class FunctionDecl extends Block
{
    private String type;
    private List<Declaration> params;
    private List<Node> stmts;

    public FunctionDecl(String type, List<Declaration> params, List<Node> stmts)
    {
        this.type = type;
        this.params = params;
        this.stmts = stmts;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(type);
        buf.append("(");

        String delim = "";
        for (Node node : params) {
            buf.append(delim);
            buf.append(node.toString());
            delim = ", ";
        }
        buf.append(") ");
        buf.append("{\n");
        for (Node n : stmts) {
            if (n instanceof Block) {
                buf.append(indent(n.toString()));
            } else {
                buf.append("\t");
                buf.append(n.toString());
                buf.append(";\n");
            }
        }
        buf.append("}\n");

        return buf.toString();
    }
}

