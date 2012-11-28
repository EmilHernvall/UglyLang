package se.c0la.uglylang.ast;

public class StringObject extends BaseObject
{
    private String value;

    public StringObject(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}

