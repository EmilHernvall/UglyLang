package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class AddInstruction implements Instruction
{
    public AddInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.ADD; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
