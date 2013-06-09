package se.c0la.uglylang.ast;

public interface Visitor
{
    public enum Flag {
        OBJECT,
        ASSIGN,
        CALL;
    };

    public void addFlag(Flag flag);
    public void addScopeFlag(Flag flag);
    public void removeFlag(Flag flag);
    public void removeScopeFlag(Flag flag);

    int getCurrentAddr();

    // language constructs
    void visit(Declaration node);
    void visit(TypeDeclNode node);
    void visit(FunctionDecl node);
    void visit(ReturnStatement node);
    void visit(EndFunctionStatement node);
    void visit(IfStatement node);
    void visit(EndIfStatement node);
    void visit(ElseIfStatement node);
    void visit(EndElseIfStatement node);
    void visit(ElseStatement node);
    void visit(EndElseStatement node);
    void visit(UnpackStatement node);
    void visit(EndUnpackStatement node);
    void visit(WhileStatement node);
    void visit(EndWhileStatement node);
    void visit(ObjectNode node);
    void visit(ObjectSetNode node);
    void visit(ObjectEndNode node);
    void visit(ArrayNode node);
    void visit(ArraySetNode node);
    void visit(ArrayEndNode node);

    // assignment
    void visit(AssignNode node);
    void visit(AssignDeclarationNode node);

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
    void visit(TypeValue node);

    void visit(FunctionCall node);
}
