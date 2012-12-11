package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Value;

public class TupleGetInstruction implements Instruction
{
    public TupleGetInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.TUPLE_GET; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
