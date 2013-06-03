package se.c0la.uglylang.ir;

public enum OpCode
{
    CALL,
    RETURN,
    LOAD,
    STORE,
    PUSH,
    CAST,
    SWAP,

    ARRAY_ALLOCATE,
    ARRAY_GET,
    ARRAY_SET,

    TUPLE_ALLOCATE,
    TUPLE_GET,
    TUPLE_SET,

    NTUPLE_ALLOCATE,
    NTUPLE_GET,
    NTUPLE_SET,

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
