package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class OrInstruction implements Instruction
{
    public OrInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.OR; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
