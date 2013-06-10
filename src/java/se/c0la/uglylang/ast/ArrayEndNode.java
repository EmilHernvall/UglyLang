package se.c0la.uglylang.ast;

import java.util.List;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.ArrayType;
import se.c0la.uglylang.type.TypeException;
import se.c0la.uglylang.ir.ArrayAllocateInstruction;

public class ArrayEndNode extends AbstractNode implements Node
{
    private ArrayNode node;
    private ArrayAllocateInstruction inst;

    public ArrayEndNode(ArrayNode node, ArrayAllocateInstruction inst)
    {
        this.node = node;
        this.inst = inst;
    }

    public ArrayNode getArrayNode() { return node; }
    public ArrayAllocateInstruction getAllocInst() { return inst; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return "ArrayEnd";
    }
}
