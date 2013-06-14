package se.c0la.uglylang.nativefunc;

import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.VoidType;
import se.c0la.uglylang.type.WildcardType;
import se.c0la.uglylang.Interpreter;

public class DumpObjectRegFunction implements NativeFunction
{
    private Interpreter interpreter;

    public DumpObjectRegFunction()
    {
    }

    @Override
    public void setInterpreter(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    @Override
    public String getName() { return "dumpObjectReg"; }

    @Override
    public FunctionType getType()
    {
        List<Type> params = new ArrayList<Type>();
        return new FunctionType(VoidType.TYPE, params);
    }

    @Override
    public Value execute(Value... values)
    {
        System.out.println("objectReg: " + interpreter.dumpObjectReg());
        return null;
    }
}
