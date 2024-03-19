import java.util.ArrayList;
import java.util.Random;

public class Maze {

    private char[][] maze;
    private int width;
    private int height;
    private int statesCount;

    private Random r;

    private final static char start = 'S';
    private final static char finish = 'F';
    private final static char empty = 'E';

    public Maze() {
        r = new Random();
        width = 3;
        height = 3;
        statesCount = 3 * 3;
    }

    public void generate() {
        int rStartH = r.nextInt(height);
        int rStartW = r.nextInt(width);
        int rFinishH = r.nextInt(height);
        int rFinishW = r.nextInt(width);
        while (rFinishH == rStartH && rFinishW == rStartW) {
            rFinishH = r.nextInt(height);
            rFinishW = r.nextInt(width);
        }

        maze = new char[height][width];

        maze[rStartH][rStartW] = start;
        maze[rFinishH][rFinishW] = finish;

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] != start && maze[j][i] != finish) {
                    maze[i][j] = empty;
                }
            }
        }
    }

    public char[][] outputMaze() {
        return maze;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStatesCount() {
        return statesCount;
    }
}

