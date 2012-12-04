package se.c0la.uglylang.nativefunc;

import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.Value;

public interface NativeFunction
{
    public String getName();
    public FunctionType getType();
    public Value execute(Value... values);
}
