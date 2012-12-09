package se.c0la.uglylang.type;

import java.util.List;

public class TupleType implements Type
{
    private List<Type> parameters;

    public TupleType(List<Type> parameters)
    {
        this.parameters = parameters;
    }

    public List<Type> getParameters() { return parameters; }

    @Override
    public boolean isCompatible(Type other)
    {
        if (!(other instanceof TupleType)) {
            return false;
        }

        TupleType otherTuple = (TupleType)other;
        if (parameters.size() != otherTuple.parameters.size()) {
            return false;
        }

        for (int i = 0; i < parameters.size(); i++) {
            Type a = parameters.get(i);
            Type b = otherTuple.parameters.get(i);
            if (!a.isCompatible(b)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        String delim = "";
        for (Type param : parameters) {
            buf.append(delim);
            buf.append(param.getName());
            delim = ",";
        }
        buf.append(")");

        return buf.toString();
    }

    @Override
    public String toString() { return getName(); }
}
