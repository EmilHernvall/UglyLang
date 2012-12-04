package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class AndInstruction implements Instruction
{
    public AndInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.AND; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
