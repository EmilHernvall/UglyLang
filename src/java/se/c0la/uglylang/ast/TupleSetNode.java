package se.c0la.uglylang.ast;

import java.util.List;

public class TupleSetNode implements Node
{
    private int idx;

    public TupleSetNode(int idx)
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
        return "tuple set idx=" + idx;
    }
}
