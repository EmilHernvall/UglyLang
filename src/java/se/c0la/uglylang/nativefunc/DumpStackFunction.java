package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.WildcardType;
import se.c0la.uglylang.Interpreter;

public class DumpStackFunction implements NativeFunction
{
    private Interpreter interpreter;

    public DumpStackFunction(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    @Override
    public String getName() { return "dumpStack"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        return new FunctionType(new VoidType(), params);
    }

    @Override
    public Value execute(Value... values)
    {
        interpreter.dumpStack();
        return null;
    }
}
