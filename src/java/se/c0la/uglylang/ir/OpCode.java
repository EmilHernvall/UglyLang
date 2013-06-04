package se.c0la.uglylang.ir;

public enum OpCode
{
    CALL,
    CALL_CTX,
    RETURN,
    RETURN_CTX,
    LOAD,
    LOAD_OBJECTREG,
    STORE,
    STORE_OBJECTREG,
    PUSH,
    CAST,
    SWAP,
    DUP,

    ARRAY_ALLOCATE,
    ARRAY_GET,
    ARRAY_SET,

    OBJECT_ALLOCATE,
    OBJECT_GET,
    OBJECT_SET,

    JUMP,
    JUMPONFALSE,

    ADD,
    SUB,
    MUL,
    DIV,
    MOD,

    AND,
    OR,
    XOR,

    ISTYPE,
    EQUAL,
    NOTEQUAL,
    GT,
    LT,
    GTEQ,
    LTEQ;
}
