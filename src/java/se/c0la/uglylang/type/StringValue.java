package se.c0la.uglylang.type;

public class StringValue extends AbstractValue<StringType>
{
    public final static StringType TYPE = new StringType();

    public String str;

    public StringValue(String str)
    {
        this.str = str;
    }

    public String getString() { return str; }

    @Override
    public Value addOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new StringValue(val.str + str);
    }

    @Override
    public Value equalOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new BooleanValue(str.equals(val.str));
    }

    @Override
    public Value gtOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new BooleanValue(str.compareTo(val.str) > 0);
    }

    @Override
    public Value gtEqOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new BooleanValue(str.compareTo(val.str) >= 0);
    }

    @Override
    public Value ltOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new BooleanValue(str.compareTo(val.str) < 0);
    }

    @Override
    public Value ltEqOp(Value b)
    {
        StringValue val = (StringValue)b;
        return new BooleanValue(str.compareTo(val.str) <= 0);
    }

    @Override
    public StringType getType() { return TYPE; }

    @Override
    public String toString() { return "\"" + str + "\""; }

    @Override
    public int hashCode() { return str.hashCode(); }
}
