package se.c0la.uglylang.type;

import java.util.*;

import se.c0la.uglylang.Symbol;

public class TupleValue extends AbstractValue<TupleType>
{
    private TupleType type;
    private Value[] values;

    public TupleValue(TupleType type)
    {
        this.type = type;
        this.values = new Value[type.getParameters().size()];
    }

    public void setValue(int idx, Value v) { values[idx] = v; }
    public Value getValue(int idx) { return values[idx]; }

    @Override
    public TupleType getType() { return type; }

    @Override
    public String toString()
    {
        return "Tuple " + type.getName();
    }
}
