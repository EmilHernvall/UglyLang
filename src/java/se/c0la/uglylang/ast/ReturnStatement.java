package se.c0la.uglylang.ast;

public class ReturnStatement extends AbstractNode implements Node
{
    private Node expression;

    public ReturnStatement(Node expression)
    {
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor)
    {
        expression.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "return " + expression;
    }
}
