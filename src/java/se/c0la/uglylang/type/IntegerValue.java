package se.c0la.uglylang.type;

public class IntegerValue extends AbstractValue<IntegerType>
{
    public final static IntegerType TYPE = new IntegerType();

    public int val;

    public IntegerValue(int val)
    {
        this.val = val;
    }

    public int getInt() { return val; }

    @Override
    public Value getField(String field)
    {
        if ("str".equals(field)) {
            return new StringValue(String.valueOf(val));
        }

        throw new UnsupportedOperationException();
    }

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
    public Value modOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new IntegerValue(intVal.val % val);
    }

    @Override
    public Value equalOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val == val);
    }

    @Override
    public Value ltOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val < val);
    }

    @Override
    public Value ltEqOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val <= val);
    }

    @Override
    public Value gtOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val > val);
    }

    @Override
    public Value gtEqOp(Value b)
    {
        IntegerValue intVal = (IntegerValue)b;
        return new BooleanValue(intVal.val >= val);
    }

    @Override
    public IntegerType getType() { return TYPE; }

    @Override
    public String toString() { return "int " + val; }
}
