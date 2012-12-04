package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class GtInstruction implements Instruction
{
    public GtInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.GT; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
