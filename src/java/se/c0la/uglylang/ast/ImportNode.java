package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class ImportNode extends AbstractNode implements Node
{
    private String name;

    public ImportNode(String name)
    {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "Import " + name;
    }
}
