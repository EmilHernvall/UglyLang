package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class ArrayValue extends AbstractValue<ArrayType>
{
    private ArrayType type;
    private Value[] arr;

    public ArrayValue(ArrayType type, int size)
    {
        this.type = type;
        this.arr = new Value[size];
    }

    public int getSize()
    {
        return arr.length;
    }

    public void set(int idx, Value value)
    {
        arr[idx] = value;
    }

    public Value get(int idx)
    {
        return arr[idx];
    }

    @Override
    public ArrayType getType() { return type; }

    @Override
    public String toString()
    {
        return "array " + type + " of size " + arr.length;
    }
}
