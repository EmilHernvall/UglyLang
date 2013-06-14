package se.c0la.uglylang.type;

import se.c0la.uglylang.ExecutionEnvironment;

public interface Value<E extends Type>
{
    public E getType();

    public void setExecutionEnvironment(ExecutionEnvironment env);

    public Value getField(String field);

    public Value addOp(Value b);
    public Value subOp(Value b);
    public Value mulOp(Value b);
    public Value divOp(Value b);
    public Value modOp(Value b);

    public Value equalOp(Value b);
    public Value notEqualOp(Value b);
    public Value ltOp(Value b);
    public Value gtOp(Value b);
    public Value ltEqOp(Value b);
    public Value gtEqOp(Value b);

    public Value andOp(Value b);
    public Value orOp(Value b);
    public Value xorOp(Value b);
}
