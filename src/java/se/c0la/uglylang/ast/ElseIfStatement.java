package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;

public class ElseIfStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;

    private JumpOnFalseInstruction jmpInst;
    private EndElseIfStatement endElseIf;

    public ElseIfStatement(Node cond, List<Node> stmts)
    {
        this.cond = cond;
        this.stmts = stmts;
    }

    public void setJumpInstruction(JumpOnFalseInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    public JumpOnFalseInstruction getJumpInstruction() { return jmpInst; }
    public EndElseIfStatement getEndElseIfStmt() { return endElseIf; }

    @Override
    public void accept(Visitor visitor)
    {
        jmpInst.setAddr(visitor.getCurrentAddr());

        cond.accept(visitor);

        //String endIfLbl = "EndIf_" + visitor.getCurrentAddr();

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        endElseIf = new EndElseIfStatement();
        endElseIf.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("else if ");
        buf.append(cond.toString());

        return buf.toString();
    }
}
