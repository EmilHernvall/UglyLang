package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ReturnInstruction implements Instruction
{
    private boolean voidFunc = false;

    public ReturnInstruction(boolean voidFunc)
    {
        this.voidFunc = voidFunc;
    }

    public boolean isVoidFunc() { return voidFunc; }

    @Override
    public OpCode getOpCode() { return OpCode.RETURN; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
