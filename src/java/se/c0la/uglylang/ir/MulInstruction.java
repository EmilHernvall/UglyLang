package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class MulInstruction implements Instruction
{
    public MulInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.MUL; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
