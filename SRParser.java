import java.io.*;

class SRParser {

    int m;
    int currentState = 0;
    int currentTokenIdx = 0;
    int step = 0;
    int lenRHS = 0;
    int gotoValue;
    String currentToken;
    String inputTokensString;
    String[] actionSymbols = { "id", "+", "*", "(", ")", "$" };
    String[] gotoSymbols = { "E", "T", "F" };
    String[] actionLookUp = { "0", "" };
    String[] gotoLookUp = {"",""};
    String[][] grammar;
    String[] lhs;
    String[] rhs;
    String actionValue;
    String actionString; // actually an integer!
    String actionLookUpString;
    String stackAction;
    String action;
    String[][] actionTable;
    String[][] gotoTable;
    ParseStack pStack = new ParseStack();
    String[] inputTokens;
    String[] parseTreeStack;
    String tempStack;
    String gotoString;



    public SRParser(String[][] grammar, String[][] actionTable, String[][] gotoTable, String[] input) {
        this.grammar = grammar;
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        inputTokens = new String[input.length + 1];
        for (int i = 0; i < input.length; i++) {
            inputTokens[i] = input[i];
        }
        inputTokens[inputTokens.length - 1] = "$";
        parseTreeStack = new String[input.length];
    }

    public void splitGrammar() {
    m = grammar.length;
    System.out.println("m = " + m);
    lhs = new String[m];
    rhs = new String[m];
    // parse the grammar into lhs and rhs
    for (int i = 0; i < m; i++) {
    lhs[i] = grammar[i][0];
    rhs[i] = grammar[i][2];
    for (int j = 3; j < grammar[i].length; j++) {
    rhs[i] += grammar[i][j];
    }
    }
    // print lhs and rhs
    for (int i = 0; i < m; i++) {
    System.out.print(i + ": " + lhs[i] + "->");
    System.out.println(rhs[i]);
    }
    }

