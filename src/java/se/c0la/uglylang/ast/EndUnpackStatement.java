package se.c0la.uglylang.ast;

import java.util.*;

public class EndUnpackStatement implements Node, Block
{
    private String lbl;

    public EndUnpackStatement(String lbl)
    {
        this.lbl = lbl;
    }

    public String getLabel() { return lbl; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
