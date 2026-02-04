package domain.util;

import domain.model.PerfectMaze;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Converter {
  public static void mazeToFile(PerfectMaze maze, String filename) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
      int[][] rightWalls = maze.getRightWallsMatrix();
      int[][] bottomWalls = maze.getBottomWallsMatrix();
      int rows = maze.getRows(), columns = maze.getColumns();

      bw.write(rows + " " + columns);
      bw.newLine();
      matrixToFile(bw, rightWalls, rows, columns);
      bw.newLine();
      matrixToFile(bw, bottomWalls, rows, columns);
    } catch (Exception e) {
      throw new IllegalArgumentException("Something went wrong: " + e.getMessage());
    }
  }

  private static void matrixToFile(BufferedWriter bw, int[][] matrix, int rows, int columns) throws IOException {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        bw.write(String.valueOf(matrix[i][j]));
        if (j < columns - 1) bw.write(" ");
      }
      bw.newLine();
    }
  }
}
