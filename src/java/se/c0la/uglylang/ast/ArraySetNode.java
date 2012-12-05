package se.c0la.uglylang.ast;

import java.util.List;

public class ArraySetNode extends Node
{
    private int idx;

    public ArraySetNode(int idx)
    {
        this.idx = idx;
    }

    public int getIndex() { return idx; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "array set idx=" + idx;
    }
}
