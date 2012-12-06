package se.c0la.uglylang.ast;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.TypeException;

public interface Expression extends Node
{
    public Type inferType() throws TypeException;
}
