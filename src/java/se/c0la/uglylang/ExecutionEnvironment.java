package se.c0la.uglylang;

import java.util.Map;
import java.util.HashMap;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.FunctionValue;
import se.c0la.uglylang.type.ObjectValue;
import se.c0la.uglylang.type.ModuleValue;

public class ExecutionEnvironment
{
    private Map<Module, Interpreter> interpreters;

    public ExecutionEnvironment()
    {
        interpreters = new HashMap<Module, Interpreter>();
    }

    public Value getValue(Module module, String field)
    {
        Map<String, Symbol> exports = module.getExports();
        Symbol sym = exports.get(field);

        Interpreter interpreter = interpreters.get(module);
        Interpreter.Scope scope = interpreter.getScope();
        return scope.get(sym);
    }

    public Value call(FunctionValue func, Value[] args)
    {
        Module module = func.getModule();
        Interpreter interpreter = interpreters.get(module);
        return interpreter.call(func, args);
    }

    public Value callCtx(Value value, FunctionValue func, Value[] args)
    {
        Module module = func.getModule();
        Interpreter interpreter = interpreters.get(module);
        if (value instanceof ObjectValue) {
            return interpreter.callCtx((ObjectValue)value, func, args);
        } else {
            return call(func, args);
        }
    }

    public void run(Module module)
    {
        Interpreter interpreter = new Interpreter(this, module);
        interpreters.put(module, interpreter);

        Interpreter.Scope scope = interpreter.getScope();
        Map<Module, Symbol> importSymbols =  module.getImports();

        Map<String, Module> deps = module.getDependencies();
        for (Module dep : deps.values()) {
            run(dep);

            Symbol targetSym = importSymbols.get(dep);
            Value value = new ModuleValue(dep);
            value.setExecutionEnvironment(this);
            scope.set(targetSym, value);
        }

        try {
            interpreter.run();
        }
        catch (Exception e) {
            System.out.println("Module " + module.getName());
            e.printStackTrace();

            interpreter.dumpStack();
        }
    }
}
