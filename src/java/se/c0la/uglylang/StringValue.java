package se.c0la.uglylang;

public class StringValue implements Value<StringType>
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
    public Value equalOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringType getType() { return TYPE; }

    @Override
    public String toString() { return "str " + str; }
}
