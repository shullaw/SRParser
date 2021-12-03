import java.util.*;

public class ParseStack{

    LinkedList<String> tree = new LinkedList<String>();

    LinkedList<PstackEntry> stk = null;
    
    public ParseStack() {
        stk = new LinkedList<PstackEntry>();
    }
    
    public PstackEntry pop() {
        PstackEntry top = stk.getFirst();
        stk.removeFirst();
        return top;
    }
    
    public void popNum(int n) {
        for (int i = 1; i <= n; i++)
            this.pop();
    }
    
    public void push(PstackEntry e) {
        stk.addFirst(e);
    }
    
    public PstackEntry top() {
        return stk.getFirst();
    }
    
    public String toString() {
        LinkedList<PstackEntry> reversed = new LinkedList<PstackEntry>();
        for (PstackEntry e : stk)
            reversed.addFirst(e);
        String returnString = "";
        for (PstackEntry e : reversed)
            returnString += e.toString();
        return returnString;
    }
}
