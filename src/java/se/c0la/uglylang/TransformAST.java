package se.c0la.uglylang;

import java.util.*;

import se.c0la.uglylang.ast.*;

public class TransformAST
{
    public TransformAST()
    {

    }

    public static void main( String[] args )
    throws ParseException, TokenMgrError {
        Parser parser = new Parser(System.in);
        List<Node> nodes = parser.parse();

        for (Node node : nodes) {
            System.out.println(node);
        }

        TransformAST transformer = new TransformAST();
    }
}
