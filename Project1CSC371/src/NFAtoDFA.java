import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class DFA {
    private String[][] transitions;
    private List<List<String>> dfaStates;  // List of DFA states as combinations of NFA states
    private List<String> finalStates;
    private int stateCount;

    public DFA(int numStates) {
        this.transitions = new String[numStates][2];  // 2 symbols (a and b)
        this.dfaStates = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.stateCount = 0;
    }

    // Getter for transitions
    public String getTransition(int state, int symbol) {
        return transitions[state][symbol];
    }

    // Setter for transitions
    public void setTransition(int state, int symbol, String destination) {
        this.transitions[state][symbol] = destination;
    }

    // Add DFA states (which are lists of NFA states)
    public void addDfaState(List<String> state) {
        dfaStates.add(state);
    }

    // Get DFA states
    public List<List<String>> getDfaStates() {
        return dfaStates;
    }

    // Getter for final states
    public List<String> getFinalStates() {
        return finalStates;
    }

    // Add a final state
    public void addFinalState(String state) {
        if (!finalStates.contains(state)) {
            finalStates.add(state);
        }
    }

    // Getter for state count
    public int getStateCount() {
        return stateCount;
    }

    // Increment state count
    public void incrementStateCount() {
        stateCount++;
    }

    // Output the DFA states
    public void printDfaStates() {
        System.out.println("DFA States: " + dfaStates);
    }

    // Output the DFA transitions in the required format
    public void printDfaTransitions() {
        System.out.println("DFA Transitions: {");
        for (int i = 0; i < stateCount; i++) {
            String state = dfaStates.get(i).toString();
            Map<String, String> transitionsMap = new HashMap<>();
            if (transitions[i][0] != null) {
                transitionsMap.put("a", transitions[i][0]);
            }
            if (transitions[i][1] != null) {
                transitionsMap.put("b", transitions[i][1]);
            }
            System.out.println("  " + state + "=" + transitionsMap);
        }
        System.out.println("}");
    }

    // Output the DFA final states
    public void printFinalStates() {
        System.out.println("DFA Final States: " + finalStates);
    }
}

public class NFAtoDFA {
    // Function to convert NFA to DFA
    public static DFA nfaToDfa(
            String[][][] nfa,  // 3D array for NFA transitions (states, symbols, destination states)
            String initialState,
            String[] finalStates) {

        // Data structures for DFA
        List<List<String>> dfaStates = new ArrayList<>();  // DFA states (as lists of NFA states)
        DFA dfa = new DFA(100); // DFA with max 100 states (for simplicity)
        List<List<String>> unprocessedStates = new LinkedList<>();
        List<String> initialDfaState = new ArrayList<>();
        initialDfaState.add(initialState);
        unprocessedStates.add(initialDfaState);

        List<List<String>> dfaStateMapping = new ArrayList<>();
        dfaStateMapping.add(initialDfaState);

        // Process all DFA states
        while (!unprocessedStates.isEmpty()) {
            List<String> currentDfaState = unprocessedStates.remove(0);
            dfaStates.add(currentDfaState);
            dfa.addDfaState(currentDfaState);  // Add DFA state to DFA object

            // Process each possible input symbol
            for (int symbol = 0; symbol < 2; symbol++) {  // Assuming symbols are 0: 'a' and 1: 'b'
                List<String> newState = new ArrayList<>();

                // Union of transitions from all NFA states in the current DFA state
                for (String nfaState : currentDfaState) {
                    int nfaStateIndex = nfaState.charAt(1) - '0';  // Convert 'q0', 'q1', etc. to index 0, 1, 2
                    if (nfa[nfaStateIndex][symbol] != null) {
                        newState.addAll(Arrays.asList(nfa[nfaStateIndex][symbol]));
                    }
                }

                if (!newState.isEmpty()) {
                    // Check if this DFA state has been processed
                    int stateIndex = dfaStateMapping.indexOf(newState);
                    if (stateIndex == -1) {
                        dfaStateMapping.add(newState);
                        stateIndex = dfa.getStateCount();
                        dfa.incrementStateCount();
                        unprocessedStates.add(newState);
                    }

                    // Add the transition for the current DFA state
                    int currentStateIndex = dfaStates.indexOf(currentDfaState);
                    dfa.setTransition(currentStateIndex, symbol, dfaStateMapping.get(stateIndex).toString());
                }
            }

            // Check if this DFA state is accepting
            for (String nfaFinal : finalStates) {
                if (currentDfaState.contains(nfaFinal)) {
                    int currentStateIndex = dfaStates.indexOf(currentDfaState);
                    dfa.addFinalState(dfaStates.get(currentStateIndex).toString());
                }
            }
        }

        return dfa;
    }

    public static void main(String[] args) {
        // Define the NFA as a 3D array where the first index is the state,
        // second index is the symbol (0 for 'a', 1 for 'b'),
        // and third is the list of destination states.
        String[][][] nfa1 = new String[3][2][];

        nfa1[0][0] = new String[] {"q0", "q1"};  // (q0, a) -> {q0, q1}
        nfa1[1][1] = new String[] {"q1", "q2"};  // (q1, b) -> {q1, q2}
        nfa1[2][0] = new String[] {"q2"};        // (q2, a) -> {q2}

        String initialState1 = "q0";
        String[] finalStates1 = {"q1"};

        // Convert NFA to DFA
        DFA dfa1 = nfaToDfa(nfa1, initialState1, finalStates1);

        // Print the DFA states
        dfa1.printDfaStates();

        // Print the DFA transition table
        dfa1.printDfaTransitions();

        // Print the DFA final states
        dfa1.printFinalStates();
    }
}
