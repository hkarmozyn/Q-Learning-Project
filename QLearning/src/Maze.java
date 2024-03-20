import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Maze {

    // Maze properties
    private char[][] maze;
    private int width;
    private int height;
    private int statesCount;

    // Random number generator
    private Random r;

    // Constants for maze symbols
    private final static char penalty = 'X';
    private final static char finish = 'F';
    private final static char empty = '0';

    // Constructor
    public Maze() {
        // Initialize random number generator
        r = new Random();
        // Default maze size
        width = 3;
        height = 3;
        statesCount = width * height;
    }

    // Generate the maze
    public void generate() {
        // Randomly select penalty and finish positions
        int rPenaltyH = r.nextInt(height);
        int rPenaltyW = r.nextInt(width);
        int rFinishH = r.nextInt(height);
        int rFinishW = r.nextInt(width);

        // Ensure penalty and finish positions are different
        while (rFinishH == rPenaltyH && rFinishW == rPenaltyW) {
            rFinishH = r.nextInt(height);
            rFinishW = r.nextInt(width);
        }

        // Initialize maze array
        maze = new char[height][width];

        // Place penalty and finish symbols in maze
        maze[rPenaltyH][rPenaltyW] = penalty;
        maze[rFinishH][rFinishW] = finish;

        // Fill remaining cells with empty symbol
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] != penalty && maze[i][j] != finish) {
                    maze[i][j] = empty;
                }
            }
        }
    }

    // Output maze to file
    public void outputMaze() throws IOException {
        StringBuilder sb = new StringBuilder();
        // Convert maze array to string
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(maze[i][j]);
            }
            sb.append("\n");
        }
        // Write maze string to file
        PrintWriter pw = new PrintWriter("maze.txt");
        pw.println(sb);
        pw.close();
    }
}
