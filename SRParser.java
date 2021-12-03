import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
// due to compiling on linux and windows, you may need to:
// javac --release 8 *.java in order to compile
class SRParser {

    String[][] grammar;
    String[][] actionTable;
    String[][] gotoTable;
    String[] inputTokens;
    String strFromGrammar;
    String lhs;
    String[] rhs;
    String gotoChoices;
    String[] terminals = { "id", "+", "*", "(", ")", "$" };
    String action;
    String actionValue;
    ParseStack stack;
    ParseStack pts = new ParseStack();
    int currentTokenIdx = 0;
    String stateSym;
    String grammarSym;
    String tempStack;
    String gotoVal;
    char valLHS;
    int lenRHS;
    boolean shifted;

    public SRParser(String[][] grammar, String[][] actionTable, String[][] gotoTable) {
        this.grammar = grammar;
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        strFromGrammar = new String();
        lhs = new String();
        rhs = new String[grammar.length];
        for (int i = 0; i < grammar.length; i++) {
            lhs += grammar[i][0];
            rhs[i] = new String();
            for (int j = 2; j < grammar[i].length; j++) {
                rhs[i] += grammar[i][j] + " ";
            }

        }
    }

    public void createGoToChoices() {
        gotoChoices = new String();
        for (int i = 0; i < lhs.length(); i++) {
            if (gotoChoices.indexOf(lhs.charAt(i)) == -1) {
                gotoChoices += lhs.charAt(i);
            }
        }
    }
    // input is a String[] inputTokens
    // if inputTokens belongs to the language, return "Accepted"
    // otherwise, return "Rejected"
    // Steps:
    // 1. Initialize the stack by pushing the start symbol onto the stack
    // 2. Read the next token from the input
    // 3. If the token is a nonterminal, push it onto the stack
    // 4. If the token is a terminal, pop the stack and check if the popped symbol
    // is the same as the token
    // 5. If the popped symbol is the same as the token, continue to step 2
    // 6. If the popped symbol is not the same as the token, pop the stack and check
    // if the popped symbol is a nonterminal

    public void print(Object o) {
        // so tired of typing
        System.out.println(o);

    }

    public void parse(String[] inputTokens) {
        this.inputTokens = inputTokens;
        shift();
        while (!actionStr(action).equals("a")) {
            if (actionStr(action).equals("S")) {
                // print("shift");
                shift();
            } else if (actionStr(action).equals("R")) {
                // print("reduce");
                reduce();
            } else if (actionStr(action).equals("")) {
                reject();
            }
        }
        accept();
    }

    public void shift() {
        tempStack = stack.toString();
        pts.tree.push(inputTokens[currentTokenIdx]);
        System.out.format("%-14s%-20s%10s%12s%10s%10s%10s%5s%18s%-7s%-14s\n",
                stack.toString(), // stack
                Arrays.toString(sliceInputTokens(currentTokenIdx)), // input tokens
                "[" + stateSym + "," + inputTokens[currentTokenIdx] + "]", // action lookup
                action, // action value
                "", // value of lhs
                "", // length of rhs
                "", // temp stack
                "", // goto lookup
                "", // goto value
                inputTokens[currentTokenIdx] + actionVal(action), // stack action
                pts.tree.toString());   // parse tree stack
        stack.push(new PstackEntry(actionVal(action), inputTokens[currentTokenIdx]));
        if (currentTokenIdx < inputTokens.length - 1) {
            currentTokenIdx++;
        }
        stateSym = stack.top().stateSym;
        grammarSym = stack.top().grammarSym;
        action = action(stateSym, inputTokens[currentTokenIdx]);
    }


