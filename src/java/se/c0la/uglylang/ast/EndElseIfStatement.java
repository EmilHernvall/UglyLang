package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpInstruction;

public class EndElseIfStatement implements Node, Block
{
    private JumpInstruction jmpInst;

    public EndElseIfStatement()
    {
    }

    public void setJumpInstruction(JumpInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    public JumpInstruction getJumpInstruction() { return jmpInst; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
