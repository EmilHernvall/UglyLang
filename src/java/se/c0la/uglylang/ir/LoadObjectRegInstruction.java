package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class LoadObjectRegInstruction implements Instruction
{
    public LoadObjectRegInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.LOAD_OBJECTREG; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
