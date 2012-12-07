package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.NamedTupleType;

public class NamedTupleEndNode implements Node
{
    private NamedTupleType type;

    public NamedTupleEndNode(NamedTupleType type)
    {
        this.type = type;
    }

    public NamedTupleType getType() { return type; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "namedtuple end";
    }
}
