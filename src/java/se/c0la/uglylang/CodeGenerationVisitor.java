package se.c0la.uglylang;

import se.c0la.uglylang.ast.*;

public class CodeGenerationVisitor implements Visitor
{
    private int addr = 0;

    public CodeGenerationVisitor()
    {
    }

    @Override
    public int getCurrentAddr()
    {
        return addr;
    }

    @Override
    public void visit(Declaration node)
    {
        System.out.printf("%d PUSH Declaration: type=%s, name=%s\n",
                addr++, node.getType(), node.getName());
    }

    @Override
    public void visit(FunctionDecl node)
    {
        System.out.println();
        System.out.println("Entering new scope");
        System.out.printf("%d Function: type=%s\n", addr++, node.getType());
    }

    @Override
    public void visit(ReturnStatement node)
    {
        System.out.printf("%d JUMP Return\n", addr++);
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        System.out.printf("%d PUSH EndFunctionStatement: funcAddr=%d\n",
                addr++, node.getFuncAddr());
        System.out.println("Leaving scope");
        System.out.println();
    }

    @Override
    public void visit(IfStatement node)
    {
        System.out.printf("%d POP JumpOnFalse %s\n", addr, "EndIf_" + addr);
        addr++;
    }

    @Override
    public void visit(EndIfStatement node)
    {
        System.out.printf("%d LABEL :%s\n", addr++, node.getLabel());
    }

    @Override
    public void visit(AssignNode node)
    {
        System.out.printf("%d POP2 Assign\n", addr++);
    }

    @Override
    public void visit(AddNode node)
    {
        System.out.printf("%d POP2 Add\n", addr++);
    }

    @Override
    public void visit(SubNode node)
    {
        System.out.printf("%d POP2 Sub\n", addr++);
    }

    @Override
    public void visit(MulNode node)
    {
        System.out.printf("%d POP2 Mul\n", addr++);
    }

    @Override
    public void visit(DivNode node)
    {
        System.out.printf("%d POP2 Div\n", addr++);
    }

    @Override
    public void visit(EqualNode node)
    {
        System.out.printf("%d POP2 Equal\n", addr++);
    }

    @Override
    public void visit(Variable node)
    {
        System.out.printf("%d PUSH Variable: name=%s\n", addr++, node.getName());
    }

    @Override
    public void visit(StringConstant node)
    {
        System.out.printf("%d PUSH String: value=%s\n", addr++, node.getValue());
    }

    @Override
    public void visit(IntegerConstant node)
    {
        System.out.printf("%d PUSH Integer: value=%d\n", addr++, node.getValue());
    }

    @Override
    public void visit(FunctionCall node)
    {
        System.out.printf("%d %dPOP CALL PUSH FunctionCall: function=%s\n", addr++,
                node.getParamCount(), node.getFunctionName());
    }
}
