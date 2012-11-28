package se.c0la.uglylang.ast;

public class Variable extends Node
{
    private String name;

    public Variable(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}

