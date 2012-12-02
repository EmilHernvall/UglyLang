package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class EqualInstruction implements Instruction
{
    public EqualInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.EQUAL; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
