package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class ModInstruction implements Instruction
{
    public ModInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.MOD; }

    @Override
    public String toString() { return getOpCode().toString(); }
}
