package se.c0la.uglylang.type;

import se.c0la.uglylang.ExecutionEnvironment;

public abstract class AbstractValue<E extends Type> implements Value<E>
{
    protected ExecutionEnvironment env;

    public abstract E getType();

    @Override
    public void setExecutionEnvironment(ExecutionEnvironment env)
    {
        this.env = env;
    }

    @Override
    public Value getField(String field)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value addOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value subOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value mulOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value divOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value modOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value equalOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value notEqualOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value ltOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value gtOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value ltEqOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value gtEqOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value andOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value orOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value xorOp(Value b)
    {
        throw new UnsupportedOperationException();
    }
}
