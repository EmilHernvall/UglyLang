package se.c0la.uglylang.ir;

public class LoadInstruction implements Instruction
{
    private String varName;

    public LoadInstruction(String varName)
    {
        this.varName = varName;
    }

    @Override
    public OpCode getOpCode() { return OpCode.LOAD; }
}
