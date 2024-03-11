import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class MazeSolver {

    private final double alpha = 0.1;
    private final double gamma = 0.9;

    private final int reward = 100;
    private final int penalty = -10;

    private int[][] R;
    private double[][] Q;

    private Maze m = new Maze();
    private char[][] maze = m.outputMaze();

    public MazeSolver() {

    }

    boolean isFinalState(int state) {
        int i = state / m.getWidth();
        int j = state - i * m.getWidth();

        return maze[i][j] == 'F';
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < m.getStatesCount(); i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        int[] actionsArray = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            actionsArray[i] = result.get(i);
        }

        return actionsArray;
    }

    public double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);

        double maxValue = -10;
        for (int i = 0; i < actionsFromState.length; i++) {
            int nextAction = actionsFromState[i];
            double value = Q[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    void initializeQ() {
        for (int i = 0; i < m.getStatesCount(); i++){
            for(int j = 0; j < m.getStatesCount(); j++){
                Q[i][j] = (double)R[i][j];
            }
        }
    }

    void calculateQ() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) { // Train cycles
            // Select random initial state
            int crtState = rand.nextInt(m.getStatesCount());

            while (!isFinalState(crtState)) {
                int[] actionsFromCurrentState = possibleActionsFromState(crtState);


                int index = rand.nextInt(actionsFromCurrentState.length);
                int nextState = actionsFromCurrentState[index];

                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
                double q = Q[crtState][nextState];
                double maxQ = maxQ(nextState);
                int r = R[crtState][nextState];

                double value = q + alpha * (r + gamma * maxQ - q);
                Q[crtState][nextState] = value;

                crtState = nextState;
            }
        }
    }
    public void printQ() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("resources/output.txt"))) {
            pw.println("Q matrix");
            for (int i = 0; i < Q.length; i++) {
                pw.print("From state " + i + ":  ");
                for (int j = 0; j < Q[i].length; j++) {
                    pw.println("%6.2f " + Q[i][j]);
                }
                pw.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printR(int[][] matrix) {
        System.out.print("%25s " + "States: ");
        for (int i = 0; i <= 8; i++) {
            System.out.print("%4s " + i);
        }
        System.out.println();

        for (int i = 0; i < m.getStatesCount(); i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < m.getStatesCount(); j++) {
                System.out.print("%4s " + matrix[i][j]);
            }
            System.out.println("]");
        }
    }

    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < m.getStatesCount(); i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }

    int getPolicyFromState(int state) {
        int[] actionsFromState = possibleActionsFromState(state);

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;


        for (int nextState : actionsFromState) {
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

}