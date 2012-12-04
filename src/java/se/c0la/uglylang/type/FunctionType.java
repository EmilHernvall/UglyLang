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
