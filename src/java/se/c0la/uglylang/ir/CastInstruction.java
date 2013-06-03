package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;
import se.c0la.uglylang.type.Value;

public class CastInstruction implements Instruction
{
    private Symbol from;
    private Symbol to;

    public CastInstruction(Symbol from, Symbol to)
    {
        this.from = from;
        this.to = to;
    }

    public Symbol getFrom() { return from; }
    public Symbol getTo() { return to; }

    @Override
    public OpCode getOpCode() { return OpCode.CAST; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " +
                from.getName().toString() + ":" + from.getType().getName() + " " +
                to.getName().toString() + ":" + to.getType().getName();
    }
}
