package se.c0la.uglylang.ast;

import java.util.*;

public class EndWhileStatement extends AbstractNode implements Node, Block
{
    private String lbl;
    private int condAddr;

    public EndWhileStatement(String lbl, int condAddr)
    {
        this.lbl = lbl;
        this.condAddr = condAddr;
    }

    public String getLbl() { return lbl; }
    public int getCondAddr() { return condAddr; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
