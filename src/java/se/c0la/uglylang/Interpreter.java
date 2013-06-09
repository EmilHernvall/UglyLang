package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;
import se.c0la.uglylang.ir.*;
import se.c0la.uglylang.type.*;
import se.c0la.uglylang.nativefunc.*;

public class Interpreter
{
    private static class Scope
    {
        private Scope parent;
        private Map<Symbol, Value> values;

        public Scope(Scope parent)
        {
            this.parent = parent;
            this.values = new HashMap<Symbol, Value>();
        }

        public int getHeight()
        {
            if (parent == null) {
                return 1;
            }

            return 1 + parent.getHeight();
        }

        public Scope getParentScope()
        {
            return parent;
        }

        public void set(Symbol sym, Value val)
        {
            values.put(sym, val);
        }

        public Value get(Symbol sym)
        {
            if (!values.containsKey(sym)) {
                if (parent != null) {
                    return parent.get(sym);
                }

                return null;
            }

            return values.get(sym);
        }
    }

    private int programCounter;
    private Stack<Value> stack;
    private Scope scope;

    private ObjectValue objectReg = ObjectValue.EMPTY;

    public Interpreter()
    {
        scope = new Scope(null);
    }

    public void dumpStack()
    {
        System.out.println("Dumping stack:");
        for (Value value : stack) {
            System.out.println(value);
        }
        System.out.println();
    }

    public void dumpScope()
    {
        System.out.println("Dumping scope " + scope.getHeight() + ":");
        for (Map.Entry<Symbol, Value> entry : scope.values.entrySet()) {
            Symbol sym = entry.getKey();
            Value val = entry.getValue();
            System.out.println(sym.getName() + ": " + dumpValue(val));
        }
        System.out.println();
    }

    public void registerNativeFunction(Symbol symbol, NativeFunction func)
    {
        scope.set(symbol, new NativeFunctionValue(func.getType(), func));
    }

    public String dumpObjectReg()
    {
        return dumpValue(objectReg);
    }

    public String dumpValue(Value value)
    {
        Set<Value> seen = new HashSet<Value>();
        return dumpValue(value, seen, 0);
    }

