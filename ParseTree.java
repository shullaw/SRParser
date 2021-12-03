import java.io.PrintStream;
/*  Data structures to support parse trees
We need data structures to support the actions just described. The data structures are needed to
support two types of operations.
1. We need to represent parse trees and subtrees.
2. We need to represent a stack of subtrees. This is needed to generate the information in the
last column of the output.
*/
// // class to represent a parse tree
abstract public class ParseTree{
    PrintStream cout = System.out;

    public void printTree() {
        printTreeWork(0);
    }

    public void printTreeWork(int indentLevel) {
        String outString = "";
        for (int i = 0; i < indentLevel; i++)
           outString += " ";
        cout.println(outString + this);
    }
}

/*The above class has two subclasses: TermSym (mentioned above) and NonLeafTree (not
shown). NonLeafTree is used to represent E, T, and F. This is only one possible way to represent
parse trees in Java.
If the node in the parse tree is a terminal node, then the field termsym will be set to either +,
*, (, ), or id.
You will need to write operations to support the parse tree stack*/