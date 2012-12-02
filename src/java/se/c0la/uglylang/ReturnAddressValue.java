package se.c0la.uglylang;

public class ReturnAddressValue implements Value<IntegerType>
{
    public final static IntegerType TYPE = new IntegerType();

    public int addr;

    public ReturnAddressValue(int addr)
    {
        this.addr = addr;
    }

    public int getAddr() { return addr; }

    @Override
    public Value addOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value subOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value mulOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value divOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value equalOp(Value b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntegerType getType() { return TYPE; }

    @Override
    public String toString() { return "return addr " + addr; }
}
