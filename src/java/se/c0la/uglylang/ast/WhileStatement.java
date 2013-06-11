package se.c0la.uglylang.ast;

import java.util.*;

public class WhileStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;

    private String lbl;

    public WhileStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    public void setLbl(String v) { this.lbl = v; }
    public String getLbl() { return lbl; }

    @Override
    public void accept(Visitor visitor)
    {
        int condAddr = visitor.getCurrentAddr();

        cond.accept(visitor);

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endIf = new EndWhileStatement(lbl, condAddr);
        endIf.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("if ");
        buf.append(cond.toString());

        return buf.toString();
    }
}
