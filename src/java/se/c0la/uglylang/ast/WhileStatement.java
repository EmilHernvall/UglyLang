package se.c0la.uglylang.ast;

import java.util.*;

public class WhileStatement implements Node, Block
{
    private Node cond;
    private List<Node> stmts;

    public WhileStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    @Override
    public void accept(Visitor visitor)
    {
        int condAddr = visitor.getCurrentAddr();

        cond.accept(visitor);

        String endIfLbl = "While_" + visitor.getCurrentAddr();

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endIf = new EndWhileStatement(endIfLbl, condAddr);
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
