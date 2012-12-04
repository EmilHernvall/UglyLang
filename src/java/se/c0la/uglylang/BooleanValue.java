package se.c0la.uglylang;

public class BooleanValue extends AbstractValue<BooleanType>
{
    public final static BooleanType TYPE = new BooleanType();

    public boolean val;

    public BooleanValue(boolean val)
    {
        this.val = val;
    }

    public boolean getBoolean() { return val; }

    @Override
    public Value equalOp(Value b)
    {
        BooleanValue bool = (BooleanValue)b;
        return new BooleanValue(bool.val == val);
    }

    @Override
    public BooleanType getType() { return TYPE; }

    @Override
    public String toString() { return "boolean " + val; }
}
