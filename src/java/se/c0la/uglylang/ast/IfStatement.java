package se.c0la.uglylang.ast;

import java.util.*;

public class IfStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;
    private List<ElseIfStatement> elseIfList;
    private ElseStatement elseStmt;

    private String nextLbl;
    private String endLbl;

    public IfStatement(Node cond,
                       List<Node> stmts,
                       List<ElseIfStatement> elseIfList,
                       ElseStatement elseStmt)
    {
        this.cond = cond;
        this.stmts = stmts;
        this.elseIfList = elseIfList;
        this.elseStmt = elseStmt;
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

        EndIfStatement endIf = new EndIfStatement(nextLbl, endLbl);
        endIf.accept(visitor);

        for (ElseIfStatement elseIfStmt : elseIfList) {
            elseIfStmt.setEndLbl(endLbl);
            elseIfStmt.accept(visitor);
        }

        if (elseStmt == null) {
            elseStmt = new ElseStatement(new ArrayList<Node>());
        }

        elseStmt.setEndLbl(endLbl);
        elseStmt.accept(visitor);
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
