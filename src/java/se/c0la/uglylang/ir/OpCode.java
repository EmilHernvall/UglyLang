package se.c0la.uglylang.ir;

public enum OpCode
{
    CALL,
    RETURN,
    LOAD,
    STORE,
    PUSH,

    ARRAY_ALLOCATE,
    ARRAY_GET,
    ARRAY_SET,

    TUPLE_ALLOCATE,
    TUPLE_GET,
    TUPLE_SET,

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

    EQUAL,
    NOTEQUAL,
    GT,
    LT,
    GTEQ,
    LTEQ;
}
