import java.io.*;


class SRParser {

    String[][] grammar;
    int m;
    String[] lhs;
    String[] rhs;
    String[][] actionTable;
    String[][] gotoTable;
    ParseStack pStack;
    String[] inputTokens;
    String[] parseTreeStack;

    public SRParser(String[][] grammar, String[][] actionTable, String[][] gotoTable, String[] input) {
        this.grammar = grammar;
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        inputTokens = new String[input.length+1];
        for (int i = 0; i < input.length; i++) {
            inputTokens[i] = input[i];
        }
        inputTokens[inputTokens.length-1] = "$"; 
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

    public String inputTokensToString(){
        String input = "";
        for (int i = 0; i < inputTokens.length; i++) {
            input += inputTokens[i];
        }
        return input;
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

        parser.pStack = new ParseStack();
        PstackEntry psEntry = new PstackEntry("0", parser.inputTokens[0]);
        // PstackEntry psEntry = new PstackEntry("0", "");
        parser.pStack.push(psEntry);
        System.out.println("pStack: " + parser.pStack.toString());
        int state = Integer.parseInt(parser.pStack.top().stateSym);
        System.out.println("State: " + state);
        // String inputToken = parser.pStack.top().grammarSym;
        // System.out.println("InputToken: " + inputToken);
        // parser.printInputTokens();
        // action lookup is [0][state]
        String actionValue = parser.actionTable[0][state];
        // action value is aValue
        System.out.println("aValue: " + actionValue);
        String actionString = actionValue.replaceAll("\\D+","");
        int actionInt = Integer.parseInt(actionString);
        String stackActionString = parser.pStack.top().grammarSym + actionInt;
        parser.parseTreeStack[state] = parser.inputTokens[state];
        // one line of output
        // System.out.print(parser.pStack.top().stateSym + " "); 
        // parser.printInputTokens();
        // System.out.println();
        String inputTokensString = parser.inputTokensToString();
        System.out.format("%s%20s%10s%10s%10s%10s%10s%10s%10s%20s%20s\n", "", "input", "action", "action", "value", "length", "temp", "goto", "goto", "stack", "");
        System.out.format("%s%20s%10s%10s%10s%10s%10s%10s%10s%10s%20s\n", "Stack     ", "tokens", "lookup", "value", "of LHS", "of RHS", "stack", "lookup", "value", "action", "parse tree stack");
        // System.out.println(" " +  "[" + state + "," + parser.inputTokens[0] + "]" + " " +  stackActionString + " " +  parser.pStack.top().grammarSym );
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.format("%s%20s%10s%10s%10s%10s%10s%10s%10s%20s%5s\n", parser.pStack.top().stateSym, inputTokensString, "[" + state + "," + parser.inputTokens[0] + "]", actionValue, "", "", "", "", "", "push " + stackActionString,  parser.pStack.top().grammarSym);
    }

}
