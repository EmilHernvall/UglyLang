package se.c0la.uglylang;

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
    public StringType getType() { return TYPE; }

    @Override
    public String toString() { return "str " + str; }
}
