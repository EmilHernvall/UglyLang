package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class SwapInstruction implements Instruction
{
    public SwapInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.SWAP; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
