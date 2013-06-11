package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.CompoundType;

public class UnpackStatement extends AbstractNode implements Node, Block
{
    private Expression src;
    private String dst;
    private CompoundType type;
    private String subType;
    private List<Node> stmts;

    private String lbl;

    public UnpackStatement(Expression src,
                           String dst,
                           CompoundType type,
                           String subType,
                           List<Node> stmts)
    {
        this.src = src;
        this.dst = dst;
        this.type = type;
        this.subType = subType;
        this.stmts = stmts;
    }

    public CompoundType getType() { return type; }
    public String getSubType() { return subType; }
    public Expression getSrc() { return src; }
    public String getDst() { return dst; }

    public void setLbl(String lbl) { this.lbl = lbl; }
    public String getLbl() { return lbl; }

    @Override
    public void accept(Visitor visitor)
    {
        src.accept(visitor);

        visitor.visit(this);

        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node endUnpack = new EndUnpackStatement(lbl);
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
