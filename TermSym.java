// Let’s talk about building parse trees first. To build parse trees, you must represent nodes in the
// parse tree. Here is an example data type to do this. It is called TermSym and is declared as a
// subclass of ParseTree. It directly represents a leaf node.

public class TermSym extends ParseTree {
    String termSym;

    public TermSym(String sym) {
        termSym = sym; // must be +, *, (, ), id
    }

    public String toString() {
        return termSym;
    }
}