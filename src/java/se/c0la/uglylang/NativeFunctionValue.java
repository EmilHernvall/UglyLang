package se.c0la.uglylang;

import java.util.*;

public class NativeFunctionValue extends AbstractValue<FunctionType>
{
    private FunctionType type;
    private NativeFunction func;

    public NativeFunctionValue(FunctionType type, NativeFunction func)
    {
        this.type = type;
        this.func = func;
    }

    public NativeFunction getFunction() { return func; }

    @Override
    public FunctionType getType() { return type; }

    @Override
    public String toString()
    {
        return "native func " + type;
    }
}

