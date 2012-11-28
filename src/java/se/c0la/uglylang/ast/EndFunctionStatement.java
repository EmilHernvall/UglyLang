package se.c0la.uglylang.ast;

public class EndFunctionStatement extends Node
{
    private int funcAddr;

    public EndFunctionStatement(int funcAddr)
    {
        this.funcAddr = funcAddr;
    }

    public int getFuncAddr() { return funcAddr; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
