package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;

public class WhileStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;

    private JumpOnFalseInstruction jmpInst;

    public WhileStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    public void setJumpInstruction(JumpOnFalseInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    public JumpOnFalseInstruction getJumpInstruction() { return jmpInst; }

    @Override
    public void accept(Visitor visitor)
    {
        int condAddr = visitor.getCurrentAddr();

        cond.accept(visitor);

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endIf = new EndWhileStatement(jmpInst, condAddr);
        endIf.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("if ");
        buf.append(cond.toString());

        return buf.toString();
    }
}
