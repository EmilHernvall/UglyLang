package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;

public class CodeGenerationVisitor implements Visitor
{
    public static class Scope
    {
        private Scope parent = null;
        private List<Scope> subScopes = null;
        private List<Symbol> symbols = null;

        public Scope()
        {
            this.subScopes = new ArrayList<Scope>();
            this.symbols = new ArrayList<Symbol>();
        }

        public Scope getParentScope() { return parent; }
        public void setParentScope(Scope scope) { this.parent = scope; }

        public void addSubScope(Scope scope) { subScopes.add(scope); }

        public void addSymbol(Symbol v) { symbols.add(v); }
        public List<Symbol> getSymbols() { return symbols; }
        //public boolean hasSymbol(String sym) { return symbols.contains(sym); }
    }

    private int addr = 0;
    private Scope rootScope = null;
    private Scope currentScope = null;

    public CodeGenerationVisitor()
    {
        rootScope = new Scope();

        currentScope = rootScope;
    }

    public void dumpScope()
    {
        System.out.println();
        System.out.println("Root Scope Symbols:");
        for (Symbol symbol : rootScope.getSymbols()) {
            System.out.println(symbol.getType() + " " + symbol.getName());
        }
    }

    @Override
    public int getCurrentAddr()
    {
        return addr;
    }

    @Override
    public void visit(Declaration node)
    {
        System.out.printf("%d Declaration: type=%s, name=%s\n",
                addr++, node.getType(), node.getName());

        currentScope.addSymbol(new Symbol(node.getType(), node.getName()));
    }

    @Override
    public void visit(FunctionDecl node)
    {
        Scope subScope = new Scope();
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        for (Declaration decl : node.getParams()) {
            currentScope.addSymbol(new Symbol(decl.getType(), decl.getName()));
        }

        System.out.println();
        System.out.println("Entering new scope");
        System.out.printf("%d Function: type=%s\n", addr++, node.getType());
    }

    @Override
    public void visit(ReturnStatement node)
    {
        System.out.printf("%d Return\n", addr++);

        // emit RETURN
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        System.out.printf("%d EndFunctionStatement: funcAddr=%d\n",
                addr++, node.getFuncAddr());
        System.out.println("Leaving scope");
        System.out.println("Symbols:");
        for (Symbol symbol : currentScope.getSymbols()) {
            System.out.println(symbol.getType() + " " + symbol.getName());
        }
        System.out.println();

        currentScope = currentScope.getParentScope();

        // push function prototype and adress onto stack
    }

    @Override
    public void visit(IfStatement node)
    {
        System.out.printf("%d JumpOnFalse %s\n", addr, "EndIf_" + addr);
        addr++;

        // emit JumpOnFalseInstruction
    }

    @Override
    public void visit(EndIfStatement node)
    {
        System.out.printf("%d LABEL :%s\n", addr++, node.getLabel());
    }

    @Override
    public void visit(AssignNode node)
    {
        System.out.printf("%d Assign\n", addr++);

        // emit StoreInstruction
    }

    @Override
    public void visit(AddNode node)
    {
        System.out.printf("%d Add\n", addr++);

        // emit AddInstruction
    }

    @Override
    public void visit(SubNode node)
    {
        System.out.printf("%d Sub\n", addr++);

        // emit SubInstruction
    }

    @Override
    public void visit(MulNode node)
    {
        System.out.printf("%d Mul\n", addr++);

        // emit MulInstruction
    }

    @Override
    public void visit(DivNode node)
    {
        System.out.printf("%d Div\n", addr++);

        // emit DivInstruction
    }

    @Override
    public void visit(EqualNode node)
    {
        System.out.printf("%d Equal\n", addr++);

        // emit EqualInstruction
    }

    @Override
    public void visit(Variable node)
    {
        /*if (!currentScope.hasSymbol(node.getName())) {
            System.out.println("Symbol not found!");
        }*/
        System.out.printf("%d Variable: name=%s\n", addr++, node.getName());

        // emit LoadInstruction(node.getName())
    }

    @Override
    public void visit(StringConstant node)
    {
        System.out.printf("%d String: value=%s\n", addr++, node.getValue());
    }

    @Override
    public void visit(IntegerConstant node)
    {
        System.out.printf("%d Integer: value=%d\n", addr++, node.getValue());
    }

    @Override
    public void visit(FunctionCall node)
    {
        System.out.printf("%d CALL FunctionCall: function=%s\n", addr++,
                node.getFunctionName());

        // handle arguments
        // CALL
    }
}
