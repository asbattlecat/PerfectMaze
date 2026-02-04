package domain.util;

public class ArgumentCheckerUtil {
  public static void checkMazeSize(int value) {
    if (value <= 1) throw new ArrayIndexOutOfBoundsException("size cannot be zero or less, size: " + value);
  }

  public static void checkIndexBounds(int value, int maxValue) {
    if (value < 0 || value >= maxValue)
      throw new ArrayIndexOutOfBoundsException("row value is out of range, value: " + value);
  }
}
