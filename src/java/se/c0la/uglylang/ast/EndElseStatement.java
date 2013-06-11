package se.c0la.uglylang.ast;

import java.util.*;

public class EndElseStatement extends AbstractNode implements Node, Block
{
    private String endLbl;

    public EndElseStatement(String endLbl)
    {
        this.endLbl = endLbl;
    }

    public String getEndLbl() { return endLbl; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
