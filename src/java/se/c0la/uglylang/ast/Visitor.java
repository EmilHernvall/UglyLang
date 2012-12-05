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
    void visit(TupleNode node);
    void visit(NamedTupleNode node);
    void visit(ArrayNode node);
    void visit(ArraySetNode node);
    void visit(ArrayEndNode node);

    // assignment
    void visit(AssignNode node);

    // logic
    void visit(AndNode node);
    void visit(OrNode node);
    void visit(XorNode node);

    // arithmetic
    void visit(AddNode node);
    void visit(SubNode node);
    void visit(MulNode node);
    void visit(DivNode node);
    void visit(ModNode node);

    // conditionals
    void visit(EqualNode node);
    void visit(NotEqualNode node);
    void visit(LtNode node);
    void visit(GtNode node);
    void visit(LtEqNode node);
    void visit(GtEqNode node);

    // values
    void visit(SubscriptNode node);
    void visit(IndexNode node);
    void visit(Variable node);
    void visit(StringConstant node);
    void visit(IntegerConstant node);

    void visit(FunctionCall node);
}
