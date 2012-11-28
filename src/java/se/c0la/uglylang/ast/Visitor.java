package se.c0la.uglylang.ast;

public interface Visitor
{
    int getCurrentAddr();

    // language constructs
    void visit(Declaration node);
    void visit(FunctionDecl node);
    void visit(ReturnStatement node);
    void visit(EndFunctionStatement node);
    void visit(IfStatement node);
    void visit(EndIfStatement node);

    // assignment
    void visit(AssignNode node);

    // arithmetic
    void visit(AddNode node);
    void visit(SubNode node);
    void visit(MulNode node);
    void visit(DivNode node);

    // conditionals
    void visit(EqualNode node);

    // values
    void visit(Variable node);
    void visit(StringConstant node);
    void visit(IntegerConstant node);

    void visit(FunctionCall node);
}
