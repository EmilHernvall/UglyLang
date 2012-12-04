package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class LtInstruction implements Instruction
{
    public LtInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.LT; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
