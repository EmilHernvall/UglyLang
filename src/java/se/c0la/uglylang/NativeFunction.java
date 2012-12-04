package se.c0la.uglylang;

public interface NativeFunction
{
    public Value execute(Value... values);
}
