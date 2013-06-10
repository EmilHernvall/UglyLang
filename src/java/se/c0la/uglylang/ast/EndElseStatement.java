package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpInstruction;

public class EndElseStatement extends AbstractNode implements Node, Block
{
    private List<JumpInstruction> jumps;

    public EndElseStatement(List<JumpInstruction> jumps)
    {
        this.jumps = jumps;
    }

    public List<JumpInstruction> getJumps() { return jumps; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }
}
