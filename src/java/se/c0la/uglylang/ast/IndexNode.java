package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ArrayType;
import se.c0la.uglylang.type.TypeException;

public class IndexNode extends AbstractNode implements Expression
{
    private Expression var;
    private Expression index;
    private int seq;

    public IndexNode(Expression var, Expression index, int seq)
    {
        this.var = var;
        this.index = index;
        this.seq = seq;
    }

    public Expression getVariable() { return var; }
    public Expression getIndex() { return index; }
    public int getSeq() { return seq; }

    @Override
    public Type inferType()
    throws TypeException
    {
        Type type = var.inferType();
        if (type instanceof ArrayType) {
            ArrayType arrType = (ArrayType)type;
            return arrType.getType();
        }

        throw new TypeException(type.getName() + " cannot be indexed.");
    }

    @Override
    public void accept(Visitor visitor)
    {
        var.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.var + "[" + this.index + "]";
    }
}
