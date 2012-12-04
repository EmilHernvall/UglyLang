package se.c0la.uglylang;

public class ReturnAddressValue extends AbstractValue<IntegerType>
{
    public final static IntegerType TYPE = new IntegerType();

    public int addr;

    public ReturnAddressValue(int addr)
    {
        this.addr = addr;
    }

    public int getAddr() { return addr; }

    @Override
    public IntegerType getType() { return TYPE; }

    @Override
    public String toString() { return "return addr " + addr; }
}
