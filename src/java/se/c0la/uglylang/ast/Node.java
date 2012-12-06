package se.c0la.uglylang.ast;

public interface Node
{
    public abstract void accept(Visitor visitor);
}
