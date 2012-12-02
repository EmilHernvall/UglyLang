package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;
import se.c0la.uglylang.ir.*;

public class CodeGenerationVisitor implements Visitor
{
    public static class Scope
    {
        private Scope parent = null;
        private List<Scope> subScopes = null;
        private List<Symbol> symbols = null;

        private Symbol targetSymbol = null;

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

        public Symbol findSymbol(String name)
        {
            for (Symbol symbol : symbols) {
                if (name.equals(symbol.getName())) {
                    return symbol;
                }
            }

            if (parent != null) {
                return parent.findSymbol(name);
            }

            return null;
        }

        public void setTargetSymbol(Symbol sym) { this.targetSymbol = sym; }
        public Symbol getTargetSymbol() { return targetSymbol; }
    }

    private Scope rootScope = null;
    private Scope currentScope = null;

    private List<Instruction> instructions;
    private Map<String, Integer> labels;

    public CodeGenerationVisitor()
    {
        instructions = new ArrayList<Instruction>();
        labels = new HashMap<String, Integer>();

        rootScope = new Scope();

        {
            Type type = new FunctionType(new IntegerType(), new ArrayList<Type>());
            rootScope.addSymbol(new Symbol(type, "readInt"));
        }

        {
            List<Type> params = new ArrayList<Type>();
            params.add(new IntegerType());
            params.add(new IntegerType());
            Type type = new FunctionType(new IntegerType(), params);
            rootScope.addSymbol(new Symbol(type, "pow"));
        }

        {
            List<Type> params = new ArrayList<Type>();
            params.add(new IntegerType());
            Type type = new FunctionType(new StringType(), params);
            rootScope.addSymbol(new Symbol(type, "intToStr"));
        }

        {
            List<Type> params = new ArrayList<Type>();
            params.add(new StringType());
            Type type = new FunctionType(new VoidType(), params);
            rootScope.addSymbol(new Symbol(type, "print"));
        }

        currentScope = rootScope;
    }

    public List<Instruction> getInstructions()
    {
        return instructions;
    }

    public void dump()
    {
        System.out.println();
        System.out.println("Root Scope Symbols:");
        for (Symbol symbol : rootScope.getSymbols()) {
            System.out.println(symbol.getType() + " " + symbol.getName());
        }

        System.out.println();
        System.out.println("Instructions");

        int i = 0;
        for (Instruction inst : instructions) {
            System.out.printf("%d %s\n", i++, inst.toString());
        }
    }

    @Override
    public int getCurrentAddr()
    {
        return instructions.size();
    }

    @Override
    public void visit(Declaration node)
    {
        System.out.printf("%d Declaration: type=%s, name=%s\n",
                getCurrentAddr(), node.getType(), node.getName());

        Symbol sym = new Symbol(node.getType(), node.getName());
        node.setSymbol(sym);
        currentScope.addSymbol(sym);
        currentScope.setTargetSymbol(sym);
    }

    @Override
    public void visit(FunctionDecl node)
    {
        Scope subScope = new Scope();
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        System.out.println();
        System.out.println("Entering new scope");
        System.out.printf("%d Function: type=%s\n", getCurrentAddr(), node.getType());

        instructions.add(new JumpInstruction("func" + getCurrentAddr()));
    }

    @Override
    public void visit(ReturnStatement node)
    {
        System.out.printf("%d Return\n", getCurrentAddr());

        instructions.add(new ReturnInstruction());
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        System.out.printf("%d EndFunctionStatement: funcAddr=%d\n",
                getCurrentAddr(), node.getFuncAddr());
        System.out.println("Leaving scope");
        System.out.println("Symbols:");
        for (Symbol symbol : currentScope.getSymbols()) {
            System.out.println(symbol.getType() + " " + symbol.getName());
        }
        System.out.println();

        currentScope = currentScope.getParentScope();

        // push function prototype and adress onto stack
        FunctionValue value = new FunctionValue(node.getType(), node.getFuncAddr()+1,
                node.getSymbolMap());
        int addr = getCurrentAddr();

        instructions.add(new PushInstruction(value));

        String label = "func" + node.getFuncAddr();
        for (Instruction inst : instructions) {
            if (inst.getOpCode() != OpCode.JUMP) {
                continue;
            }

            JumpInstruction jump = (JumpInstruction)inst;
            if (!jump.getLabel().equals(label)) {
                continue;
            }

            jump.setAddr(addr);
        }
    }

