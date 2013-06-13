package se.c0la.uglylang;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;

import se.c0la.uglylang.ast.Node;
import se.c0la.uglylang.ir.Instruction;
import se.c0la.uglylang.nativefunc.*;

public class Main
{
    public static void main(String[] args)
    throws Exception
    {
        if (args.length == 0) {
            System.out.println("usage: uli " +
                    "[-debug] " +
                    "[-ir] " +
                    "[-searchpath path] " +
                    "module");
            return;
        }

        ModuleCompiler compiler = new ModuleCompiler();

        String moduleName = null;
        boolean debug = false;
        boolean printInstructions = false;
        for (int i = 0; i < args.length; ) {
            if ("-searchpath".equals(args[i])) {
                compiler.addSearchPath(args[i+1]);
                i += 2;
            }
            else if ("-debug".equals(args[i])) {
                debug = true;
                compiler.setDebug(debug);
                i++;
            }
            else if ("-ir".equals(args[i])) {
                printInstructions = true;
                i++;
            }
            else {
                if (moduleName != null) {
                    System.out.println("You can only specify one module.");
                    return;
                }

                moduleName = args[i];
                i++;
            }
        }

        if (moduleName == null) {
            System.out.println("Please specify module to execute.");
            return;
        }

        Module module = null;
        try {
            module = compiler.compile(moduleName);
        }
        catch (ParseException e) {
            if (e.currentToken != null) {
                Token t = e.currentToken;
                System.out.printf("Parse error at line %d, col %d. ",
                        t.beginLine, t.beginColumn);

                System.out.print("Found " + e.tokenImage[t.kind] + ". ");

                System.out.print("Expected: ");
                String delim = "";
                for (int[] seq : e.expectedTokenSequences) {
                    System.out.print(delim);
                    for (int token : seq) {
                        System.out.print(e.tokenImage[token]);
                    }
                    delim = ", ";
                }
                System.out.println();
            } else {
                System.out.println(e.getMessage());
            }
            return;
        }
        catch (CodeGenerationException e) {
            System.out.printf("Error at line %d, col %d: ",
                    e.getLine(), e.getColumn());

            System.out.println(e.getMessage());

            return;
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (printInstructions) {
            int i = 0;
            for (Instruction inst : module.getInstructions()) {
                System.out.printf("%d %s\n", i++, inst.toString());
            }
            return;
        }
    }
}
