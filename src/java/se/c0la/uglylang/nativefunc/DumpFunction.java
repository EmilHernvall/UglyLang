package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.WildcardType;
import se.c0la.uglylang.Interpreter;

public class DumpFunction implements NativeFunction
{
    private Interpreter interpreter;

    public DumpFunction()
    {
    }

    @Override
    public void setInterpreter(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    @Override
    public String getName() { return "dump"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        params.add(new WildcardType());

        return new FunctionType(VoidType.TYPE, params);
    }

    @Override
    public Value execute(Value... values)
    {
        System.out.println(interpreter.dumpValue(values[0]));

        return null;
    }
}
