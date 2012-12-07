package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.NamedTupleType;
import se.c0la.uglylang.type.TypeException;

public class SubscriptNode implements Expression
{
    private Expression var;
    private String key;
    private boolean assignTarget;

    public SubscriptNode(Expression var, String key, boolean assignTarget)
    {
        this.var = var;
        this.key = key;
        this.assignTarget = assignTarget;
    }

    public Expression getVar() { return var; }
    public String getKey() { return key; }
    public boolean isAssignTarget() { return assignTarget; }

    public NamedTupleType getType()
    throws TypeException
    {
        Type exprType = var.inferType();
        if (!(exprType instanceof NamedTupleType)) {
            throw new RuntimeException("Only named tuples can be subscripted.");
        }

        return (NamedTupleType)exprType;
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        NamedTupleType namedTupleType = getType();
        Type fieldType = namedTupleType.getParameters().get(key);
        if (fieldType == null) {
            throw new TypeException(namedTupleType.getName() + " does not have " +
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
