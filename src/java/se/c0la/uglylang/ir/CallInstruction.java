package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class CallInstruction implements Instruction
{
    private Symbol function;

    public CallInstruction(Symbol function)
    {
        this.function = function;
    }

    public Symbol getFunctionSymbol() { return function; }

    @Override
    public OpCode getOpCode() { return OpCode.CALL; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + function.getName();
    }
}
