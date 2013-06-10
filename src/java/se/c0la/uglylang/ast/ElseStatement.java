package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;
import se.c0la.uglylang.ir.JumpInstruction;

public class ElseStatement extends AbstractNode implements Node, Block
{
    private List<Node> stmts;

    private JumpOnFalseInstruction jmpInst;
    private List<JumpInstruction> jumps;

    public ElseStatement(List<Node> stmts)
    {
        this.stmts = stmts;
    }

    public void setJumpInstruction(JumpOnFalseInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    public JumpOnFalseInstruction getJumpInstruction() { return jmpInst; }

    public void setJumps(List<JumpInstruction> jumps)
    {
        this.jumps = jumps;
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endElse = new EndElseStatement(jumps);
        endElse.accept(visitor);
    }

    @Override
    public String toString()
    {
        return "else";
    }
}
