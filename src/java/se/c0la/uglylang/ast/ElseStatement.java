package se.c0la.uglylang.ast;

import java.util.*;

public class ElseStatement extends AbstractNode implements Node, Block
{
    private List<Node> stmts;

    private String endLbl;

    public ElseStatement(List<Node> stmts)
    {
        this.stmts = stmts;
    }

    public void setEndLbl(String v) { this.endLbl = v; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endElse = new EndElseStatement(endLbl);
        endElse.accept(visitor);
    }

    @Override
    public String toString()
    {
        return "else";
    }
}
