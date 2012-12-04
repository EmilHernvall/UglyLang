package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Value;

public class PushInstruction implements Instruction
{
    private Value value;

    public PushInstruction(Value value)
    {
        this.value = value;
    }

    public Value getValue() { return value; }

    @Override
    public OpCode getOpCode() { return OpCode.PUSH; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + value.toString();
    }
}
