package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.Symbol;

public class Declaration implements Node
{
    private Type type;
    private String name;
    private Symbol symbol;

    public Declaration(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Type getType() { return type; }
    public String getName() { return name; }

    public Symbol getSymbol() { return symbol; }
    public void setSymbol(Symbol v) { this.symbol = v; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", type, name);
    }
}

