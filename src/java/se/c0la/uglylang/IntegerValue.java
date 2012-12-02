package se.c0la.uglylang;

public class IntegerValue implements Value<IntegerType>
{
    public final static IntegerType TYPE = new IntegerType();

    public int val;

    public IntegerValue(int val)
    {
        this.val = val;
    }

    public int getInt() { return val; }

    @Override
    public Value addOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new IntegerValue(intVal.val + val);
    }

    @Override
    public Value subOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new IntegerValue(intVal.val - val);
    }

    @Override
    public Value mulOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new IntegerValue(intVal.val * val);
    }

    @Override
    public Value divOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new IntegerValue(intVal.val / val);
    }

    @Override
    public Value equalOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val == val);
    }

    @Override
    public IntegerType getType() { return TYPE; }

    @Override
    public String toString() { return "int " + val; }
}
