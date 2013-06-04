package se.c0la.uglylang;

import se.c0la.uglylang.type.Type;

public class Symbol
{
    private Type type;
    private String name;
    private boolean nTupleFld;

    public Symbol(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Symbol(Type type, String name, boolean nTupleFld)
    {
        this.type = type;
        this.name = name;
        this.nTupleFld = nTupleFld;
    }

    public void setType(Type v) { this.type = v; }
    public Type getType() { return type; }
    public String getName() { return name; }
    public boolean isObjectField() { return nTupleFld; }

    public Symbol copy()
    {
        return new Symbol(type, name);
    }
}
