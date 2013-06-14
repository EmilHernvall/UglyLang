package se.c0la.uglylang;

import java.util.Map;
import java.util.List;

import se.c0la.uglylang.ir.Instruction;
import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.CompoundType;
import se.c0la.uglylang.type.ModuleType;
import se.c0la.uglylang.type.Value;

public class Module
{
    private String name;

    private List<Instruction> instructions;
    private Map<String, Type> types;
    private Map<String, CompoundType> compoundTypes;
    private Map<String, Symbol> exports;
    private Map<Module, Symbol> imports;
    private Map<String, Module> deps;
    private Map<Symbol, Value> predef;

    private ModuleType type;

    public Module()
    {
    }

    public void setName(String v) { this.name = v; }
    public String getName() { return name; }

    public void setInstructions(List<Instruction> v) { this.instructions = v; }
    public List<Instruction> getInstructions() { return instructions; }

    public void setTypes(Map<String, Type> v) { this.types = v; }
    public Map<String, Type> getTypes() { return types; }

    public void setCompoundTypes(Map<String, CompoundType> v) { this.compoundTypes = v; }
    public Map<String, CompoundType> getCompoundTypes() { return compoundTypes; }

    public Map<String, Symbol> getExports() { return exports; }
    public void setExports(Map<String, Symbol> v)
    {
        this.exports = v;

        type = new ModuleType(this);
    }

    public void setImports(Map<Module, Symbol> v) { this.imports = v; }
    public Map<Module, Symbol> getImports() { return imports; }

    public void setPredefinedSymbols(Map<Symbol, Value> v) { this.predef = v; }
    public Map<Symbol, Value> getPredefinedSymbols() { return predef; }

    public void setDependencies(Map<String, Module> v) { this.deps = v; }
    public Map<String, Module> getDependencies() { return deps; }

    public ModuleType getType() { return type; }
}
