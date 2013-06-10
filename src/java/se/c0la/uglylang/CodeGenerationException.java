package se.c0la.uglylang;

public class CodeGenerationException extends RuntimeException
{
    private int line, column;

    public CodeGenerationException(String message, int line, int column)
    {
        super(message);
        this.line = line;
        this.column = column;
    }

    public CodeGenerationException(Exception e, int line, int column)
    {
        super(e);
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
}
