package se.c0la.uglylang;

import java.util.Map;
import java.util.HashMap;

import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.FunctionValue;

public class ExecutionEnvironment
{
    private Map<Module, Interpreter> interpreters;

    public ExecutionEnvironment()
    {
        interpreters = new HashMap<Module, Interpreter>();
    }

    public Value call(FunctionValue func, Value[] args)
    {
        return null;
    }

    public Interpreter run(Module module)
    {
        Interpreter interpreter = new Interpreter(this, module);

        Map<String, Module> deps = module.getDependencies();
        for (Module dep : deps.values()) {
            Interpreter moduleInterpreter = run(dep);
            interpreters.put(dep, moduleInterpreter);

            Interpreter.Scope scope = moduleInterpreter.getScope();

            Map<String, Symbol> exports = dep.getExports();
            for (Symbol symbol : exports.values()) {
                Value value = scope.get(symbol);
            }
        }

        interpreter.run();

        return interpreter;
    }
}
