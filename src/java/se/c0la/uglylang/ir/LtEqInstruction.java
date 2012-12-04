package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class LtEqInstruction implements Instruction
{
    public LtEqInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.LTEQ; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
