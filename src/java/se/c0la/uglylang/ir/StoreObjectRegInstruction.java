package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class StoreObjectRegInstruction implements Instruction
{
    public StoreObjectRegInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.STORE_OBJECTREG; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
