package se.c0la.uglylang.ast;

import java.util.*;

public class IfStatement extends Block
{
    private Node cond;
    private List<Node> stmts;

    public IfStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    @Override
    public void accept(Visitor visitor)
    {
        cond.accept(visitor);

        String endIfLbl = "EndIf_" + visitor.getCurrentAddr();

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endIf = new EndIfStatement(endIfLbl);
        endIf.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("if ");
        buf.append(cond.toString());
        buf.append(" {\n");
        for (Node n : stmts) {
            if (n instanceof Block) {
                buf.append(indent(n.toString()));
            } else {
                buf.append("\t");
                buf.append(n.toString());
                buf.append(";\n");
            }
        }
        buf.append("}");

        return buf.toString();
    }
}
