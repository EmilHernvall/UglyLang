package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class JumpOnFalseInstruction implements Instruction
{
    private int addr;

    public JumpOnFalseInstruction(int addr)
    {
        this.addr = addr;
    }

    public void setAddr(int v) { this.addr = v; }
    public int getAddr() { return addr; }

    @Override
    public OpCode getOpCode() { return OpCode.JUMPONFALSE; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + addr;
    }
}
