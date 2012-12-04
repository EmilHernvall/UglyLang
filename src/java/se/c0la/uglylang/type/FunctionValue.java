package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class FunctionValue extends AbstractValue<FunctionType>
{
    private FunctionType type;

    private int addr;
    private Map<String, Symbol> symbolMap;

    public FunctionValue(FunctionType type, int addr, Map<String, Symbol> symbolMap)
    {
        this.type = type;
        this.addr = addr;
        this.symbolMap = symbolMap;
    }

    public int getAddr() { return addr; }
    public Map<String, Symbol> getSymbolMap() { return symbolMap; }

    @Override
    public FunctionType getType() { return type; }

    @Override
    public String toString()
    {
        return "func " + type + " at " + addr;
    }
}
