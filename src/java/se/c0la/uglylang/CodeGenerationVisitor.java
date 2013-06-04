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

        public Scope(int scopeId)
        {
            this.subScopes = new ArrayList<Scope>();
            this.symbols = new ArrayList<Symbol>();

            this.scopeId = scopeId;
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
    private Map<String, Integer> labels;

    private int scopeCounter = 0;
    private int maxSeq = 0;

    private Set<Flag> flags;

    public CodeGenerationVisitor()
    {
        instructions = new ArrayList<Instruction>();
        labels = new HashMap<String, Integer>();

        rootScope = new Scope(scopeCounter++);

        flags = EnumSet.noneOf(Flag.class);

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
    public void addFlag(Flag flag)
    {
        flags.add(flag);
    }

    @Override
    public void removeFlag(Flag flag)
    {
        flags.remove(flag);
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

        if (flags.contains(Flag.OBJECT)) {
            instructions.add(new ReturnCtxInstruction(false));
        } else {
            instructions.add(new ReturnInstruction(false));
        }
    }

    @Override
    public void visit(EndFunctionStatement node)
    {
        // implicit return
        if (flags.contains(Flag.OBJECT)) {
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

        Scope subScope = new Scope(scopeCounter++);
        subScope.setParentScope(currentScope);
        currentScope.addSubScope(subScope);

        currentScope = subScope;

        CompoundType type = node.getType();
        Type subType = type.getSubType(node.getSubType());

        instructions.add(new IsTypeInstruction(subType));
        instructions.add(new JumpOnFalseInstruction(label));

        if (node.getDst() != null) {
            if (subType instanceof VoidType) {
                throw new RuntimeException("Void types cannot be assigned to variable.");
            }

            Symbol dstSym = new Symbol(subType, node.getDst());
            currentScope.addSymbol(dstSym);

            Variable srcVar = node.getSrc();
            Symbol srcSym = currentScope.findSymbol(srcVar.getName());
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

        //instructions.add(new StoreInstruction(sym));

        //Symbol objSym =
        //    currentScope.findSymbol("_obj" + currentScope.getScopeId());
        //instructions.add(new LoadInstruction(objSym));
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
            throw new RuntimeException(e);
        }

        ArrayAllocateInstruction allocInst = node.getAllocInst();
        allocInst.setType(type);
    }

    @Override
    public void visit(AssignNode node)
    {
        /*Type exprType;
        try {
            exprType = node.inferType();
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

        if (targetSym.isObjectField()) {
            Scope scope = currentScope.findSymbolScope(targetSym);
            Symbol objSym =
                currentScope.findSymbol("_obj" + scope.getScopeId());
            instructions.add(new LoadInstruction(objSym));
            instructions.add(new SwapInstruction());
            instructions.add(new ObjectSetInstruction(targetSym.getName()));
        } else {
            instructions.add(new StoreInstruction(targetSym));
        }*/

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
            throw new RuntimeException(e);
        }

        Symbol targetSym = node.getDeclaration().getSymbol();
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
                //Scope scope = currentScope.findSymbolScope(sym);
                //Symbol objSym =
                //    currentScope.findSymbol("_obj" + scope.getScopeId());
                //instructions.add(new LoadInstruction(objSym));
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
        ObjectType type = null;
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
            throw new RuntimeException("Only arrays can be indexed.");
        }

        if (flags.contains(Flag.ASSIGN) && node.getSeq() == maxSeq) {
            instructions.add(new ArraySetInstruction());
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

        if (node.isSubscriptCall()) {
            instructions.add(new CallCtxInstruction());
        } else {
            instructions.add(new CallInstruction());
        }
    }
}
