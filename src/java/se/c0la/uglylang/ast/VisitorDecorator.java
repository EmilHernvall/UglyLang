package se.c0la.uglylang.ast;

public class VisitorDecorator implements Visitor
{
    private Visitor delegate;

    public VisitorDecorator(Visitor delegate)
    {
        this.delegate = delegate;
    }

    public void addFlag(Flag flag)
    {
        delegate.addFlag(flag);
    }

    public void addScopeFlag(Flag flag)
    {
        delegate.addScopeFlag(flag);
    }

    public void removeFlag(Flag flag)
    {
        delegate.removeFlag(flag);
    }

    public void removeScopeFlag(Flag flag)
    {
        delegate.removeScopeFlag(flag);
    }

    public int getCurrentAddr()
    {
        return delegate.getCurrentAddr();
    }

    // language constructs
    public void visit(Declaration node)
    {
        delegate.visit(node);
    }
    public void visit(TypeDeclNode node)
    {
        delegate.visit(node);
    }
    public void visit(FunctionDecl node)
    {
        delegate.visit(node);
    }
    public void visit(ReturnStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndFunctionStatement node)
    {
        delegate.visit(node);
    }
    public void visit(IfStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndIfStatement node)
    {
        delegate.visit(node);
    }
    public void visit(ElseIfStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndElseIfStatement node)
    {
        delegate.visit(node);
    }
    public void visit(ElseStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndElseStatement node)
    {
        delegate.visit(node);
    }
    public void visit(UnpackStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndUnpackStatement node)
    {
        delegate.visit(node);
    }
    public void visit(WhileStatement node)
    {
        delegate.visit(node);
    }
    public void visit(EndWhileStatement node)
    {
        delegate.visit(node);
    }
    public void visit(ObjectNode node)
    {
        delegate.visit(node);
    }
    public void visit(ObjectSetNode node)
    {
        delegate.visit(node);
    }
    public void visit(ObjectEndNode node)
    {
        delegate.visit(node);
    }
    public void visit(ArrayNode node)
    {
        delegate.visit(node);
    }
    public void visit(ArraySetNode node)
    {
        delegate.visit(node);
    }
    public void visit(ArrayEndNode node)
    {
        delegate.visit(node);
    }

    // assignment
    public void visit(AssignNode node)
    {
        delegate.visit(node);
    }
    public void visit(AssignDeclarationNode node)
    {
        delegate.visit(node);
    }

    // logic
    public void visit(AndNode node)
    {
        delegate.visit(node);
    }
    public void visit(OrNode node)
    {
        delegate.visit(node);
    }
    public void visit(XorNode node)
    {
        delegate.visit(node);
    }

    // arithmetic
    public void visit(AddNode node)
    {
        delegate.visit(node);
    }
    public void visit(SubNode node)
    {
        delegate.visit(node);
    }
    public void visit(MulNode node)
    {
        delegate.visit(node);
    }
    public void visit(DivNode node)
    {
        delegate.visit(node);
    }
    public void visit(ModNode node)
    {
        delegate.visit(node);
    }

    // conditionals
    public void visit(EqualNode node)
    {
        delegate.visit(node);
    }
    public void visit(NotEqualNode node)
    {
        delegate.visit(node);
    }
    public void visit(LtNode node)
    {
        delegate.visit(node);
    }
    public void visit(GtNode node)
    {
        delegate.visit(node);
    }
    public void visit(LtEqNode node)
    {
        delegate.visit(node);
    }
    public void visit(GtEqNode node)
    {
        delegate.visit(node);
    }

    // values
    public void visit(SubscriptNode node)
    {
        delegate.visit(node);
    }
    public void visit(IndexNode node)
    {
        delegate.visit(node);
    }
    public void visit(Variable node)
    {
        delegate.visit(node);
    }
    public void visit(StringConstant node)
    {
        delegate.visit(node);
    }
    public void visit(IntegerConstant node)
    {
        delegate.visit(node);
    }
    public void visit(TypeValue node)
    {
        delegate.visit(node);
    }

    public void visit(FunctionCall node)
    {
        delegate.visit(node);
    }
}
