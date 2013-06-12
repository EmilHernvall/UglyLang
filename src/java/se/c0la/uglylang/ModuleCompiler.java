package se.c0la.uglylang;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.Reader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import se.c0la.uglylang.ast.Node;
import se.c0la.uglylang.type.Value;
import se.c0la.uglylang.type.NativeFunctionValue;
import se.c0la.uglylang.nativefunc.NativeFunction;
import se.c0la.uglylang.nativefunc.PrintFunction;
import se.c0la.uglylang.nativefunc.IntToStrFunction;

public class ModuleCompiler
{
    private List<File> searchPath;
    private List<NativeFunction> functions;

    private boolean debug = false;

    public ModuleCompiler()
    {
        searchPath = new ArrayList<File>();

        functions = new ArrayList<NativeFunction>();
        functions.add(new PrintFunction());
        functions.add(new IntToStrFunction());
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public void addSearchPath(String dirName)
    throws IOException
    {
        File dir = new File(dirName);
        if (!dir.exists()) {
            throw new FileNotFoundException(dirName + " does not exist.");
        }
        if (!dir.isDirectory()) {
            throw new IOException(dirName + " is not a directory.");
        }

        searchPath.add(dir);
    }

    private Reader loadModule(String moduleName)
    throws IOException
    {
        String moduleFile = moduleName + ".ul";
        for (File dir : searchPath) {
            File file = new File(dir, moduleFile);
            if (file.exists()) {
                return new InputStreamReader(new FileInputStream(file));
            }
        }

        throw new FileNotFoundException(moduleName + " not found in search path.");
    }

    public Module compile(String moduleName)
    throws IOException, ParseException, CodeGenerationException
    {
        Parser parser;

        // initial pass to find types and imports/exports
        parser = new Parser(loadModule(moduleName));
        parser.setPass(Parser.Pass.FIRST);
        parser.parse();

        // compile dependencies
        List<String> imports = parser.getImports();
        Map<String, Module> deps = new HashMap<String, Module>();
        for (String dep : imports) {
            Module depMod = compile(dep);
            deps.put(dep, depMod);
        }

        if (debug) {
            System.out.println("Compiling module " + moduleName);
        }

        // second pass with full type checking and import processing
        parser = new Parser(loadModule(moduleName));
        parser.setDependencies(deps);
        parser.setPass(Parser.Pass.SECOND);
        List<Node> nodes = parser.parse();

        // generate instructions
        CodeGenerationVisitor visitor = new CodeGenerationVisitor();
        visitor.setDebug(debug);
        visitor.setImports(deps);
        Map<Symbol, Value> predef = new HashMap<Symbol, Value>();
        for (NativeFunction func : functions) {
            Symbol sym = visitor.registerNativeFunction(func);
            predef.put(sym, new NativeFunctionValue(func.getType(), func));
        }

        for (Node node : nodes) {
            node.accept(visitor);
        }

        // iterate over all instructions and resolve labels to addresses in jumps
        visitor.setLabels();

        Map<String, Symbol> exports = visitor.getExports();

        Module module = new Module();
        module.setName(moduleName);
        module.setInstructions(visitor.getInstructions());
        module.setTypes(parser.getTypes());
        module.setDependencies(deps);
        module.setExports(exports);
        module.setPredefinedSymbols(predef);

        return module;
    }
}
