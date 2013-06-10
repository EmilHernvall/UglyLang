package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;

public class EndUnpackStatement extends AbstractNode implements Node, Block
{
    private JumpOnFalseInstruction jmpInst;

    public EndUnpackStatement(JumpOnFalseInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    public JumpOnFalseInstruction getJumpInstruction() { return jmpInst; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
