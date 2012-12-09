package se.c0la.uglylang.type;

import java.util.List;

public class FunctionType implements Type
{
    private Type returnType;
    private List<Type> parameters;

    public FunctionType(Type returnType, List<Type> parameters)
    {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public Type getReturnType() { return returnType; }
    public List<Type> getParameters() { return parameters; }

    @Override
    public boolean isCompatible(Type other)
    {
        if (!(other instanceof FunctionType)) {
            return false;
        }

        FunctionType otherFunc = (FunctionType)other;

        if (!returnType.isCompatible(otherFunc.returnType)) {
            return false;
        }

        if (parameters.size() != otherFunc.parameters.size()) {
            return false;
        }

        for (int i = 0; i < parameters.size(); i++) {
            Type a = parameters.get(i);
            Type b = otherFunc.parameters.get(i);
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
        buf.append(returnType.getName());
        buf.append(")");
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
