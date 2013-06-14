package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;
import se.c0la.uglylang.ir.*;
import se.c0la.uglylang.type.*;
import se.c0la.uglylang.nativefunc.*;

public class Interpreter
{
    public static class Scope
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

    private ExecutionEnvironment env;
    private Module module;

    private int programCounter;
    //private Stack<Value> stack;
    private Value[] stack;
    private int stackPointer;
    private Scope scope;

    private ObjectValue objectReg = ObjectValue.EMPTY;

    public Interpreter(ExecutionEnvironment env, Module module)
    {
        this.env = env;
        this.module = module;

        scope = new Scope(null);

        for (Map.Entry<Symbol, Value> entry :
                module.getPredefinedSymbols().entrySet()) {
            Symbol sym = entry.getKey();
            Value value = entry.getValue();
            if (value instanceof NativeFunctionValue) {
                ((NativeFunctionValue)value).getFunction().setInterpreter(this);
            }
            scope.set(sym, value);
        }

        this.programCounter = 0;
        this.stack = new Value[4096];
        this.stackPointer = 0;
    }

    public Scope getScope()
    {
        return scope;
    }

    public void dumpStack()
    {
        System.out.println("Dumping stack:");
        for (int i = 0; i < stackPointer; i++) {
            Value value = stack[i];
            if (value == null) {
                continue;
            }
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

    public Value call(FunctionValue func, Value[] args)
    {
        scope = new Scope(scope);

        int retAddr = programCounter;
        programCounter = func.getAddr();

        Map<String, Symbol> symbolMap = func.getSymbolMap();

        // put arguments in values map
        List<String> names = new ArrayList<String>();
        names.addAll(symbolMap.keySet());

        // pop args from stack and set symbols
        for (int i = 0; i < args.length; i++) {
            String name = names.get(i);
            Symbol sym = symbolMap.get(name);
            Value arg = args[i];
            scope.set(sym, arg);
        }

        // ad return addres to stack
        stack[stackPointer++] = (new ReturnAddressValue(retAddr, true));

        run();

        Value ret = null;
        Type returnType = func.getType().getReturnType();
        if (returnType != VoidType.TYPE) {
            ret = stack[--stackPointer];
        }

        return ret;
    }

    public Value callCtx(ObjectValue newCtx, FunctionValue func, Value[] args)
    {
        scope = new Scope(scope);

        int retAddr = programCounter;
        ObjectValue currentCtx = objectReg;

        programCounter = func.getAddr();
        objectReg = newCtx;

        Map<String, Symbol> symbolMap = func.getSymbolMap();

        // put arguments in values map
        List<String> names = new ArrayList<String>();
        names.addAll(symbolMap.keySet());

        // pop args from stack and set symbols
        for (int i = 0; i < args.length; i++) {
            String name = names.get(i);
            Symbol sym = symbolMap.get(name);
            Value arg = args[i];
            scope.set(sym, arg);
        }

        // ad return addres to stack
        stack[stackPointer++] = (new ReturnAddressValue(retAddr, true));
        stack[stackPointer++] = (currentCtx);

        run();

        Value ret = null;
        Type returnType = func.getType().getReturnType();
        if (returnType != VoidType.TYPE) {
            ret = stack[--stackPointer];
        }

        return ret;
    }

    public void run()
    {
        List<Instruction> instructions = module.getInstructions();

        int instrCount = 0;
        while (true) {
            instrCount++;

            if (programCounter >= instructions.size()) {
                //System.out.println();
                //System.out.println("Execution halted after " + instrCount +
                //        " instructions.");
                return;
            }

            Instruction inst = instructions.get(programCounter);
            //System.out.println(programCounter + " " + inst);
            //dumpStack();
            switch (inst.getOpCode()) {
                case CALL:
                {
                    int retAddr = programCounter+1;

                    Value value = stack[--stackPointer];

                    if (value instanceof FunctionValue) {
                        FunctionValue func = (FunctionValue)value;
                        Map<String, Symbol> symbolMap = func.getSymbolMap();
                        int argCount = symbolMap.size();

                        // put arguments in values map
                        List<String> names = new ArrayList<String>();
                        names.addAll(symbolMap.keySet());

                        // call to other module
                        if (func.getModule() != module) {
                            Value[] args = new Value[argCount];
                            for (int i = argCount - 1; i >= 0; i--) {
                                String name = names.get(i);
                                Symbol sym = symbolMap.get(name);

                                Value arg = stack[--stackPointer];
                                args[i] = arg;
                            }

                            Value res = env.call(func, args);
                            if (res != null) {
                                stack[stackPointer++] = (res);
                            }
                            programCounter++;
                        }

                        // normal call
                        else {
                            scope = new Scope(scope);

                            // jump
                            programCounter = func.getAddr();

                            // pop args from stack and set symbols
                            for (int i = argCount - 1; i >= 0; i--) {
                                String name = names.get(i);
                                Symbol sym = symbolMap.get(name);

                                Value arg = stack[--stackPointer];
                                scope.set(sym, arg);
                            }

                            // ad return addres to stack
                            stack[stackPointer++] =
                                (new ReturnAddressValue(retAddr, false));
                        }
                    }
                    else if (value instanceof NativeFunctionValue) {
                        NativeFunctionValue funcValue = (NativeFunctionValue)value;
                        NativeFunction func = funcValue.getFunction();
                        FunctionType funcType = funcValue.getType();
                        List<Type> params = funcType.getParameters();

                        Value[] args = new Value[params.size()];
                        for (int i = 0; i < params.size(); i++) {
                            args[i] = stack[--stackPointer];
                        }

                        Value ret = func.execute(args);
                        if (ret != null) {
                            stack[stackPointer++] = (ret);
                        }

                        programCounter++;
                    }

                    continue;
                }

                case CALL_CTX:
                {
                    int retAddr = programCounter+1;
                    ObjectValue currentCtx = objectReg;

                    Value value = stack[--stackPointer];
                    if (value instanceof FunctionValue) {
                        FunctionValue func = (FunctionValue)value;
                        Map<String, Symbol> symbolMap = func.getSymbolMap();
                        int argCount = symbolMap.size();

                        // put arguments in values map
                        List<String> names = new ArrayList<String>();
                        names.addAll(symbolMap.keySet());

                        if (func.getModule() != module) {
                            Value obj = stack[--stackPointer];

                            Value[] args = new Value[argCount];
                            for (int i = argCount - 1; i >= 0; i--) {
                                String name = names.get(i);
                                Symbol sym = symbolMap.get(name);

                                Value arg = stack[--stackPointer];
                                args[i] = arg;
                            }

                            Value res = env.callCtx(obj, func, args);
                            if (res != null) {
                                stack[stackPointer++] = (res);
                            }
                            programCounter++;
                        }
                        else {
                            scope = new Scope(scope);

                            objectReg = (ObjectValue)stack[--stackPointer];

                            programCounter = func.getAddr();

                            for (int i = argCount-1; i >= 0; i--) {
                                String name = names.get(i);
                                Symbol sym = symbolMap.get(name);

                                Value arg = stack[--stackPointer];
                                scope.set(sym, arg);
                            }

                            stack[stackPointer++] =
                                (new ReturnAddressValue(retAddr, false));
                            stack[stackPointer++] = (currentCtx);
                        }
                    }
                    else if (value instanceof NativeFunctionValue) {
                        NativeFunctionValue funcValue = (NativeFunctionValue)value;

                        // skip object value
                        // (ObjectValue)stack[--stackPointer];
                        stackPointer--;

                        NativeFunction func = funcValue.getFunction();
                        FunctionType funcType = funcValue.getType();
                        List<Type> params = funcType.getParameters();

                        Value[] args = new Value[params.size()];
                        for (int i = 0; i < params.size(); i++) {
                            args[i] = stack[--stackPointer];
                        }

                        Value ret = func.execute(args);
                        if (ret != null) {
                            stack[stackPointer++] = (ret);
                        }

                        programCounter++;
                    }

                    continue;
                }

                case RETURN:
                {
                    ReturnInstruction ret = (ReturnInstruction)inst;
                    Value retVal = null;
                    if (!ret.isVoidFunc()) {
                        retVal = stack[--stackPointer];
                    }

                    ReturnAddressValue retAddr = (ReturnAddressValue)stack[--stackPointer];
                    programCounter = retAddr.getAddr();

                    if (!ret.isVoidFunc()) {
                        stack[stackPointer++] = (retVal);
                    }

                    scope = scope.getParentScope();

                    if (retAddr.getExitModule()) {
                        break;
                    }

                    continue;
                }

                case RETURN_CTX:
                {
                    ReturnCtxInstruction ret = (ReturnCtxInstruction)inst;
                    Value retVal = null;
                    if (!ret.isVoidFunc()) {
                        retVal = stack[--stackPointer];
                    }

                    objectReg = (ObjectValue)stack[--stackPointer];
                    ReturnAddressValue retAddr =
                        (ReturnAddressValue)stack[--stackPointer];
                    programCounter = retAddr.getAddr();

                    if (!ret.isVoidFunc()) {
                        stack[stackPointer++] = (retVal);
                    }

                    scope = scope.getParentScope();

                    if (retAddr.getExitModule()) {
                        break;
                    }

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
                    stack[stackPointer++] = (value);
                    programCounter++;
                    continue;
                }

                case LOAD_OBJECTREG:
                {
                    stack[stackPointer++] = (objectReg);
                    programCounter++;
                    continue;
                }

                case STORE:
                {
                    StoreInstruction store = (StoreInstruction)inst;
                    Value value = stack[--stackPointer];
                    scope.set(store.getSymbol(), value);
                    programCounter++;
                    continue;
                }

                case STORE_OBJECTREG:
                {
                    objectReg = (ObjectValue)stack[--stackPointer];
                    programCounter++;
                    continue;
                }

                case PUSH:
                {
                    PushInstruction push = (PushInstruction)inst;
                    stack[stackPointer++] = (push.getValue());
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
                    Value a = stack[--stackPointer];
                    Value b = stack[--stackPointer];
                    stack[stackPointer++] = (a);
                    stack[stackPointer++] = (b);
                    programCounter++;
                    continue;
                }

                case DUP:
                {
                    Value a = stack[--stackPointer];
                    stack[stackPointer++] = (a);
                    stack[stackPointer++] = (a);
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

                    Value value = stack[--stackPointer];
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

                    IntegerValue size = (IntegerValue)stack[--stackPointer];
                    stack[stackPointer++] = (new ArrayValue(alloc.getType(), size.getInt()));
                    programCounter++;
                    continue;
                }

                case ARRAY_SET:
                {
                    try {
                        IntegerValue index = (IntegerValue)stack[--stackPointer];
                        Value value = stack[--stackPointer];
                        ArrayValue arr = (ArrayValue)stack[--stackPointer];

                        arr.set(index.getInt(), value);

                        stack[stackPointer++] = (arr);

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
                        IntegerValue index = (IntegerValue)stack[--stackPointer];
                        ArrayValue arr = (ArrayValue)stack[--stackPointer];
                        Value value = stack[--stackPointer];

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
                    IntegerValue index = (IntegerValue)stack[--stackPointer];
                    ArrayValue arr = (ArrayValue)stack[--stackPointer];

                    stack[stackPointer++] = (arr.get(index.getInt()));

                    programCounter++;
                    continue;
                }

                case OBJECT_ALLOCATE:
                {
                    ObjectAllocateInstruction allocInst =
                        (ObjectAllocateInstruction)inst;

                    Value value = new ObjectValue(allocInst.getType());
                    stack[stackPointer++] = (value);
                    programCounter++;
                    continue;
                }

                case OBJECT_GET:
                {
                    ObjectGetInstruction getInst = (ObjectGetInstruction)inst;

                    Value obj = stack[--stackPointer];
                    Value value = obj.getField(getInst.getField());
                    stack[stackPointer++] = (value);
                    programCounter++;
                    continue;
                }

                case OBJECT_SET:
                {
                    ObjectSetInstruction getInst =
                        (ObjectSetInstruction)inst;

                    Value value =  stack[--stackPointer];
                    ObjectValue obj = (ObjectValue)stack[--stackPointer];
                    obj.setField(getInst.getField(), value);
                    programCounter++;
                    continue;
                }

                case ADD:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.addOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case SUB:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.subOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case MUL:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.mulOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case DIV:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.divOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case MOD:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.modOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case ISTYPE:
                {
                    IsTypeInstruction istype = (IsTypeInstruction)inst;
                    Value val = stack[--stackPointer];
                    if (istype.getType().isCompatible(val.getType())) {
                        stack[stackPointer++] = (BooleanValue.TRUE);
                    } else {
                        stack[stackPointer++] = (BooleanValue.FALSE);
                    }
                    programCounter++;
                    continue;
                }

                case EQUAL:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.equalOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case NOTEQUAL:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.notEqualOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case LT:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.ltOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case LTEQ:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.ltEqOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case GT:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.gtOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case GTEQ:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.gtEqOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case AND:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.andOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case OR:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.orOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }

                case XOR:
                {
                    Value first = stack[--stackPointer];
                    Value second = stack[--stackPointer];
                    Value newValue = first.xorOp(second);
                    stack[stackPointer++] = (newValue);
                    programCounter++;
                    continue;
                }
            }

            break;
        }
    }
}
