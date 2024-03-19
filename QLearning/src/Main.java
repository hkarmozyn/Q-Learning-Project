import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        MazeSolver solver = new MazeSolver();
        solver.run();
        solver.calculateQ();
        solver.printQ();
    }
}