    public void reduce() {

        int actionVali = actionVali(actionVal(action));
        valLHS = lhs.charAt(actionVali - 1);
        lenRHS = spaces(rhs[actionVali - 1]);
        tempStack = stack.toString();
        for (int i = 0; i < lenRHS; i++) 
        {
            stack.pop();
        }

        gotoVal = gotoVal(stack.toString(), valLHS, lenRHS);
        // pts.tree.push(lhs.charAt(actionVali - 1) + " " + grammarSym);        
        // pts.tree.push("[" + String.valueOf(lhs.charAt(actionVali - 1)) + grammarSym + "]");
        pts.tree.push(String.valueOf(valLHS));
        // pts.tree.push(grammarSym);
        // print("[" + String.valueOf(lhs.charAt(actionVali - 1)) + "]\n");
        System.out.format("%-14s%-20s%10s%12s%10s%-5s%-10s%-13s%-7s%-15s%-10s\n",
                tempStack, // stack
                Arrays.toString(sliceInputTokens(currentTokenIdx)), // input tokens
                "[" + stateSym + "," + inputTokens[currentTokenIdx] + "]", // action lookup
                action, // action value
                valLHS, // value of lhs
                lenRHS, // length of rhs
                stack.toString(), // temp stack
                "[" + stack.toString().substring(stack.toString().length() - 1) + "," + valLHS + "]", // goto lookup
                gotoVal, // goto value
                valLHS + gotoVal, // stack action
                // lhs.charAt(actionVali - 1) + " " + grammarSym); // parse tree stack
                pts.tree.toString());
                // pts.tree.pop();

        stack.push(new PstackEntry(gotoVal, String.valueOf(valLHS)));
        stateSym = stack.top().stateSym;
        grammarSym = stack.top().grammarSym;
        action = action(gotoVal, inputTokens[currentTokenIdx]);
    }

    private void reject() {
        print("Unable to parse; rejected");
        System.exit(-1);
    }

    private void accept() {
        print("Accepted; parsing complete");
        char tab = ' ';

        for (int i = 0; i < pts.tree.size(); i++) {
            print(pts.tree.get(i));
            print(tab);
            System.out.print(tab);
            for (int j = 0; j < i; j++) {
                if (!pts.tree.get(i).equals("id"))
                {
                    System.out.print(tab);
                }
            }
        }
            // System.out.print(tab);
            // print(pts.tree.get(i));
        
        print(pts.tree);
        System.exit(0);
    }

