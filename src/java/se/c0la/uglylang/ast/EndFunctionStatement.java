package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.Symbol;

public class EndFunctionStatement extends Node
{
    private FunctionType type;
    private int funcAddr;
    private Map<String, Symbol> params;

    public EndFunctionStatement(FunctionType type, int funcAddr,
            Map<String, Symbol> params)
    {
        this.type = type;
        this.funcAddr = funcAddr;
        this.params = params;
    }

    public FunctionType getType() { return type; }
    public int getFuncAddr() { return funcAddr; }
    public Map<String, Symbol> getSymbolMap() { return params; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
