package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class ObjectSetNode extends AbstractNode implements Node
{
    private String field;
    private Type type;

    public ObjectSetNode(String field, Type type)
    {
        this.field = field;
        this.type = type;
    }

    public String getField() { return field; }
    public Type getType() { return type; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "object set field=" + field;
    }
}
