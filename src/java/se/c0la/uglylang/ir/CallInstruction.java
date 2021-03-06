package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class CallInstruction implements Instruction
{
    public CallInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.CALL; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
