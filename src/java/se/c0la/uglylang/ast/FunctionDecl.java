package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.Type;
import se.c0la.uglylang.FunctionType;

public class FunctionDecl extends Block
{
    private Type returnType;
    private List<Declaration> params;
    private List<Node> stmts;

    public FunctionDecl(Type returnType, List<Declaration> params, List<Node> stmts)
    {
        this.returnType = returnType;
        this.params = params;
        this.stmts = stmts;
    }

    public List<Declaration> getParams() { return params; }

    public Type getType()
    {
        List<Type> paramTypes = new ArrayList<Type>();
        for (Declaration param : params) {
            paramTypes.add(param.getType());
        }
        return new FunctionType(returnType, paramTypes);
    }

    @Override
    public void accept(Visitor visitor)
    {
        int funcAddr = visitor.getCurrentAddr();

        visitor.visit(this);
        for (Node node : stmts) {
            node.accept(visitor);
        }

        // implicit return
        Node retStmt = new EndFunctionStatement(funcAddr);
        retStmt.accept(visitor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(returnType.getName());
        buf.append("(");

        String delim = "";
        for (Node node : params) {
            buf.append(delim);
            buf.append(node.toString());
            delim = ", ";
        }
        buf.append(") ");
        buf.append("{\n");
        for (Node n : stmts) {
            if (n instanceof Block) {
                buf.append(indent(n.toString()));
            } else {
                buf.append("\t");
                buf.append(n.toString());
                buf.append(";\n");
            }
        }
        buf.append("}\n");

        return buf.toString();
    }
}
