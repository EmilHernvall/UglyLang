package se.c0la.uglylang.ast;

import se.c0la.uglylang.Token;

public abstract class AbstractNode implements Node
{
    private int line, column;

    public void setPosition(int line, int column)
    {
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
}
