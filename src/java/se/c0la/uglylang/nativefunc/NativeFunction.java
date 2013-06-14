package se.c0la.uglylang.nativefunc;

import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.Interpreter;

public interface NativeFunction
{
    public void setInterpreter(Interpreter interpreter);
    public String getName();
    public FunctionType getType();
    public Value execute(Value... values);
}
