package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ObjectType;
import se.c0la.uglylang.type.TypeException;

public class SubscriptNode extends AbstractNode implements Expression
{
    private Expression var;
    private String key;
    private int seq;

    public SubscriptNode(Expression var, String key, int seq)
    {
        this.var = var;
        this.key = key;
        this.seq = seq;
    }

    public Expression getVar() { return var; }
    public String getKey() { return key; }
    public int getSeq() { return seq; }

    public Type getType()
    throws TypeException
    {
        return var.inferType();
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        Type objectType = getType();
        Type fieldType = objectType.getField(key);
        if (fieldType == null) {
            throw new TypeException(objectType.getName() + " does not have " +
                    "a field called " + key + ".");
        }

        return fieldType;
    }

    @Override
    public void accept(Visitor visitor)
    {
        var.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return this.var + "." + this.key;
    }
}
