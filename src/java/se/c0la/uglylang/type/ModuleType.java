package se.c0la.uglylang.type;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import se.c0la.uglylang.Module;
import se.c0la.uglylang.Symbol;

public class ModuleType extends AbstractType
{
    private Module module;
    private Map<String, Type> parameters;

    public ModuleType(Module module)
    {
        this.module = module;

        parameters = new HashMap<String, Type>();

        Map<String, Symbol> exports = module.getExports();
        for (Symbol sym : exports.values()) {
            parameters.put(sym.getName(), sym.getType());
        }
    }

    public Map<String, Type> getParameters() { return parameters; }

    @Override
    public Type getField(String field)
    {
        return parameters.get(field);
    }

    @Override
    public boolean hasField(String name)
    {
        for (String field : parameters.keySet()) {
            if (name.equals(field)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        if (other == this) {
            return true;
        }

        return false;
    }

    @Override
    public String getName(Set<Type> seenTypes)
    {
        return module.getName();
    }

    @Override
    public String toString() { return getName(); }
}
