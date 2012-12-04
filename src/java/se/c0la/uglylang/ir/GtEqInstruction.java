package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class GtEqInstruction implements Instruction
{
    public GtEqInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.GTEQ; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
