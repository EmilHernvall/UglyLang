package se.c0la.uglylang.ast;

import java.util.*;

public class ElseIfStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;

    private EndElseIfStatement endElseIf;

    private String nextLbl;
    private String endLbl;

    public ElseIfStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    public void setNextLbl(String v) { this.nextLbl = v; }
    public String getNextLbl() { return nextLbl; }

    public void setEndLbl(String v) { this.endLbl = v; }

    @Override
    public void accept(Visitor visitor)
    {
        cond.accept(visitor);

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        endElseIf = new EndElseIfStatement(nextLbl, endLbl);
        endElseIf.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("else if ");
        buf.append(cond.toString());

        return buf.toString();
    }
}
