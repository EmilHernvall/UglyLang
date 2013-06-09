package se.c0la.uglylang.type;

public class BooleanValue extends AbstractValue<BooleanType>
{
    public final static BooleanType TYPE = new BooleanType();
    public final static BooleanValue TRUE = new BooleanValue(true);
    public final static BooleanValue FALSE = new BooleanValue(false);

    public boolean val;

    public static BooleanValue negate(Value v)
    {
        if (v.equals(TRUE)) {
            return FALSE;
        } else {
            return TRUE;
        }
    }

    public static BooleanValue fromBool(boolean v)
    {
        if (v) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private BooleanValue(boolean val)
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
    public Value andOp(Value b)
    {
        BooleanValue bool = (BooleanValue)b;
        return new BooleanValue(bool.val && val);
    }

    @Override
    public Value orOp(Value b)
    {
        BooleanValue bool = (BooleanValue)b;
        return new BooleanValue(bool.val || val);
    }

    @Override
    public Value xorOp(Value b)
    {
        BooleanValue bool = (BooleanValue)b;
        return new BooleanValue(bool.val ^ val);
    }

    @Override
    public BooleanType getType() { return TYPE; }

    @Override
    public String toString() { return "boolean " + val; }
}
