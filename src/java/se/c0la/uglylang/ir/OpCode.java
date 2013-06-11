package se.c0la.uglylang.ir;

public enum OpCode
{
    CALL(0),
    CALL_CTX(0),
    RETURN(1), // boolean void
    RETURN_CTX(1), // boolean void
    LOAD(1), // symbol
    LOAD_OBJECTREG(0),
    STORE(1), // symbol
    STORE_OBJECTREG(0),
    PUSH(1), // value
    CAST(2), // symbol symbol
    SWAP(0),
    DUP(0),

    ARRAY_ALLOCATE(1), // type
    ARRAY_GET(0),
    ARRAY_SET(0),
    ARRAY_SET2(0),

    OBJECT_ALLOCATE(1), // type
    OBJECT_GET(1), // string field
    OBJECT_SET(1), // string field

    JUMP(1), // addr
    JUMPONFALSE(1), // addr

    ADD(0),
    SUB(0),
    MUL(0),
    DIV(0),
    MOD(0),

    AND(0),
    OR(0),
    XOR(0),

    ISTYPE(1), // type
    EQUAL(0),
    NOTEQUAL(0),
    GT(0),
    LT(0),
    GTEQ(0),
    LTEQ(0);

    private int argCount;

    private OpCode(int argCount)
    {
        this.argCount = argCount;
    }
}
