package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;
import se.c0la.uglylang.ir.*;
import se.c0la.uglylang.type.*;
import se.c0la.uglylang.nativefunc.*;

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

    private static boolean DEBUG = true;

    private Scope rootScope = null;
    private Scope currentScope = null;

    private List<Instruction> instructions;
    private Map<String, Integer> labels;

    public CodeGenerationVisitor()
    {
        instructions = new ArrayList<Instruction>();
        labels = new HashMap<String, Integer>();

        rootScope = new Scope();

        currentScope = rootScope;
    }

    public Symbol registerNativeFunction(NativeFunction func)
    {
        Symbol symbol = new Symbol(func.getType(), func.getName());
        rootScope.addSymbol(symbol);
        return symbol;
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
            System.out.println(symbol.getType().getName() + " " + symbol.getName());
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
        if (DEBUG) {
            System.out.printf("%d Declaration: type=%s, name=%s\n",
                    getCurrentAddr(), node.getType().getName(), node.getName());
        }

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

        if (DEBUG) {
            System.out.println();
            System.out.println("Entering new scope");
            System.out.printf("%d Function: type=%s\n", getCurrentAddr(), node.getType());
        }

        instructions.add(new JumpInstruction("func" + getCurrentAddr()));
    }

    @Override
    public void visit(ReturnStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d Return\n", getCurrentAddr());
        }

        instructions.add(new ReturnInstruction());
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndFunctionStatement: funcAddr=%d\n",
                    getCurrentAddr(), node.getFuncAddr());
            System.out.println("Leaving scope");
            System.out.println("Symbols:");
            for (Symbol symbol : currentScope.getSymbols()) {
                System.out.println(symbol.getType().getName() + " " + symbol.getName());
            }
            System.out.println();
        }

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
        if (DEBUG) {
            System.out.printf("%d JumpOnFalse %s\n", getCurrentAddr(), label);
        }

        instructions.add(new JumpOnFalseInstruction(label));
    }

    @Override
    public void visit(EndIfStatement node)
    {
        labels.put(node.getLabel(), getCurrentAddr());
        if (DEBUG) {
            System.out.printf("%d LABEL :%s\n", getCurrentAddr(), node.getLabel());
        }

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
    public void visit(TupleNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Tuple %d\n", getCurrentAddr(), node.getValueCount());
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getValueCount())));
        instructions.add(new TupleAllocateInstruction());
    }

    @Override
    public void visit(TupleSetNode node)
    {
        if (DEBUG) {
            System.out.printf("%d TupleSet %d\n", getCurrentAddr(), node.getIndex());
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getIndex())));
        instructions.add(new TupleSetInstruction());
    }

    @Override
    public void visit(NamedTupleNode node)
    {
        if (DEBUG) {
            System.out.printf("%d NamedTuple\n", getCurrentAddr());
        }

        /*Scope subScope = new Scope();
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;*/

        // TODO: Implement
    }

    @Override
    public void visit(ArrayNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Array: size=%d\n", getCurrentAddr(),
                    node.getSize());
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getSize())));
        instructions.add(new ArrayAllocateInstruction());
    }

    @Override
    public void visit(ArraySetNode node)
    {
        if (DEBUG) {
            System.out.printf("%d ArraySet: idx=%d\n", getCurrentAddr(),
                    node.getIndex());
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getIndex())));
        instructions.add(new ArraySetInstruction());
    }

    @Override
    public void visit(AssignNode node)
    {
        Type exprType;
        try {
            exprType = node.getExprType();
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

        Symbol targetSym = currentScope.getTargetSymbol();
        if (DEBUG) {
            System.out.printf("%d Assigning to %s to %s of type %s\n",
                    getCurrentAddr(), exprType.getName(),
                    targetSym.getName(), targetSym.getType().getName());
        }

        if (!exprType.getName().equals(targetSym.getType().getName())) {
            throw new RuntimeException("Type mismatch in assignment.");
        }

        instructions.add(new StoreInstruction(currentScope.getTargetSymbol()));

        currentScope.setTargetSymbol(null);
    }

    @Override
    public void visit(AndNode node)
    {
        if (DEBUG) {
            System.out.printf("%d And\n", getCurrentAddr());
        }

        instructions.add(new AndInstruction());
    }

    @Override
    public void visit(OrNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Or\n", getCurrentAddr());
        }

        instructions.add(new OrInstruction());
    }

    @Override
    public void visit(XorNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Xor\n", getCurrentAddr());
        }

        instructions.add(new XorInstruction());
    }

    @Override
    public void visit(AddNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Add\n", getCurrentAddr());
        }

        instructions.add(new AddInstruction());
    }

    @Override
    public void visit(SubNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Sub\n", getCurrentAddr());
        }

        instructions.add(new SubInstruction());
    }

    @Override
    public void visit(MulNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Mul\n", getCurrentAddr());
        }

        instructions.add(new MulInstruction());
    }

    @Override
    public void visit(DivNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Div\n", getCurrentAddr());
        }

        instructions.add(new DivInstruction());
    }

    @Override
    public void visit(ModNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Mod\n", getCurrentAddr());
        }

        instructions.add(new ModInstruction());
    }

    @Override
    public void visit(EqualNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Equal\n", getCurrentAddr());
        }

        instructions.add(new EqualInstruction());
    }

    @Override
    public void visit(NotEqualNode node)
    {
        if (DEBUG) {
            System.out.printf("%d NotEqual\n", getCurrentAddr());
        }

        instructions.add(new NotEqualInstruction());
    }

    @Override
    public void visit(LtNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Lt\n", getCurrentAddr());
        }

        instructions.add(new LtInstruction());
    }

    @Override
    public void visit(GtNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Gt\n", getCurrentAddr());
        }

        instructions.add(new GtInstruction());
    }

    @Override
    public void visit(LtEqNode node)
    {
        if (DEBUG) {
            System.out.printf("%d LtEq\n", getCurrentAddr());
        }

        instructions.add(new LtEqInstruction());
    }

    @Override
    public void visit(GtEqNode node)
    {
        if (DEBUG) {
            System.out.printf("%d GtEq\n", getCurrentAddr());
        }

        instructions.add(new GtEqInstruction());
    }

    @Override
    public void visit(Variable node)
    {
        Symbol sym = currentScope.findSymbol(node.getName());
        if (sym == null) {
            throw new RuntimeException("Symbol not found: " + node.getName());
        }

        node.setSymbol(sym);

        if (DEBUG) {
            System.out.printf("%d Variable: name=%s, type=%s\n", getCurrentAddr(),
                    node.getName(), sym.getType().getName());
        }

        // TODO: This is wrong if the variable is the target of an assignment
        instructions.add(new LoadInstruction(sym));

        if (node.isAssignTarget()) {
            currentScope.setTargetSymbol(sym);
        }
    }

    @Override
    public void visit(SubscriptNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Subscript %s\n", getCurrentAddr(), node.toString());
        }

        // TODO: Implement
    }

    @Override
    public void visit(IndexNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Index %s\n", getCurrentAddr(), node.toString());
        }

        instructions.add(new ArrayGetInstruction());
    }

    @Override
    public void visit(StringConstant node)
    {
        if (DEBUG) {
            System.out.printf("%d String: value=%s\n", getCurrentAddr(), node.getValue());
        }

        StringValue value = new StringValue(node.getValue());
        instructions.add(new PushInstruction(value));
    }

    @Override
    public void visit(IntegerConstant node)
    {
        if (DEBUG) {
            System.out.printf("%d Integer: value=%d\n", getCurrentAddr(), node.getValue());
        }

        IntegerValue value = new IntegerValue(node.getValue());
        instructions.add(new PushInstruction(value));
    }

    @Override
    public void visit(FunctionCall node)
    {
        Type type = null;
        try {
            type = node.getFunctionType();
        }
        catch (TypeException e) {
            throw new RuntimeException(e);
        }

        if (DEBUG) {
            System.out.printf("%d CALL FunctionCall: type=%s\n", getCurrentAddr(),
                    type.getName());
        }

        instructions.add(new CallInstruction());
    }
}
