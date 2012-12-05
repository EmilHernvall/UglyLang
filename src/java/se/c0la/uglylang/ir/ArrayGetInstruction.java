package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Value;

public class ArrayGetInstruction implements Instruction
{
    public ArrayGetInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.ARRAY_GET; }

    @Override
    public String toString()
    {
        return null;
    }
}
