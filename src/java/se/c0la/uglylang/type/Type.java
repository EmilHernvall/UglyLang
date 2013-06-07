package se.c0la.uglylang.type;

import java.util.Set;

public interface Type
{
    public String getName();
    public String getName(Set<Type> seenTypes);
    public boolean isCompatible(Type other);
    public boolean isCompatible(Type other, Set<Type> seenTypes);
    public boolean hasField(String name);
    public Type getField(String field);
}
