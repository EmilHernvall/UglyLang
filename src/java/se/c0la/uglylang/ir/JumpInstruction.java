package se.c0la.uglylang.ir;

import se.c0la.uglylang.Symbol;

public class JumpInstruction implements Instruction
{
    private String label;
    private int addr;

    public JumpInstruction(String label)
    {
        this.label = label;
        this.addr = -1;
    }

    public JumpInstruction(int addr)
    {
        this.label = null;
        this.addr = addr;
    }

    public String getLabel() { return label; }

    public void setAddr(int v) { this.addr = v; }
    public int getAddr() { return addr; }

    @Override
    public OpCode getOpCode() { return OpCode.JUMP; }

    @Override
    public String toString()
    {
        return getOpCode().toString() + " " + label + " " + addr;
    }
}
