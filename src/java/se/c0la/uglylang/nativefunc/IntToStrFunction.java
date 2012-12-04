package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.StringType;
import se.c0la.uglylang.type.StringValue;
import se.c0la.uglylang.type.IntegerType;
import se.c0la.uglylang.type.IntegerValue;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.FunctionType;

public class IntToStrFunction implements NativeFunction
{
    public IntToStrFunction()
    {
    }

    @Override
    public String getName() { return "intToStr"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        params.add(new IntegerType());

        return new FunctionType(new StringType(), params);
    }

    @Override
    public Value execute(Value... values)
    {
        IntegerValue val = (IntegerValue)values[0];
        int intValue = val.getInt();

        return new StringValue(Integer.toString(intValue));
    }
}
