package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class NotEqualInstruction implements Instruction
{
    public NotEqualInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.NOTEQUAL; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