    @Override
    public void visit(IfStatement node)
    {
        String label = "EndIf_" + getCurrentAddr();
        System.out.printf("%d JumpOnFalse %s\n", getCurrentAddr(), label);

        instructions.add(new JumpOnFalseInstruction(label));
    }

    @Override
    public void visit(EndIfStatement node)
    {
        labels.put(node.getLabel(), getCurrentAddr());
        System.out.printf("%d LABEL :%s\n", getCurrentAddr(), node.getLabel());

        for (Instruction inst : instructions) {
            if (inst.getOpCode() != OpCode.JUMPONFALSE) {
                continue;
            }

            JumpOnFalseInstruction jumpOnFalse = (JumpOnFalseInstruction)inst;
            if (!jumpOnFalse.getLabel().equals(node.getLabel())) {
                continue;
            }

            jumpOnFalse.setAddr(getCurrentAddr());
        }
    }

    @Override
    public void visit(AssignNode node)
    {
        System.out.printf("%d Assigning to %s\n", getCurrentAddr(),
                currentScope.getTargetSymbol().getName());

        instructions.add(new StoreInstruction(currentScope.getTargetSymbol()));

        currentScope.setTargetSymbol(null);
    }

    @Override
    public void visit(AddNode node)
    {
        System.out.printf("%d Add\n", getCurrentAddr());

        instructions.add(new AddInstruction());
    }

    @Override
    public void visit(SubNode node)
    {
        System.out.printf("%d Sub\n", getCurrentAddr());

        instructions.add(new SubInstruction());
    }

    @Override
    public void visit(MulNode node)
    {
        System.out.printf("%d Mul\n", getCurrentAddr());

        instructions.add(new MulInstruction());
    }

    @Override
    public void visit(DivNode node)
    {
        System.out.printf("%d Div\n", getCurrentAddr());

        instructions.add(new DivInstruction());
    }

    @Override
    public void visit(EqualNode node)
    {
        System.out.printf("%d Equal\n", getCurrentAddr());

        instructions.add(new EqualInstruction());
    }

    @Override
    public void visit(Variable node)
    {
        Symbol sym = currentScope.findSymbol(node.getName());
        if (sym == null) {
            throw new RuntimeException("Symbol not found: " + node.getName());
        }
        System.out.printf("%d Variable: name=%s, type=%s\n", getCurrentAddr(),
                node.getName(), sym.getType());

        // TODO: This is wrong if the variable is the target of an assignment
        instructions.add(new LoadInstruction(sym));

        if (node.isAssignTarget()) {
            currentScope.setTargetSymbol(sym);
        }
    }

    @Override
    public void visit(StringConstant node)
    {
        System.out.printf("%d String: value=%s\n", getCurrentAddr(), node.getValue());

        StringValue value = new StringValue(node.getValue());
        instructions.add(new PushInstruction(value));
    }

    @Override
    public void visit(IntegerConstant node)
    {
        System.out.printf("%d Integer: value=%d\n", getCurrentAddr(), node.getValue());

        IntegerValue value = new IntegerValue(node.getValue());
        instructions.add(new PushInstruction(value));
    }

    @Override
    public void visit(FunctionCall node)
    {
        Symbol sym = currentScope.findSymbol(node.getFunctionName());
        if (sym == null) {
            throw new RuntimeException("Symbol not found: " +
                    node.getFunctionName());
        }

        System.out.printf("%d CALL FunctionCall: function=%s, type=%s\n", getCurrentAddr(),
                node.getFunctionName(), sym.getType());

        instructions.add(new CallInstruction(sym));
    }
}
