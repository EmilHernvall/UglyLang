package se.c0la.uglylang.ast;

import java.util.List;

public class ArrayEndNode implements Node
{
    public ArrayEndNode()
    {
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "ArrayEnd";
    }
}
