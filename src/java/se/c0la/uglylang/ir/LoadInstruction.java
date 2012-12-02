package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class LoadInstruction implements Instruction
{
    private Symbol symbol;

    public LoadInstruction(Symbol symbol)
    {
        this.symbol = symbol;
    }

    public Symbol getSymbol() { return symbol; }

    @Override
    public OpCode getOpCode() { return OpCode.LOAD; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + symbol.getName();
    }
}
