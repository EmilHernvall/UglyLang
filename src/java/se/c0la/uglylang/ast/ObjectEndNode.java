package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ObjectType;

public class ObjectEndNode extends AbstractNode implements Node
{
    private ObjectType type;

    public ObjectEndNode(ObjectType type)
    {
        this.type = type;
    }

    public ObjectType getType() { return type; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "object end";
    }
}
