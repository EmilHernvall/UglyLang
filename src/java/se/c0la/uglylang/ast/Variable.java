package se.c0la.uglylang.ast;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class Variable implements Expression
{
    private String name = null;
    private Symbol symbol = null;
    private int seq;

    public Variable(String name, int seq)
    {
        this.name = name;
        this.seq = seq;
    }

    public String getName() { return name; }
    public int getSeq() { return seq; }

    public void setSymbol(Symbol sym) { this.symbol = sym; }
    public Symbol getSymbol() { return symbol; }

    @Override
    public Type inferType()
    throws TypeException
    {
        if (symbol == null) {
            throw new IllegalStateException(name + " is missing symbol.");
        }

        return symbol.getType();
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "var:" + this.name;
    }
}