    public void printActionTable() {
        System.out.println("Action Table");
        for (int i = 0; i < actionTable.length; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < actionTable[i].length; j++) {
                System.out.print(actionTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printGotoTable() {
        System.out.println("Goto Table");
        for (int i = 0; i < gotoTable.length; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < gotoTable[i].length; j++) {
                System.out.print(gotoTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printInputTokens() {
        // System.out.print("Input Tokens: ");
        for (int i = 0; i < inputTokens.length; i++) {
            System.out.print(inputTokens[i] + " ");

        }
        // System.out.println();
    }

    public String inputTokensToString() {
        String input = "";
        for (int i = currentTokenIdx; i < inputTokens.length; i++) {
            input += inputTokens[i];
        }
        return input;
    }

    public int search(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }


    public void shiftPrint(){
        System.out.format("%20s%20s%10s%10s%10s%10s%10s%10s%10s%15s%20s\n", pStack.toString(), inputTokensString,
        actionLookUpString, actionValue, "", "", "", "", "",
        "push " + stackAction, parseTreeStack[step]);
        step++;
    }

    public void shift(){
        actionString = actionValue.replaceAll("\\D+", "");
        stackAction = pStack.top().grammarSym + actionString;
        actionValue = actionTable[currentState][currentTokenIdx];
        pStack.push(new PstackEntry(stackAction, inputTokens[currentTokenIdx]));
        currentTokenIdx++;
        actionLookUp[0] = pStack.top().stateSym;
        actionLookUp[1] = pStack.top().grammarSym;
        actionLookUpString = "[" + actionLookUp[0] + "," + inputTokens[currentTokenIdx] + "]";
        inputTokensString = inputTokensToString();
        stackAction = pStack.top().grammarSym.toString() + actionString;
        currentToken = inputTokens[currentTokenIdx];
        currentState = Integer.parseInt(actionValue.replaceAll("\\D+", ""));
        actionValue = actionTable[currentState][currentTokenIdx];
        action = actionValue.substring(0, 1);
        
    }

    public void reducePrint(){
        System.out.format("%20s%20s%10s%10s%10s%10s%10s%10s%10s%15s%20s\n", 
        pStack.toString(), // stack
        inputTokensString, // input tokens
        actionLookUpString,  // action lookup
        actionValue,  // action value
        lhs[Integer.parseInt(actionString)-1], // value of lhs
        lenRHS-1,   // length of rhs
        tempStack, // temp stack
        gotoString , // goto lookup
        gotoValue, // goto value
        "push " + stackAction, // stack action
        parseTreeStack[step]);  // parse tree stack
        step++;
    }


    // reduce method for SR parser
    public void reduce() {
        actionValue = actionTable[currentState][currentTokenIdx];
        System.out.println("actionValue = " + actionValue);
        actionString = actionValue.replaceAll("\\D+", "");
        System.out.println("actionString = " + actionString);
        int as = Integer.parseInt(actionString) - 1;
        String grule = lhs[as];
        System.out.println("grule = " + grule);
        System.out.println(search(gotoSymbols, grule)); // LEFT OFF HERE

        stackAction = pStack.top().grammarSym + actionString;
        System.out.println("stackAction = " + stackAction);
        action = actionValue.substring(0, 1);
        System.out.println("action = " + action);
        lenRHS = rhs[Integer.parseInt(actionString)-1].length();
        System.out.println("lenRHS = " + lenRHS);
        actionLookUp[0] = pStack.top().stateSym;
        System.out.println("actionLookUp[0] = " + actionLookUp[0]);
        actionLookUp[1] = pStack.top().grammarSym;
        System.out.println("actionLookUp[1] = " + actionLookUp[1]);
        pStack.popNum(lenRHS-1);
        gotoLookUp[0] = actionLookUp[0];
        gotoLookUp[1] = lhs[Integer.parseInt(actionString)-1];
        gotoString = "[" + gotoLookUp[0] + " " + gotoLookUp[1] + "]";
        inputTokensString = inputTokensToString();
        actionLookUpString = "[" + actionLookUp[0] + "," + inputTokens[currentTokenIdx] + "]";
        
        pStack.push(new PstackEntry(gotoLookUp[0], gotoLookUp[1]));
        stackAction = pStack.top().grammarSym.toString() + actionString;
        gotoValue = search(gotoSymbols, gotoLookUp[1]);
    }



    public void parse() {
        pStack.push(new PstackEntry(actionLookUp[0], actionLookUp[1]));
        currentToken = inputTokens[currentTokenIdx];
        currentTokenIdx = search(actionSymbols, currentToken);
        actionValue = actionTable[currentState][currentTokenIdx];
        action = actionValue.substring(0, 1);
        parseTreeStack[currentTokenIdx] = inputTokens[0];
        actionString = actionValue.replaceAll("\\D+", "");
        inputTokensString = inputTokensToString();
        System.out.format("%20s%20s%10s%10s%10s%10s%10s%10s%10s%15s%20s\n", 0, inputTokensString,
                "[" + actionLookUp[0] + "," + inputTokens[0] + "]", actionValue, "", "", "", "", "",
                "push " + inputTokens[currentTokenIdx] + actionString , parseTreeStack[step]);
        step++;
        while (true) {
                if (action.equals("S")) {
            // System.out.println("ACTION: Shift");
            shift();
            shiftPrint();
        } else if (action.equals("R")) {
            // System.out.println("ACTION: Reduce");
            reduce();
            reducePrint();
        } else if (action.equals("$")) {
            System.out.println("ACTION: Accept");
        } else if (action.equals("")) {
            System.out.println("ACTION Error");
        } else {
            System.out.println("ACTION SOMETHING IS WRONG");
        }

    }
}

    public static void main(String[] args) throws Exception {

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

        String[] input = { "id", "+", "id", "*", "id" };

        SRParser parser = new SRParser(grammar, actionTable, gotoTable, input);
        parser.splitGrammar();
        parser.printActionTable();
        parser.printGotoTable();

        // parser.pStack = new ParseStack();
        // PstackEntry psEntry = new PstackEntry("0", parser.inputTokens[0]);
        // // PstackEntry psEntry = new PstackEntry("0", "");
        // parser.pStack.push(psEntry);
        // System.out.println("pStack: " + parser.pStack.toString());
        // int state = Integer.parseInt(parser.pStack.top().stateSym);
        // System.out.println("State: " + state);
        // // String inputToken = parser.pStack.top().grammarSym;
        // // System.out.println("InputToken: " + inputToken);
        // // parser.printInputTokens();
        // // action lookup is [0][state]
        // String actionValue = parser.actionTable[0][state];
        // // action value is aValue
        // System.out.println("aValue: " + actionValue);
        // String actionString = actionValue.replaceAll("\\D+","");
        // int actionInt = Integer.parseInt(actionString);
        // String stackActionString = parser.pStack.top().grammarSym + actionInt;
        // parser.parseTreeStack[state] = parser.inputTokens[state];
        // // one line of output
        // // System.out.print(parser.pStack.top().stateSym + " ");
        // // parser.printInputTokens();
        // // System.out.println();
        // String inputTokensString = parser.inputTokensToString();
        System.out.format("%20s%20s%10s%10s%10s%10s%10s%10s%10s%15s%20s\n", "     ", "input", "action", "action", "value",
                "length", "temp", "goto", "goto", "stack", "");
        System.out.format("%20s%20s%10s%10s%10s%10s%10s%10s%10s%15s%20s\n", "Stack", "tokens", "lookup", "value",
                "of LHS", "of RHS", "stack", "lookup", "value", "action", "parse tree stack");
        // System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");
        // System.out.format("%s%20s%10s%10s%10s%10s%10s%10s%10s%20s%5s\n",
        // parser.pStack.top().stateSym, inputTokensString, "[" + state + "," +
        // parser.inputTokens[0] + "]", actionValue, "", "", "", "", "", "push " +
        // stackActionString, parser.pStack.top().grammarSym);
        // parser.initParse();
        parser.parse();
    }

}
