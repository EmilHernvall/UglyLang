package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.ir.JumpOnFalseInstruction;
import se.c0la.uglylang.ir.JumpInstruction;

public class IfStatement extends AbstractNode implements Node, Block
{
    private Node cond;
    private List<Node> stmts;
    private List<ElseIfStatement> elseIfList;
    private ElseStatement elseStmt;

    private JumpOnFalseInstruction jmpInst;

    public IfStatement(Node cond,
                       List<Node> stmts,
                       List<ElseIfStatement> elseIfList,
                       ElseStatement elseStmt)
    {
        this.cond = cond;
        this.stmts = stmts;
        this.elseIfList = elseIfList;
        this.elseStmt = elseStmt;
    }

    public void setJumpInstruction(JumpOnFalseInstruction jmpInst)
    {
        this.jmpInst = jmpInst;
    }

    @Override
    public void accept(Visitor visitor)
    {
        cond.accept(visitor);

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        List<JumpInstruction> jumps = new ArrayList<JumpInstruction>();

        EndIfStatement endIf = new EndIfStatement();
        endIf.accept(visitor);
        jumps.add(endIf.getJumpInstruction());

        for (ElseIfStatement elseIfStmt : elseIfList) {
            elseIfStmt.setJumpInstruction(jmpInst);
            elseIfStmt.accept(visitor);
            EndElseIfStatement endElseIfStmt = elseIfStmt.getEndElseIfStmt();
            jumps.add(endElseIfStmt.getJumpInstruction());
            jmpInst = elseIfStmt.getJumpInstruction();
        }

        if (elseStmt == null) {
            elseStmt = new ElseStatement(new ArrayList<Node>());
        }

        elseStmt.setJumpInstruction(jmpInst);
        elseStmt.setJumps(jumps);
        elseStmt.accept(visitor);
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
