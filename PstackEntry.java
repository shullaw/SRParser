public class PstackEntry {
     
    String stateSym;
    String grammarSym;

    public PstackEntry(String stateSym, String grammarSym) {
        this.stateSym = stateSym;
        this.grammarSym = grammarSym;
    }
    
    public String toString() {
        return grammarSym + stateSym;
    }
}
