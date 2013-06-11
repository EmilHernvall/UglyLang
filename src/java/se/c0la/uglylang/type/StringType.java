package se.c0la.uglylang.type;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import se.c0la.uglylang.nativefunc.NativeFunction;

public class StringType extends AbstractType
{
    public static class Compare implements NativeFunction
    {
        public static final FunctionType TYPE;

        static {
            List<Type> params = new ArrayList<Type>();
            params.add(new StringType());
            TYPE = new FunctionType(new IntegerType(), params);
        }

        private StringValue value;

        public Compare(StringValue value)
        {
            this.value = value;
        }

        @Override
        public String getName() { return "compare"; }

        @Override
        public FunctionType getType()
        {
            return TYPE;
        }

        @Override
        public Value execute(Value... values)
        {
            StringValue other = (StringValue)values[0];
            String a = value.getString();
            String b = other.getString();

            return new IntegerValue(a.compareTo(b));
        }
    }

    @Override
    public boolean hasField(String name)
    {
        return "compare".equals(name);
    }

    @Override
    public Type getField(String field)
    {
        if ("compare".equals(field)) {
            return Compare.TYPE;
        }

        return null;
    }

    @Override
    public boolean isCompatible(Type other, Set<Type> seenTypes)
    {
        return other instanceof StringType;
    }

    @Override
    public String getName(Set<Type> seenTypes) { return "string"; }
}
