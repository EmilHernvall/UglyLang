package se.c0la.uglylang.type;

import se.c0la.uglylang.Module;

public class ModuleValue extends AbstractValue<ModuleType>
{
    private Module module;

    public ModuleValue(Module module)
    {
        this.module = module;
    }

    @Override
    public ModuleType getType()
    {
        return module.getType();
    }

    @Override
    public Value getField(String field)
    {
        return env.getValue(module, field);
    }
}
