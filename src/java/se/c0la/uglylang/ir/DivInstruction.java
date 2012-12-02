package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class DivInstruction implements Instruction
{
    public DivInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.DIV; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
