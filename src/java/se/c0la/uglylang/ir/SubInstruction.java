package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class SubInstruction implements Instruction
{
    public SubInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.SUB; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
