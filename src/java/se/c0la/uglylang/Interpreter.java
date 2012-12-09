package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;
import se.c0la.uglylang.ir.*;
import se.c0la.uglylang.type.*;
import se.c0la.uglylang.nativefunc.*;

public class Interpreter
{
    private int programCounter;
    private Stack<Value> stack;
    private Map<Symbol, Value> values;

    public Interpreter()
    {
        values = new HashMap<Symbol, Value>();
    }

    public void dumpStack()
    {
        System.out.println("Dumping stack:");
        for (Value value : stack) {
            System.out.println(value);
        }
        System.out.println();
    }

    public void registerNativeFunction(Symbol symbol, NativeFunction func)
    {
        values.put(symbol, new NativeFunctionValue(func.getType(), func));
    }

    public void run(List<Instruction> instructions)
    {
        programCounter = 0;
        stack = new Stack<Value>();
        int instrCount = 0;
        while (true) {
            instrCount++;

            if (programCounter >= instructions.size()) {
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
                    CallInstruction call = (CallInstruction)inst;
                    int retAddr = programCounter+1;

                    Value value = stack.pop();

                    if (value instanceof FunctionValue) {
                        FunctionValue func = (FunctionValue)value;
                        programCounter = func.getAddr();

                        // put arguments in values map
                        Map<String, Symbol> symbolMap = func.getSymbolMap();
                        List<String> names = new ArrayList<String>(symbolMap.keySet());

                        for (int i = names.size()-1; i >= 0; i--) {
                            String name = names.get(i);
                            Symbol sym = symbolMap.get(name);

                            Value arg = stack.pop();
                            values.put(sym, arg);
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

                case RETURN:
                {
                    Value retVal = stack.pop();
                    try {
                        ReturnAddressValue retAddr = (ReturnAddressValue)stack.pop();
                        programCounter = retAddr.getAddr();
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
                    stack.push(retVal);
                    continue;
                }

                case LOAD:
                {
                    LoadInstruction load = (LoadInstruction)inst;
                    Value value = values.get(load.getSymbol());
                    if (value == null) {
                        throw new RuntimeException(load.getSymbol().getName() +
                                " is not defined.");
                    }
                    stack.push(value);
                    programCounter++;
                    continue;
                }

                case STORE:
                {
                    StoreInstruction store = (StoreInstruction)inst;
                    Value value = stack.pop();
                    values.put(store.getSymbol(), value);
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
                    try {
                        BooleanValue b = (BooleanValue)value;
                        if (b.getBoolean()) {
                            programCounter++;
                        } else {
                            programCounter = jumpOnFalse.getAddr();
                        }
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                case ARRAY_ALLOCATE:
                {
                    try {
                        IntegerValue size = (IntegerValue)stack.pop();
                        stack.push(new ArrayValue(new ArrayType(new VoidType()),
                                    size.getInt()));
                        programCounter++;
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
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

                case ARRAY_GET:
                {
                    try {
                        IntegerValue index = (IntegerValue)stack.pop();
                        ArrayValue arr = (ArrayValue)stack.pop();

                        stack.push(arr.get(index.getInt()));

                        programCounter++;
                    }
                    catch (ClassCastException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                case NTUPLE_ALLOCATE:
                {
                    NamedTupleAllocateInstruction allocInst =
                        (NamedTupleAllocateInstruction)inst;
                    Value value = new NamedTupleValue(allocInst.getType(),
                            allocInst.getFieldsMap());
                    stack.push(value);
                    programCounter++;
                    continue;
                }

                case NTUPLE_GET:
                {
                    NamedTupleGetInstruction getInst =
                        (NamedTupleGetInstruction)inst;

                    NamedTupleValue tuple = (NamedTupleValue)stack.pop();
                    Symbol sym = tuple.getField(getInst.getField());
                    Value value = values.get(sym);
                    stack.push(value);
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

                case EQUAL:
                {
                    Value first = stack.pop();
                    Value second = stack.pop();
                    Value newValue = first.equalOp(second);
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
        Interpreter interpreter = new Interpreter();

        // register native functions
        {
            NativeFunction func = new PrintFunction();
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
                System.out.println("Execution finished in " +
                        (System.currentTimeMillis() - start) + "ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
                interpreter.dumpStack();
            }
        }
        else if ("parse".equals(mode)) {
            visitor.dump();
        }
    }
}
