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
        private NamedTupleAllocateInstruction ntupAlloc = null;

        private int namedTupleId = -1;

        public Scope()
        {
            this.subScopes = new ArrayList<Scope>();
            this.symbols = new ArrayList<Symbol>();
        }

        public Scope(int namedTupleId)
        {
            this.subScopes = new ArrayList<Scope>();
            this.symbols = new ArrayList<Symbol>();

            this.namedTupleId = namedTupleId;
        }

        public int getNamedTupleId() { return namedTupleId; }

        public Scope getParentScope() { return parent; }
        public void setParentScope(Scope scope) { this.parent = scope; }

        public void addSubScope(Scope scope) { subScopes.add(scope); }

        public void addSymbol(Symbol v) { symbols.add(v); }
        public void removeSymbol(Symbol v) { symbols.remove(v); }
        public List<Symbol> getSymbols() { return symbols; }

        public Scope findSymbolScope(Symbol sym)
        {
            for (Symbol symbol : symbols) {
                if (sym == symbol) {
                    return this;
                }
            }

            if (parent != null) {
                return parent.findSymbolScope(sym);
            }

            return null;
        }

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

    private static boolean DEBUG = false;

    private Scope rootScope = null;
    private Scope currentScope = null;

    private List<Instruction> instructions;
    private Map<String, Integer> labels;

    private int namedTupleCounter = 0;

    public CodeGenerationVisitor()
    {
        instructions = new ArrayList<Instruction>();
        labels = new HashMap<String, Integer>();

        rootScope = new Scope();

        currentScope = rootScope;
    }

    public void setDebug(boolean v) { DEBUG = v; }

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
    public void visit(TypeDeclNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Type Declaration: type=%s, name=%s\n",
                    getCurrentAddr(), node.getType().getName(), node.getName());
        }
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

        instructions.add(new ReturnInstruction(false));
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        // implicit return
        instructions.add(new ReturnInstruction(true));

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
    public void visit(UnpackStatement node)
    {
        String label = "EndUnpack_" + getCurrentAddr();

        Scope subScope = new Scope();
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        CompoundType type = node.getType();
        Type subType = type.getSubType(node.getSubType());

        Variable srcVar = node.getSrc();
        Symbol srcSym = currentScope.findSymbol(srcVar.getName());

        instructions.add(new LoadInstruction(srcSym));
        instructions.add(new IsTypeInstruction(subType));
        instructions.add(new JumpOnFalseInstruction(label));

        if (node.getDst() != null) {
            if (subType instanceof VoidType) {
                throw new RuntimeException("Void types cannot be assigned to variable.");
            }

            Symbol dstSym = new Symbol(subType, node.getDst());
            //node.setSymbol(sym);
            currentScope.addSymbol(dstSym);
            currentScope.setTargetSymbol(dstSym);

            instructions.add(new CastInstruction(srcSym, dstSym));
        }

        if (DEBUG) {
            System.out.printf("%d %s\n", getCurrentAddr(), node.toString());
        }
    }


    @Override
    public void visit(EndUnpackStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d %s\n", getCurrentAddr(), node.getLabel());
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

        currentScope = currentScope.getParentScope();
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
    public void visit(WhileStatement node)
    {
        String label = "While_" + getCurrentAddr();
        if (DEBUG) {
            System.out.printf("%d JumpOnFalse %s\n", getCurrentAddr(), label);
        }

        instructions.add(new JumpOnFalseInstruction(label));
    }

    @Override
    public void visit(EndWhileStatement node)
    {
        labels.put(node.getLabel(), getCurrentAddr());
        if (DEBUG) {
            System.out.printf("%d JUMP %d\n", getCurrentAddr(), node.getCondAddr());
            System.out.printf("%d LABEL :%s\n", getCurrentAddr(), node.getLabel());
        }

        instructions.add(new JumpInstruction(node.getCondAddr()));

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
    public void visit(NamedTupleNode node)
    {
        namedTupleCounter++;

        if (DEBUG) {
            System.out.printf("%d NamedTuple %d\n", getCurrentAddr(),
                    namedTupleCounter);
        }

        Scope subScope = new Scope(namedTupleCounter);
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        // Use a wildcard type as a placeholder
        Symbol sym = new Symbol(new WildcardType(), "_ntuple" + namedTupleCounter);
        currentScope.addSymbol(sym);

        NamedTupleAllocateInstruction alloc = new NamedTupleAllocateInstruction(null);
        currentScope.ntupAlloc = alloc;

        instructions.add(alloc);
        // SCOPESTACK PUSH
        instructions.add(new StoreInstruction(sym));
    }

    @Override
    public void visit(NamedTupleSetNode node)
    {
        if (DEBUG) {
            System.out.printf("%d NamedTupleSet: field=%s type=%s\n",
                    getCurrentAddr(), node.getField(), node.getType().getName());
        }

        Symbol sym = new Symbol(node.getType(), node.getField(), true);
        currentScope.addSymbol(sym);

        //instructions.add(new StoreInstruction(sym));

        // SCOPESTACK GET
        Symbol ntupleSym =
            currentScope.findSymbol("_ntuple" + currentScope.getNamedTupleId());
        instructions.add(new LoadInstruction(ntupleSym));
        instructions.add(new SwapInstruction());
        instructions.add(new NamedTupleSetInstruction(node.getField()));
    }

    @Override
    public void visit(NamedTupleEndNode node)
    {
        if (DEBUG) {
            System.out.printf("%d NamedTupleEnd: type=%s\n", getCurrentAddr(),
                    node.getType().getName());
        }

        Symbol ntupleSym =
            currentScope.findSymbol("_ntuple" + currentScope.getNamedTupleId());
        ntupleSym.setType(node.getType());

        currentScope.ntupAlloc.setType(node.getType());

        currentScope = currentScope.getParentScope();

        // SCOPESTACK POP
        instructions.add(new LoadInstruction(ntupleSym));
    }

    @Override
    public void visit(ArrayNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Array: size=%d\n", getCurrentAddr(),
                    node.getSize());
        }

        ArrayType type;
        try {
            type = node.inferType();
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getSize())));
        instructions.add(new ArrayAllocateInstruction(type));
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

        if (!targetSym.getType().isCompatible(exprType)) {
            throw new RuntimeException("Type mismatch in assignment: " +
                    targetSym.getType().getName() + " != " + exprType.getName());
        }

        if (targetSym.isNamedTupleField()) {
            // SCOPESTACK GET
            Scope scope = currentScope.findSymbolScope(targetSym);
            Symbol ntupleSym =
                currentScope.findSymbol("_ntuple" + scope.getNamedTupleId());
            instructions.add(new LoadInstruction(ntupleSym));
            instructions.add(new SwapInstruction());
            instructions.add(new NamedTupleSetInstruction(targetSym.getName()));
        } else {
            instructions.add(new StoreInstruction(targetSym));
        }

        currentScope.setTargetSymbol(null);
    }

    @Override
    public void visit(AssignDeclarationNode node)
    {
        Type exprType;
        try {
            exprType = node.getExprType();
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

        Symbol targetSym = currentScope.getTargetSymbol();
        if (DEBUG) {
            System.out.printf("%d Assigning to decl %s to %s of type %s\n",
                    getCurrentAddr(), exprType.getName(),
                    targetSym.getName(), targetSym.getType().getName());
        }

        if (!targetSym.getType().isCompatible(exprType)) {
            throw new RuntimeException("Type mismatch in assignment: " +
                    targetSym.getType().getName() + " != " + exprType.getName());
        }

        instructions.add(new StoreInstruction(targetSym));

        currentScope.setTargetSymbol(null);
    }

    @Override
    public void visit(AssignSubscriptNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Assigning to subscript\n", getCurrentAddr());
        }

        instructions.add(new NamedTupleSetInstruction(node.getField()));
    }

    @Override
    public void visit(AssignIndexNode node)
    {
        Variable var = node.getVariable();
        Symbol sym = currentScope.findSymbol(var.getName());
        Type type = sym.getType();

        if (DEBUG) {
            System.out.printf("%d Assigning to index: type=%s\n",
                    getCurrentAddr(), type.getName());
        }

        if (type instanceof ArrayType) {
            instructions.add(new ArraySetInstruction());
        }
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
            System.out.printf("%d Variable: name=%s, type=%s nTupleFld=%s\n",
                    getCurrentAddr(), node.getName(), sym.getType().getName(),
                    sym.isNamedTupleField());
        }

        if (node.isAssignTarget()) {
            currentScope.setTargetSymbol(sym);
        } else {
            if (sym.isNamedTupleField()) {
                // SCOPESTACK GET
                Scope scope = currentScope.findSymbolScope(sym);
                Symbol ntupleSym =
                    currentScope.findSymbol("_ntuple" + scope.getNamedTupleId());
                instructions.add(new LoadInstruction(ntupleSym));
                instructions.add(new NamedTupleGetInstruction(sym.getName()));
            } else {
                instructions.add(new LoadInstruction(sym));
            }
        }
    }

    @Override
    public void visit(SubscriptNode node)
    {
        NamedTupleType type = null;
        try {
            type = node.getType();
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

        if (!type.hasField(node.getKey())) {
            throw new RuntimeException("Field " + node.getKey() + " was " +
                    "not found in " + type.getName() + ".");
        }

        if (DEBUG) {
            System.out.printf("%d Subscript %s\n", getCurrentAddr(),
                    node.toString());
        }

        instructions.add(new NamedTupleGetInstruction(node.getKey()));
    }

    @Override
    public void visit(IndexNode node)
    {
        Type type = null;
        try {
            type = node.getVariable().inferType();
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }

        if (DEBUG) {
            System.out.printf("%d Index: idx=%s type=%s\n", getCurrentAddr(),
                    node.toString(), type.getName());
        }

        if (type instanceof ArrayType) {
            instructions.add(new ArrayGetInstruction());
        } else {
            throw new RuntimeException("Only arrays and tuples can be indexed.");
        }
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
    public void visit(TypeValue node)
    {
        CompoundType compoundType = node.getType();
        CompoundTerminalType type =
            (CompoundTerminalType)compoundType.getSubType(node.getName());

        if (DEBUG) {
            System.out.printf("%d TypeValue: type=%s\n",
                    getCurrentAddr(),
                    type.getName().toString());
        }

        CompoundTypeValue value = new CompoundTypeValue(type);
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
