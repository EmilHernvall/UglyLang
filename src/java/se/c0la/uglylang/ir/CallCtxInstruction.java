package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class CallCtxInstruction implements Instruction
{
    public CallCtxInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.CALL_CTX; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
