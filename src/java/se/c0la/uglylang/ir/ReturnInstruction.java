package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ReturnInstruction implements Instruction
{
    public ReturnInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.RETURN; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
