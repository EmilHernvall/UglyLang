package se.c0la.uglylang.ast;

import java.util.*;

public class EndElseIfStatement extends AbstractNode implements Node, Block
{
    private String nextLbl;
    private String endLbl;

    public EndElseIfStatement(String nextLbl, String endLbl)
    {
        this.nextLbl = nextLbl;
        this.endLbl = endLbl;
    }

    public String getNextLbl() { return nextLbl; }
    public String getEndLbl() { return endLbl; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
