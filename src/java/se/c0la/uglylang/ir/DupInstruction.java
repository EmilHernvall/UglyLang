package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class DupInstruction implements Instruction
{
    public DupInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.DUP; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
