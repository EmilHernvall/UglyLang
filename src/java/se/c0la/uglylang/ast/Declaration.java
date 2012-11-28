package se.c0la.uglylang.ast;

public class Declaration extends Node
{
    private String type;
    private String name;

    public Declaration(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", type, name);
    }
}

