package se.c0la.uglylang.type;

public class PlaceholderType implements Type
{
    private String name;

    public PlaceholderType(String name)
    {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

    public String toString() { return "Placeholder:" + name; }
}
