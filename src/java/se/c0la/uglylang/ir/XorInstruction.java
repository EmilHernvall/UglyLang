package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class XorInstruction implements Instruction
{
    public XorInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.XOR; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