    public String pad(int depth)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            buf.append("    ");
        }
        return buf.toString();
    }

    public String dumpValue(Value value, Set<Value> seen, int depth)
    {
        StringBuilder buffer = new StringBuilder();
        if (value instanceof ObjectValue) {
            if (seen.contains(value)) {
                return String.format("%X", System.identityHashCode(value));
            }

            seen.add(value);

            buffer.append(String.format("%X", System.identityHashCode(value)));
            buffer.append(" (\n");
            ObjectValue obj = (ObjectValue)value;
            String delim = "";
            for (String field : obj.getFields()) {
                buffer.append(delim);
                buffer.append(pad(depth+1));
                buffer.append(field);
                buffer.append(": ");

                Value sub = obj.getField(field);
                buffer.append(dumpValue(sub, seen, depth+1));

                delim = ", \n";
            }
            buffer.append("\n");
            buffer.append(pad(depth));
            buffer.append(")");
        }
        else if (value instanceof ArrayValue) {
            if (seen.contains(value)) {
                return "** RECURSION **";
            }

            seen.add(value);

            buffer.append(String.format("%X", System.identityHashCode(value)));
            buffer.append(" [\n");
            ArrayValue arr = (ArrayValue)value;
            String delim = "";
            for (int i = 0; i < arr.getSize(); i++) {
                Value sub = arr.get(i);
                buffer.append(delim);
                buffer.append(pad(depth+1));
                buffer.append(dumpValue(sub, seen, depth+1));

                delim = ", \n";
            }
            buffer.append("\n");
            buffer.append(pad(depth));
            buffer.append("]");
        }
        else {
            buffer.append(value.toString());
        }

        return buffer.toString();
    }

    public void run(List<Instruction> instructions)
    {
        programCounter = 0;
        stack = new Stack<Value>();
        int instrCount = 0;
        while (true) {
            instrCount++;

            if (programCounter >= instructions.size()) {
                System.out.println();
                System.out.println("Execution halted after " + instrCount +
                        " instructions.");
                return;
            }

            Instruction inst = instructions.get(programCounter);
            //System.out.println(programCounter + " " + inst);
            //dumpStack();
            switch (inst.getOpCode()) {
                case CALL:
                {
                    int retAddr = programCounter+1;

                    Value value = stack.pop();

                    if (value instanceof FunctionValue) {
                        scope = new Scope(scope);
                        FunctionValue func = (FunctionValue)value;
                        programCounter = func.getAddr();

                        // put arguments in values map
                        Map<String, Symbol> symbolMap = func.getSymbolMap();
                        List<String> names = new ArrayList<String>(symbolMap.keySet());

                        for (int i = names.size()-1; i >= 0; i--) {
                            String name = names.get(i);
                            Symbol sym = symbolMap.get(name);

                            Value arg = stack.pop();
                            scope.set(sym, arg);
                        }

                        stack.push(new ReturnAddressValue(retAddr));
                    }
                    else if (value instanceof NativeFunctionValue) {
                        NativeFunctionValue funcValue = (NativeFunctionValue)value;
                        NativeFunction func = funcValue.getFunction();
                        FunctionType funcType = funcValue.getType();
                        List<Type> params = funcType.getParameters();

                        Value[] args = new Value[params.size()];
                        for (int i = 0; i < params.size(); i++) {
                            args[i] = stack.pop();
                        }

                        Value ret = func.execute(args);
                        if (ret != null) {
                            stack.push(ret);
                        }

                        programCounter++;
                    }

                    continue;
                }

                case CALL_CTX:
                {
                    int retAddr = programCounter+1;
                    ObjectValue currentCtx = objectReg;

                    FunctionValue func = (FunctionValue)stack.pop();
                    objectReg = (ObjectValue)stack.pop();

                    programCounter = func.getAddr();

                    scope = new Scope(scope);

                    // put arguments in values map
                    Map<String, Symbol> symbolMap = func.getSymbolMap();
                    List<String> names = new ArrayList<String>(symbolMap.keySet());

                    for (int i = names.size()-1; i >= 0; i--) {
                        String name = names.get(i);
                        Symbol sym = symbolMap.get(name);

                        Value arg = stack.pop();
                        scope.set(sym, arg);
                    }

                    stack.push(new ReturnAddressValue(retAddr));
                    stack.push(currentCtx);

                    continue;
                }

                case RETURN:
                {
                    ReturnInstruction load = (ReturnInstruction)inst;
                    Value retVal = null;
                    if (!load.isVoidFunc()) {
                        retVal = stack.pop();
                    }

                    ReturnAddressValue retAddr = (ReturnAddressValue)stack.pop();
                    programCounter = retAddr.getAddr();

                    if (!load.isVoidFunc()) {
                        stack.push(retVal);
                    }

                    scope = scope.getParentScope();
                    continue;
                }

                case RETURN_CTX:
                {
                    ReturnCtxInstruction load = (ReturnCtxInstruction)inst;
                    Value retVal = null;
                    if (!load.isVoidFunc()) {
                        retVal = stack.pop();
                    }

                    objectReg = (ObjectValue)stack.pop();
                    ReturnAddressValue retAddr = (ReturnAddressValue)stack.pop();
                    programCounter = retAddr.getAddr();

                    if (!load.isVoidFunc()) {
                        stack.push(retVal);
                    }

                    scope = scope.getParentScope();
                    continue;
                }

                case LOAD:
                {
                    LoadInstruction load = (LoadInstruction)inst;
                    Value value = scope.get(load.getSymbol());
                    if (value == null) {
                        throw new RuntimeException(load.getSymbol().getName() +
                                " is not defined.");
                    }
                    stack.push(value);
                    programCounter++;
                    continue;
                }

                case LOAD_OBJECTREG:
                {
                    stack.push(objectReg);
                    programCounter++;
                    continue;
                }

                case STORE:
                {
                    StoreInstruction store = (StoreInstruction)inst;
                    Value value = stack.pop();
                    scope.set(store.getSymbol(), value);
                    programCounter++;
                    continue;
                }

                case STORE_OBJECTREG:
                {
                    objectReg = (ObjectValue)stack.pop();
                    programCounter++;
                    continue;
                }

                case PUSH:
                {
                    PushInstruction push = (PushInstruction)inst;
                    stack.push(push.getValue());
                    programCounter++;
                    continue;
                }

                case CAST:
                {
                    CastInstruction cast = (CastInstruction)inst;
                    Value value = scope.get(cast.getFrom());
                    scope.set(cast.getTo(), value);
                    programCounter++;
                    continue;
                }

                case SWAP:
                {
                    Value a = stack.pop();
                    Value b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                    programCounter++;
                    continue;
                }

                case DUP:
                {
                    Value a = stack.pop();
                    stack.push(a);
                    stack.push(a);
                    programCounter++;
                    continue;
                }

                case JUMP:
                {
                    JumpInstruction jump = (JumpInstruction)inst;
                    programCounter = jump.getAddr();
                    continue;
                }

                case JUMPONFALSE:
                {
                    JumpOnFalseInstruction jumpOnFalse =
                        (JumpOnFalseInstruction)inst;

                    Value value = stack.pop();
                    BooleanValue b = (BooleanValue)value;
                    if (b.getBoolean()) {
                        programCounter++;
                    } else {
                        programCounter = jumpOnFalse.getAddr();
                    }
                    continue;
                }

                case ARRAY_ALLOCATE:
                {
                    ArrayAllocateInstruction alloc =
                        (ArrayAllocateInstruction)inst;

                    IntegerValue size = (IntegerValue)stack.pop();
                    stack.push(new ArrayValue(alloc.getType(), size.getInt()));
                    programCounter++;
                    continue;
                }

                case ARRAY_SET:
                {
                    try {
                        IntegerValue index = (IntegerValue)stack.pop();
                        Value value = stack.pop();
                        ArrayValue arr = (ArrayValue)stack.pop();

                        arr.set(index.getInt(), value);

                        stack.push(arr);

                        programCounter++;
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                case ARRAY_SET2:
                {
                    try {
                        IntegerValue index = (IntegerValue)stack.pop();
                        ArrayValue arr = (ArrayValue)stack.pop();
                        Value value = stack.pop();

                        arr.set(index.getInt(), value);

                        programCounter++;
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                case ARRAY_GET:
                {
                    IntegerValue index = (IntegerValue)stack.pop();
                    ArrayValue arr = (ArrayValue)stack.pop();

                    stack.push(arr.get(index.getInt()));

                    programCounter++;
                    continue;
                }

                case OBJECT_ALLOCATE:
                {
                    ObjectAllocateInstruction allocInst =
                        (ObjectAllocateInstruction)inst;

                    Value value = new ObjectValue(allocInst.getType());
                    stack.push(value);
                    programCounter++;
                    continue;
                }

                case OBJECT_GET:
                {
                    ObjectGetInstruction getInst = (ObjectGetInstruction)inst;

                    Value obj = stack.pop();
                    Value value = obj.getField(getInst.getField());
                    stack.push(value);
                    programCounter++;
                    continue;
                }

                case OBJECT_SET:
                {
                    ObjectSetInstruction getInst =
                        (ObjectSetInstruction)inst;

                    Value value =  stack.pop();
                    ObjectValue obj = (ObjectValue)stack.pop();
                    obj.setField(getInst.getField(), value);
                    programCounter++;
                    continue;
                }

                case ADD:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.addOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case SUB:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.subOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case MUL:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.mulOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case DIV:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.divOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case MOD:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.modOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case ISTYPE:
                {
                    IsTypeInstruction istype = (IsTypeInstruction)inst;
                    Value val = stack.pop();
                    if (istype.getType().isCompatible(val.getType())) {
                        stack.push(BooleanValue.TRUE);
                    } else {
                        stack.push(BooleanValue.FALSE);
                    }
                    programCounter++;
                    continue;
                }

                case EQUAL:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.equalOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case NOTEQUAL:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.notEqualOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case LT:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.ltOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case LTEQ:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.ltEqOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case GT:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.gtOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case GTEQ:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.gtEqOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case AND:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.andOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case OR:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.orOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }

                case XOR:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.xorOp(second);
                    stack.push(newValue);
                    programCounter++;
                    continue;
                }
            }

            break;
        }
    }

    public static void main(String[] args)
    throws Exception
    {
        String mode = "run";
        if (args.length == 1 && args[0].equals("-parse")) {
            mode = "parse";
        }

        Parser parser = new Parser(System.in);
        CodeGenerationVisitor visitor = new CodeGenerationVisitor();
        if ("parse".equals(mode)) {
            visitor.setDebug(true);
        }
        Interpreter interpreter = new Interpreter();

        // register native functions
        {
            NativeFunction func = new PrintFunction();
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        {
            NativeFunction func = new DumpFunction(interpreter);
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        {
            NativeFunction func = new DumpStackFunction(interpreter);
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        {
            NativeFunction func = new DumpObjectRegFunction(interpreter);
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        {
            NativeFunction func = new DumpScopeFunction(interpreter);
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        {
            NativeFunction func = new IntToStrFunction();
            Symbol sym = visitor.registerNativeFunction(func);
            interpreter.registerNativeFunction(sym, func);
        }

        // parse
        List<Node> nodes = parser.parse();

        // generate instructions
        for (Node node : nodes) {
            node.accept(visitor);
        }

        if ("run".equals(mode)) {
            // execute
            try {
                long start = System.currentTimeMillis();
                interpreter.run(visitor.getInstructions());
                System.out.println("pc: " + interpreter.programCounter);
                System.out.println("Execution finished in " +
                        (System.currentTimeMillis() - start) + "ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
                System.out.println("pc: " + interpreter.programCounter);
                interpreter.dumpStack();
            }
        }
        else if ("parse".equals(mode)) {
            visitor.dump();
        }
    }
}
