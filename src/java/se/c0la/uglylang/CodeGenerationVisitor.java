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

        private ObjectAllocateInstruction ntupAlloc = null;

        private int scopeId = -1;

        private Set<Flag> flags;

        public Scope(int scopeId)
        {
            this.subScopes = new ArrayList<Scope>();
            this.symbols = new ArrayList<Symbol>();

            this.scopeId = scopeId;

            flags = EnumSet.noneOf(Flag.class);
        }

        public int getScopeId() { return scopeId; }

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

        public Symbol findSymbolInCurrent(String name)
        {
            for (Symbol symbol : symbols) {
                if (name.equals(symbol.getName())) {
                    return symbol;
                }
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
    }

    private static boolean DEBUG = false;

    private Scope rootScope = null;
    private Scope currentScope = null;

    private List<Instruction> instructions;

    private int scopeCounter = 0;
    private int maxSeq = 0;

    private Set<Flag> flags;

    private Map<String, Integer> labels;
    private int lblCounter = 0;

    private Map<String, Module> imports;
    private Map<Module, Symbol> importSymbols;
    private Map<String, Symbol> exports;

    private Module module;

    public CodeGenerationVisitor(Module module)
    {
        this.module = module;

        instructions = new ArrayList<Instruction>();
        rootScope = new Scope(scopeCounter++);
        currentScope = rootScope;
        flags = EnumSet.noneOf(Flag.class);
        labels = new HashMap<String, Integer>();
        lblCounter = 0;
        imports = new HashMap<String, Module>();
        importSymbols = new HashMap<Module, Symbol>();
        exports = new HashMap<String, Symbol>();
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

    public void setImports(Map<String, Module> imports)
    {
        this.imports = imports;
    }

    public Map<String, Symbol> getExports()
    {
        return exports;
    }

    public Map<Module, Symbol> getImports()
    {
        return importSymbols;
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

    public void setLabels()
    {
        for (Instruction inst : instructions) {
            if (inst instanceof JumpInstruction) {
                JumpInstruction jump = (JumpInstruction)inst;
                String lbl = jump.getLabel();
                Integer addr = labels.get(lbl);
                if (addr != null) {
                    jump.setAddr(addr);
                } else {
                    //throw new RuntimeException(lbl + " not found!");
                }
            }
            else if (inst instanceof JumpOnFalseInstruction) {
                JumpOnFalseInstruction jump = (JumpOnFalseInstruction)inst;
                String lbl = jump.getLabel();
                Integer addr = labels.get(lbl);
                if (addr != null) {
                    jump.setAddr(addr);
                } else {
                    //throw new RuntimeException(lbl + " not found!");
                }
            }
        }
    }

    @Override
    public void addFlag(Flag flag)
    {
        flags.add(flag);
    }

    @Override
    public void addScopeFlag(Flag flag)
    {
        currentScope.flags.add(flag);
    }

    @Override
    public void removeFlag(Flag flag)
    {
        flags.remove(flag);
    }

    @Override
    public void removeScopeFlag(Flag flag)
    {
        currentScope.flags.remove(flag);
    }

    @Override
    public int getCurrentAddr()
    {
        return instructions.size();
    }

    @Override
    public void visit(ImportNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Import name=%s\n",
                    getCurrentAddr(), node.getName());
        }

        Symbol sym = currentScope.findSymbolInCurrent(node.getName());
        if (sym != null) {
            throw new CodeGenerationException(node.getName() + " is already declared.",
                node.getLine(), node.getColumn());
        }

        Module module = imports.get(node.getName());
        if (module == null) {
            throw new CodeGenerationException(node.getName() + " was not found.",
                node.getLine(), node.getColumn());
        }

        Type type = module.getType();

        sym = new Symbol(type, node.getName());
        currentScope.addSymbol(sym);

        importSymbols.put(module, sym);
    }

    @Override
    public void visit(ExportNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Export name=%s\n",
                    getCurrentAddr(), node.getName());
        }

        Symbol sym = currentScope.findSymbol(node.getName());
        exports.put(node.getName(), sym);
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

        Symbol sym = currentScope.findSymbolInCurrent(node.getName());
        if (sym != null) {
            throw new CodeGenerationException(node.getName() + " is already declared.",
                node.getLine(), node.getColumn());
        }

        sym = new Symbol(node.getType(), node.getName());
        node.setSymbol(sym);
        currentScope.addSymbol(sym);
    }

    @Override
    public void visit(FunctionDecl node)
    {
        Scope subScope = new Scope(scopeCounter++);
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

        Scope parentScope = currentScope.getParentScope();
        if (parentScope.flags.contains(Flag.OBJECT)) {
            instructions.add(new ReturnCtxInstruction(false));
        } else {
            instructions.add(new ReturnInstruction(false));
        }
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        // implicit return
        Scope parentScope = currentScope.getParentScope();
        if (parentScope.flags.contains(Flag.OBJECT)) {
            instructions.add(new ReturnCtxInstruction(true));
        } else {
            instructions.add(new ReturnInstruction(true));
        }

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
        FunctionValue value = new FunctionValue(module,
                                                node.getType(),
                                                node.getFuncAddr()+1,
                                                node.getSymbolMap());

        int addr = getCurrentAddr();

        instructions.add(new PushInstruction(value));

        String label = "func" + node.getFuncAddr();
        labels.put(label, addr);
    }

    @Override
    public void visit(UnpackStatement node)
    {
        node.setLbl("unpack" + getCurrentAddr());

        Scope subScope = new Scope(scopeCounter++);
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        CompoundType type = node.getType();
        Type subType = type.getSubType(node.getSubType());

        if (node.getDst() != null) {
            if (subType instanceof VoidType) {
                throw new CodeGenerationException("Void types cannot be assigned " +
                        "to variable.", node.getLine(), node.getColumn());
            }

            Symbol dstSym = new Symbol(subType, node.getDst());
            currentScope.addSymbol(dstSym);

            instructions.add(new DupInstruction());
            instructions.add(new StoreInstruction(dstSym));
        }

        instructions.add(new IsTypeInstruction(subType));
        instructions.add(new JumpOnFalseInstruction(node.getLbl()));

        if (DEBUG) {
            System.out.printf("%d %s\n", getCurrentAddr(), node.toString());
        }
    }


    @Override
    public void visit(EndUnpackStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndUnpack\n", getCurrentAddr());
        }

        labels.put(node.getLbl(), getCurrentAddr());
        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(IfStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d If\n", getCurrentAddr());
        }

        node.setEndLbl("endif" + getCurrentAddr());
        node.setNextLbl("lbl" + lblCounter++);

        // jump to next else if or else
        instructions.add(new JumpOnFalseInstruction(node.getNextLbl()));
    }

    @Override
    public void visit(EndIfStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndIf %s\n", getCurrentAddr(), node.getNextLbl());
        }

        // jump to end
        instructions.add(new JumpInstruction(node.getEndLbl()));

        labels.put(node.getNextLbl(), getCurrentAddr());
    }

    @Override
    public void visit(ElseIfStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d ElseIf\n", getCurrentAddr());
        }

        node.setNextLbl("lbl" + lblCounter++);

        // jump to next else if
        instructions.add(new JumpOnFalseInstruction(node.getNextLbl()));
    }

    @Override
    public void visit(EndElseIfStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndElseIf\n", getCurrentAddr());
        }

        // jump to end
        instructions.add(new JumpInstruction(node.getEndLbl()));

        labels.put(node.getNextLbl(), getCurrentAddr());
    }

    @Override
    public void visit(ElseStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d Else\n", getCurrentAddr());
        }
    }

    @Override
    public void visit(EndElseStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndElse\n", getCurrentAddr());
        }

        labels.put(node.getEndLbl(), getCurrentAddr());
    }

    @Override
    public void visit(WhileStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d While\n", getCurrentAddr());
        }

        node.setLbl("while" + getCurrentAddr());

        instructions.add(new JumpOnFalseInstruction(node.getLbl()));
    }

    @Override
    public void visit(EndWhileStatement node)
    {
        if (DEBUG) {
            System.out.printf("%d EndWhile %d\n", getCurrentAddr(), node.getCondAddr());
        }

        instructions.add(new JumpInstruction(node.getCondAddr()));
        labels.put(node.getLbl(), getCurrentAddr());
    }

    @Override
    public void visit(ObjectNode node)
    {
        Scope subScope = new Scope(scopeCounter++);
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        if (DEBUG) {
            System.out.printf("%d Object %d\n", getCurrentAddr(),
                    subScope.getScopeId());
        }

        // store current value of object reg on stack
        instructions.add(new LoadObjectRegInstruction());

        ObjectAllocateInstruction alloc = new ObjectAllocateInstruction(null);
        currentScope.ntupAlloc = alloc;
        instructions.add(alloc);
        instructions.add(new StoreObjectRegInstruction());
    }

    @Override
    public void visit(ObjectSetNode node)
    {
        if (DEBUG) {
            System.out.printf("%d ObjectSet: field=%s type=%s\n",
                    getCurrentAddr(), node.getField(), node.getType().getName());
        }

        Symbol sym = new Symbol(node.getType(), node.getField(), true);
        currentScope.addSymbol(sym);

        instructions.add(new LoadObjectRegInstruction());
        instructions.add(new SwapInstruction());
        instructions.add(new ObjectSetInstruction(node.getField()));
    }

    @Override
    public void visit(ObjectEndNode node)
    {
        if (DEBUG) {
            System.out.printf("%d ObjectEnd: type=%s\n", getCurrentAddr(),
                    node.getType().getName());
        }

        currentScope.ntupAlloc.setType(node.getType());

        currentScope = currentScope.getParentScope();

        instructions.add(new LoadObjectRegInstruction());
        instructions.add(new SwapInstruction());

        // restore previous value of object reg on stack
        instructions.add(new StoreObjectRegInstruction());
    }

    @Override
    public void visit(ArrayNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Array: size=%d\n", getCurrentAddr(),
                    node.getSize());
        }

        instructions.add(new PushInstruction(new IntegerValue(node.getSize())));

        ArrayAllocateInstruction allocInst = new ArrayAllocateInstruction(null);
        node.setAllocInst(allocInst);
        instructions.add(allocInst);
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
    public void visit(ArrayEndNode node)
    {
        if (DEBUG) {
            System.out.printf("%d ArrayEnd\n", getCurrentAddr());
        }

        ArrayType type;
        try {
            ArrayNode arrayNode = node.getArrayNode();
            type = arrayNode.inferType();
        } catch (TypeException e) {
            throw new CodeGenerationException(e, node.getLine(), node.getColumn());
        }

        ArrayAllocateInstruction allocInst = node.getAllocInst();
        allocInst.setType(type);
    }

    @Override
    public void visit(AssignNode node)
    {
        if (DEBUG) {
            System.out.printf("%d Assign\n", getCurrentAddr());
        }

        maxSeq = node.findMaxSeq();
    }

    @Override
    public void visit(AssignDeclarationNode node)
    {
        Type exprType;
        try {
            exprType = node.getExprType();
        } catch (TypeException e) {
            throw new CodeGenerationException(e, node.getLine(), node.getColumn());
        }

        Symbol targetSym = node.getDeclaration().getSymbol();
        if (DEBUG) {
            System.out.printf("%d Assigning to decl %s to %s of type %s\n",
                    getCurrentAddr(), exprType.getName(),
                    targetSym.getName(), targetSym.getType().getName());
        }

        if (!targetSym.getType().isCompatible(exprType)) {
            throw new CodeGenerationException("Type mismatch in assignment: " +
                    targetSym.getType().getName() + " != " + exprType.getName(),
                    node.getLine(), node.getColumn());
        }

        instructions.add(new StoreInstruction(targetSym));
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
            throw new CodeGenerationException("Symbol not found: " + node.getName(),
                    node.getLine(), node.getColumn());
        }

        node.setSymbol(sym);

        if (DEBUG) {
            if (flags.contains(Flag.CALL)) {
                System.out.printf("C ");
            }
            if (flags.contains(Flag.ASSIGN)) {
                System.out.printf("A ");
            }
            System.out.printf("%d Variable: name=%s, seq=%d, type=%s, objFld=%s\n",
                    getCurrentAddr(), node.getName(), node.getSeq(),
                    sym.getType().getName(), sym.isObjectField());
        }

        if (flags.contains(Flag.ASSIGN) && node.getSeq() == maxSeq) {
            if (sym.isObjectField()) {
                instructions.add(new LoadObjectRegInstruction());
                instructions.add(new SwapInstruction());
                instructions.add(new ObjectSetInstruction(sym.getName()));
            } else {
                instructions.add(new StoreInstruction(sym));
            }
        } else {
            if (sym.isObjectField()) {
                instructions.add(new LoadObjectRegInstruction());
                instructions.add(new ObjectGetInstruction(sym.getName()));
            } else {
                instructions.add(new LoadInstruction(sym));
            }
        }
    }

    @Override
    public void visit(SubscriptNode node)
    {
        Type type = null;
        try {
            type = node.getType();
        } catch (TypeException e) {
            throw new CodeGenerationException(e, node.getLine(), node.getColumn());
        }

        if (!type.hasField(node.getKey())) {
            throw new CodeGenerationException("Field " + node.getKey() + " was " +
                    "not found in " + type.getName() + ".",
                    node.getLine(), node.getColumn());
        }

        if (DEBUG) {
            if (flags.contains(Flag.CALL)) {
                System.out.printf("C ");
            }
            if (flags.contains(Flag.ASSIGN)) {
                System.out.printf("A ");
            }
            System.out.printf("%d Subscript %s seq=%d\n", getCurrentAddr(),
                    node.toString(), node.getSeq());
        }

        if (flags.contains(Flag.ASSIGN) && node.getSeq() == maxSeq) {
            instructions.add(new SwapInstruction());
            instructions.add(new ObjectSetInstruction(node.getKey()));
        } else if (flags.contains(Flag.CALL)) {
            instructions.add(new DupInstruction());
            instructions.add(new ObjectGetInstruction(node.getKey()));
        } else {
            instructions.add(new ObjectGetInstruction(node.getKey()));
        }

        removeFlag(Visitor.Flag.CALL);
    }

    @Override
    public void visit(IndexNode node)
    {
        Type type = null;
        try {
            type = node.getVariable().inferType();
        } catch (TypeException e) {
            throw new CodeGenerationException(e, node.getLine(), node.getColumn());
        }

        if (DEBUG) {
            if (flags.contains(Flag.CALL)) {
                System.out.printf("C ");
            }
            if (flags.contains(Flag.ASSIGN)) {
                System.out.printf("A ");
            }
            System.out.printf("%d Index: idx=%s, seq=%d, type=%s\n", getCurrentAddr(),
                    node.toString(), node.getSeq(), type.getName());
        }

        if (!(type instanceof ArrayType)) {
            throw new CodeGenerationException("Only arrays can be indexed.",
                    node.getLine(), node.getColumn());
        }

        if (flags.contains(Flag.ASSIGN) && node.getSeq() == maxSeq) {
            instructions.add(new ArraySetInstruction2());
        } else {
            instructions.add(new ArrayGetInstruction());
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
        Type actualType = null, expectedType = null, varType = null;
        try {
            actualType = node.inferActualType();
            expectedType = node.inferExpectedType();
            varType = node.inferVarType();
        }
        catch (TypeException e) {
            throw new CodeGenerationException(e, node.getLine(), node.getColumn());
        }

        if (!expectedType.isCompatible(actualType)) {
            throw new CodeGenerationException("Type mismatch for function call: " +
                actualType.getName() + " != " + expectedType.getName(),
                node.getLine(), node.getColumn());
        }

        if (DEBUG) {
            System.out.printf("%d CALL FunctionCall: actual=%s expected=%s\n",
                    getCurrentAddr(),
                    actualType.getName(), expectedType.getName());
        }

        if (node.isSubscriptCall() || node.isObjectCall()) {
            if (node.isObjectCall()) {
                instructions.add(new LoadObjectRegInstruction());
                instructions.add(new SwapInstruction());
            }
            instructions.add(new CallCtxInstruction());
        } else {
            instructions.add(new CallInstruction());
        }
    }
}
