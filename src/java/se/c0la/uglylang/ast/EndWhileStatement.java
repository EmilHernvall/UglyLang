package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;

public class EndWhileStatement extends AbstractNode implements Node, Block
{
    private JumpOnFalseInstruction jmpInst;
    private int condAddr;

    public EndWhileStatement(JumpOnFalseInstruction jmpInst, int condAddr)
    {
        this.jmpInst = jmpInst;
        this.condAddr = condAddr;
    }

    public JumpOnFalseInstruction getJumpInstruction() { return jmpInst; }
    public int getCondAddr() { return condAddr; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
