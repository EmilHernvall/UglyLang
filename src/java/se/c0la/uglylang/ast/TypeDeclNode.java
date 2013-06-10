package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class TypeDeclNode extends AbstractNode implements Node
{
    private Type type;
    private String name;

    public TypeDeclNode(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public String getName() { return name; }
    public Type getType() { return type; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "Type " + name + " " + type.getName();
    }
}