    public int spaces(String rhs) {
        int spaceCount = 0;
        for (char c : rhs.toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            }
        }
        return spaceCount;
    }

    public String[] sliceInputTokens(int i) {
        String[] temp = new String[inputTokens.length - i];
        for (int j = 0; j < inputTokens.length - i; j++) {
            temp[j] = inputTokens[j + i];
        }

        return temp;
    }

    public String gotoVal(String tempStack, char lhs, int lenRHS) {
        int idx = tempStack.lastIndexOf(lhs);
        // print("substring: " + idx);
        String temp = tempStack.substring(tempStack.length() - 1);
        // print("gotoValtemp: " + temp);
        int row = Integer.parseInt(temp);
        String l = String.valueOf(lhs);
        // print("lhs:" + lhs);
        if (l.equals("E")) {
            // print("gotoVal: " + gotoTable[row][0]);
            gotoVal = gotoTable[row][0];
        } else if (l.equals("T")) {
            // print("gotoVal: " + gotoTable[row][1]);
            gotoVal = gotoTable[row][1];

        } else if (l.equals("F")) {
            // print("gotoVal: " + gotoTable[row][2]);
            gotoVal = gotoTable[row][2];
        } else {
            print("gotoVal: broken....");
        }
        return gotoVal;
    }

    public void shiftOps()
    {
        LinkedList<String> ops = new LinkedList<String>();
        for (int i = 0; i < pts.tree.size(); i++)
        {
            if (pts.tree.get(i).equals("+") || pts.tree.get(i).equals("-") || pts.tree.get(i).equals("*") || pts.tree.get(i).equals("/"))
            {
                ops.push(pts.tree.get(i));
                pts.tree.set(i, "op");
            }
        }
        for (int i = 0; i < pts.tree.size(); i++)
        {
            if (pts.tree.get(i).equals("op"))
            {
                pts.tree.set(i, ops.pop());
            }
        }
    }

    public String setBrackets()
    {
        String temp = "[";
        for (int i = 0; i < pts.tree.size(); i++)
        {
            temp += pts.tree.get(i);
            if (pts.tree.get(i).equals("id"))
            {
                temp+= "]";
            }
            else
            {
                temp += "[";
            }
        }
        temp += "]";
        return temp;
    }

    public String action(String token, String string) {
        int row = Integer.parseInt(token);
        for (int i = 0; i < terminals.length; i++) {
            if (string.equals(terminals[i])) {
                action = actionTable[row][i];
                if (action.equals("accept")) 
                {
                    shiftOps();
                    System.out.format("%-14s%-20s%10s%10s%10s%10s%10s%10s%5s%8s%-18s\n",
                stack.toString(), // stack
                Arrays.toString(sliceInputTokens(currentTokenIdx)), // input tokens
                "[" + stateSym + "," + inputTokens[currentTokenIdx] + "]", // action lookup
                action, // action value
                "", // value of lhs
                "", // length of rhs
                "", // temp stack
                "", // goto lookup
                "", // goto value
                "", // stack action
                pts.tree.toString()); // parse tree stack
                    // accept();
                }
                return action;
            }
        }
        return "error";
    }

    public String actionVal(String action) {
        if (action.equals("error")) {
            return "error";
        }
        String[] actionSplit = action.split("");
        return actionSplit[actionSplit.length - 1];
    }

    public int actionVali(String action) {

        if (action.equals("error")) {
            return -1;
        }
        String[] actionSplit = action.split("");
        int actionVali = Integer.parseInt(actionSplit[actionSplit.length - 1]);
        return actionVali;
    }

    public String actionStr(String action) {
        if (action.equals("error")) {
            return "error";
        }
        String[] actionSplit = action.split("");
        return actionSplit[0];
    }

    public String treeVal(String tok) {
        if (tok.equals("error")) {
            return "error";
        }
        String tokSplit = tok.replaceAll("\\d", "");
        return tokSplit;
    }

    public boolean isTerminal(String token) {
        for (String t : terminals) {
            if (token.equals(t)) {
                return true;
            }
        }
        return false;
    }
    public boolean isOp(String token) {
        for (String t : "+*$".split("")) {
            if (token.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        String[][] grammar = {
                { "E", "->", "E", "+", "T" },
                { "E", "->", "T" },
                { "T", "->", "T", "*", "F" },
                { "T", "->", "F" },
                { "F", "->", "(", "E", ")" },
                { "F", "->", "id" }
        };
        String[][] actionTable = { // action table
                { "S5", "", "", "S4", "", "" }, // 0
                { "", "S6", "", "", "", "accept" }, // 1
                { "", "R2", "S7", "", "R2", "R2" }, // 2
                { "", "R4", "R4", "", "R4", "R4" }, // 3
                { "S5", "", "", "S4", "", "" }, // 4
                { "", "R6", "R6", "", "R6", "R6" }, // 5
                { "S5", "", "", "S4", "", "" }, // 6
                { "S5", "", "", "S4", "", "" }, // 7
                { "", "S6", "", "", "S11", "" }, // 8
                { "", "R1", "S7", "", "R1", "R1" }, // 9
                { "", "R3", "R3", "", "R3", "R3" }, // 10
                { "", "R5", "R5", "", "R5", "R5" }, // 11
        };
        String[][] gotoTable = {
                { "1", "2", "3" }, // 0
                { "", "", "" }, // 1
                { "", "", "" }, // 2
                { "", "", "" }, // 3
                { "8", "2", "3" }, // 4
                { "", "", "" }, // 5
                { "", "9", "3" }, // 6
                { "", "", "10" }, // 7
                { "", "", "" }, // 8
                { "", "", "" }, // 9
                { "", "", "" }, // 10
                { "", "", "" }, // 11
        };
        String[] inputTokens = { "id", "+", "id", "*", "id" };
        inputTokens = Arrays.copyOf(inputTokens, inputTokens.length + 1);
        inputTokens[inputTokens.length - 1] = "$";
        System.out.println("Input: " + Arrays.toString(inputTokens));

        SRParser parser = new SRParser(grammar, actionTable, gotoTable);
        for (String[] row : grammar) {
            System.out.println(Arrays.toString(row));
        }
        parser.createGoToChoices();
        System.out.println("LHS: " + parser.lhs);
        for (String s : parser.rhs) {
            System.out.println(s);
        }
        System.out.println("GoTo choices :\n" + parser.gotoChoices);
        System.out.format("%-14s%-20s%10s%13s%10s%10s%10s%10s%7s%10s%20s\n", "     ", "input", "action", "action",
                "value", "length", "temp", "goto", "goto", "stack", "");
        System.out.format("%-14s%-20s%10s%13s%10s%10s%10s%10s%7s%10s%20s\n", "Stack", "tokens", "lookup", "value",
                "of LHS", "of RHS", "stack", "lookup", "value", "action", "parse tree stack");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------");
        parser.stack = new ParseStack();
        parser.stack.push(new PstackEntry("0", ""));
        parser.stateSym = parser.stack.top().stateSym;
        parser.grammarSym = parser.stack.top().grammarSym;
        parser.action = parser.action(parser.stateSym, inputTokens[parser.currentTokenIdx]);
        parser.parse(inputTokens);
        // parser.parseTreeStack.printTree();

    }
}
