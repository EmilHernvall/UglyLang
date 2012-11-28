package se.c0la.uglylang.ast;

public abstract class Node
{
    protected static String indent(String data)
    {
        StringBuilder b = new StringBuilder();
        for (String l : data.split("\n")) {
            b.append("\t");
            b.append(l);
            b.append("\n");
        }

        return b.toString();
    }
}

