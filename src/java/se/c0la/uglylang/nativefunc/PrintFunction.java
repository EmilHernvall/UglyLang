package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.StringType;
import se.c0la.uglylang.type.StringValue;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.FunctionType;

public class PrintFunction implements NativeFunction
{
    public PrintFunction()
    {
    }

    @Override
    public String getName() { return "print"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        params.add(new StringType());

        return new FunctionType(new VoidType(), params);
    }

    @Override
    public Value execute(Value... values)
    {
        StringValue val = (StringValue)values[0];
        System.out.println(val.getString());

        return null;
    }
}
