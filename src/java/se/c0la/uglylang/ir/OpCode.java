package se.c0la.uglylang.ir;

public enum OpCode
{
    CALL,
    RETURN,
    LOAD,
    STORE,
    PUSH,

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
