package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.StringType;
import se.c0la.uglylang.type.StringValue;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.Interpreter;

public class PrintFunction implements NativeFunction
{
    public PrintFunction()
    {
    }

    @Override
    public void setInterpreter(Interpreter interpreter)
    {
    }

    @Override
    public String getName() { return "print"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        params.add(new StringType());

        return new FunctionType(VoidType.TYPE, params);
    }

    @Override
    public Value execute(Value... values)
    {
        StringValue val = (StringValue)values[0];
        System.out.println(val.getString());

        return null;
    }
}
