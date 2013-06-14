package se.c0la.uglylang.type;

public class ReturnAddressValue extends AbstractValue<IntegerType>
{
    public final static IntegerType TYPE = new IntegerType();

    public int addr;
    public boolean exitModule = false;

    public ReturnAddressValue(int addr, boolean exitModule)
    {
        this.addr = addr;
        this.exitModule = exitModule;
    }

    public int getAddr() { return addr; }

    public boolean getExitModule() { return exitModule; }

    @Override
    public IntegerType getType() { return TYPE; }

    @Override
    public String toString() { return "return addr " + addr; }
}
