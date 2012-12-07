package se.c0la.uglylang.ast;

import java.util.*;

import se.c0la.uglylang.type.Type;
import se.c0la.uglylang.type.FunctionType;
import se.c0la.uglylang.type.TypeException;
import se.c0la.uglylang.Symbol;

public class FunctionDecl implements Expression, Block
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

    public FunctionType getType()
    {
        List<Type> paramTypes = new ArrayList<Type>();
        for (Declaration param : params) {
            paramTypes.add(param.getType());
        }
        return new FunctionType(returnType, paramTypes);
    }

    @Override
    public Type inferType()
    throws TypeException
    {
        return getType();
    }

    @Override
    public void accept(Visitor visitor)
    {
        int funcAddr = visitor.getCurrentAddr();

        visitor.visit(this);

        Map<String, Symbol> paramSymbols = new LinkedHashMap<String, Symbol>();
        for (Declaration decl : params) {
            decl.accept(visitor);
            paramSymbols.put(decl.getName(), decl.getSymbol());
        }

        for (Node node : stmts) {
            node.accept(visitor);
        }

        Node retStmt = new EndFunctionStatement(getType(), funcAddr, paramSymbols);
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
        buf.append(")");

        return buf.toString();
    }
}
