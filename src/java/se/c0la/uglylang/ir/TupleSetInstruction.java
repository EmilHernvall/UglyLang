package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

/**
 * Pops three values of the stack in the following order:
 * 1. index
 * 2. value
 * 3. tuple
 *
 * The index position of tuple is set to value. The tuple is
 * pushed back on the stack.
 **/
public class TupleSetInstruction implements Instruction
{
    public TupleSetInstruction()
    {
    }

    @Override
    public OpCode getOpCode() { return OpCode.TUPLE_SET; }

    @Override
    public String toString()
    {
        return getOpCode().toString();
    }
}
