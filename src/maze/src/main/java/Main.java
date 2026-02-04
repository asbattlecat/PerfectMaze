import domain.algorithm.PerfectMazeDFS;
import domain.algorithm.PerfectMazeGenerator;
import domain.model.PerfectMaze;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    Random random = new Random();
    int target = 100000;
    int size = 50;
    for (int i = 0; i < target; i++) {
      PerfectMazeGenerator generator = new PerfectMazeGenerator(size, size);
      PerfectMaze maze = generator.create();
      PerfectMazeDFS dfs = new PerfectMazeDFS(maze);
      int startX = randomCoordinate(random, size);
      int startY = randomCoordinate(random, size);
      int endX = randomCoordinate(random, size);
      int endY = randomCoordinate(random, size);
      List<Point> path = dfs.findPath(new Point(startX, startY), new Point(endX, endY));

      if (path == null) System.out.println("emmmm...");
    }
  }

  private static int randomCoordinate(Random random, int max) {
    return random.nextInt(0, max);
  }
}
