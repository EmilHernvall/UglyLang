package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class StoreInstruction implements Instruction
{
    private Symbol sym;

    public StoreInstruction(Symbol sym)
    {
        this.sym = sym;
    }

    public Symbol getSymbol() { return sym; }

    @Override
    public OpCode getOpCode() { return OpCode.STORE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + sym.getName();
    }
}
