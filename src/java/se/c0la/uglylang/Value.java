package se.c0la.uglylang;

public interface Value<E extends Type>
{
    public E getType();

    public Value addOp(Value b);
    public Value subOp(Value b);
    public Value mulOp(Value b);
    public Value divOp(Value b);

    public Value equalOp(Value b);
}
