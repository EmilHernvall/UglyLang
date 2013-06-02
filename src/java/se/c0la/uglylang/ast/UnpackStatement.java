package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.CompoundType;

public class UnpackStatement implements Node, Block
{
    private Variable src;
    private String dst;
    private CompoundType type;
    private String subType;
    private List<Node> stmts;

    public UnpackStatement(Variable src, String dst, CompoundType type, String subType, List<Node> stmts)
    {
        this.src = src;
        this.dst = dst;
        this.type = type;
        this.subType = subType;
        this.stmts = stmts;
    }

    public CompoundType getType() { return type; }
    public String getSubType() { return subType; }
    public String getDst() { return dst; }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);

        //src.accept(visitor);
        //dst.accept(visitor);

        String endUnpackLbl = "EndUnpack_" + visitor.getCurrentAddr();

        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endUnpack = new EndUnpackStatement(endUnpackLbl);
        endUnpack.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("Unpack ");
        buf.append(src.toString());
        if (dst != null) {
            buf.append(" ");
            buf.append(dst.toString());
        }

        return buf.toString();
    }
}
