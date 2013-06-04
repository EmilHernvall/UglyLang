package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ReturnCtxInstruction implements Instruction
{
    private boolean voidFunc = false;

    public ReturnCtxInstruction(boolean voidFunc)
    {
        this.voidFunc = voidFunc;
    }

    public boolean isVoidFunc() { return voidFunc; }

    @Override
    public OpCode getOpCode() { return OpCode.RETURN_CTX; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
