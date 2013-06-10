package se.c0la.uglylang.ast;

import se.c0la.uglylang.Token;

public interface Node
{
    public void setPosition(int line, int column);
    public int getLine();
    public int getColumn();

    public abstract void accept(Visitor visitor);
}
