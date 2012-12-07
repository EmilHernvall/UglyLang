package se.c0la.uglylang.ast;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public class Variable implements Expression
{
    private String name = null;
    private boolean assignTarget = false;
    private Symbol symbol = null;

    public Variable(String name, boolean assignTarget)
    {
        this.name = name;
        this.assignTarget = assignTarget;
    }

    public String getName() { return name; }
    public boolean isAssignTarget() { return assignTarget; }

    public void setSymbol(Symbol sym) { this.symbol = sym; }
    public Symbol getSymbol() { return symbol; }

    @Override
    public Type inferType()
    throws TypeException
    {
        if (symbol == null) {
            throw new IllegalStateException();
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
