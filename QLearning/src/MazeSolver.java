/*
Hk
3/20/24
Cz
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class MazeSolver {
    // Learning rate and discount factor for Q-learning
    private final double alpha = 0.1;
    private final double gamma = 0.9;
    // Dimensions of the maze
    private final int mazeWidth = 3;
    private final int mazeHeight = 3;
    private final int statesCount = mazeHeight * mazeWidth;
    // Rewards and penalties
    private final int reward = 100;
    private final int penalty = -10;
    // Maze structure and Q/R matrices
    private char[][] maze;
    private int[][] R;
    private double[][] Q;

    // Main method to run the solver
    public static void main(String args[]) throws IOException {
        MazeSolver mz = new MazeSolver();

        Maze m = new Maze();
        m.generate();
        m.outputMaze();

        mz.init();
        mz.calculateQ();
        mz.printQ();
        mz.printPolicy();
    }
    // Initializes the maze, R, and Q matrices
    public void init() {
        File file = new File("maze.txt");

        R = new int[statesCount][statesCount];
        Q = new double[statesCount][statesCount];
        maze = new char[mazeHeight][mazeWidth];


        try (FileInputStream fis = new FileInputStream(file)) {

            int i = 0;
            int j = 0;

            int content;


            while ((content = fis.read()) != -1) {
                char c = (char) content;
                if (c != '0' && c != 'F' && c != 'X') {
                    continue;
                }
                maze[i][j] = c;
                j++;
                if (j == mazeWidth) {
                    j = 0;
                    i++;
                }
            }

// Initialize the R matrix based on the maze structure
            for (int k = 0; k < statesCount; k++) {


                i = k / mazeWidth;
                j = k - i * mazeWidth;

                for (int s = 0; s < statesCount; s++) {
                    R[k][s] = -1;
                }
// Assign rewards/penalties for moving in each direction
                if (maze[i][j] != 'F') {

                    int goLeft = j - 1;
                    if (goLeft >= 0) {
                        int target = i * mazeWidth + goLeft;
                        if (maze[i][goLeft] == '0') {
                            R[k][target] = 0;
                        } else if (maze[i][goLeft] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                    int goRight = j + 1;
                    if (goRight < mazeWidth) {
                        int target = i * mazeWidth + goRight;
                        if (maze[i][goRight] == '0') {
                            R[k][target] = 0;
                        } else if (maze[i][goRight] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    int goUp = i - 1;
                    if (goUp >= 0) {
                        int target = goUp * mazeWidth + j;
                        if (maze[goUp][j] == '0') {
                            R[k][target] = 0;
                        } else if (maze[goUp][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    int goDown = i + 1;
                    if (goDown < mazeHeight) {
                        int target = goDown * mazeWidth + j;
                        if (maze[goDown][j] == '0') {
                            R[k][target] = 0;
                        } else if (maze[goDown][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                }
            }
            initializeQ();
            printR(R);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Initialize Q matrix with values from R matrix
    void initializeQ()
    {
        for (int i = 0; i < statesCount; i++){
            for(int j = 0; j < statesCount; j++){
                Q[i][j] = (double)R[i][j];
            }
        }
    }
    // Print the R matrix
    void printR(int[][] matrix) {
        System.out.printf("%25s", "States: ");
        for (int i = 0; i <= 8; i++) {
            System.out.printf("%4s", i);
        }
        System.out.println();

        for (int i = 0; i < statesCount; i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < statesCount; j++) {
                System.out.printf("%4s", matrix[i][j]);
            }
            System.out.println("]");
        }
    }
    // Calculate the Q matrix using the Q-learning algorithm
    void calculateQ() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            int crtState = rand.nextInt(statesCount);

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
    // Check if a state is a final state
    boolean isFinalState(int state) {
        int i = state / mazeWidth;
        int j = state - i * mazeWidth;

        return maze[i][j] == 'F';
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < statesCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);

        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = Q[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }
    // Print the optimal policy based on the Q matrix
    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }
    // Get the policy (optimal next state) from a given state
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
    // Print the Q matrix to a file
    void printQ() throws FileNotFoundException {

        PrintWriter pw = new PrintWriter("Q-matrix.txt");

        pw.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            pw.print("From state " + i + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                pw.printf("%6.2f ", (Q[i][j]));
            }
            pw.println();
        }
        pw.close();
    }
}