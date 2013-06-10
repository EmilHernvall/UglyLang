package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.TypeException;

public class FunctionCall extends AbstractNode implements Expression
{
    private Expression var;
    private List<Expression> params;

    public FunctionCall(Expression var, List<Expression> params)
    {
        this.var = var;
        this.params = params;
    }

    public int getParamCount() { return params.size(); }

    public boolean isSubscriptCall()
    {
        return var instanceof SubscriptNode;
    }

    public FunctionType inferActualType()
    throws TypeException
    {
        List<Type> sig = new ArrayList<Type>();
        for (Expression expr : params) {
            sig.add(expr.inferType());
        }
        return new FunctionType(inferType(), sig);
    }

    public FunctionType inferExpectedType()
    throws TypeException
    {
        Type type = var.inferType();
        if (!(type instanceof FunctionType)) {
            throw new TypeException("Call to non function of type " + type);
        }

        return (FunctionType)type;
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        Type type = var.inferType();
        if (!(type instanceof FunctionType)) {
            throw new TypeException("Call to non function of type " + type);
        }

        FunctionType funcType = (FunctionType)type;
        return funcType.getReturnType();
    }

    @Override
    public void accept(Visitor visitor)
    {
        for (Node node : params) {
            node.accept(visitor);
        }

        visitor.addFlag(Visitor.Flag.CALL);
        var.accept(visitor);
        visitor.visit(this);
        visitor.removeFlag(Visitor.Flag.CALL);
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(var.toString());
        buffer.append("(");

        String delim = "";
        for (Node node : params) {
            buffer.append(delim);
            buffer.append(node.toString());
            delim = ", ";
        }
        buffer.append(")");

        return buffer.toString();
    }
}
