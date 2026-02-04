package domain.structures;

/**
 * Класс для работы с DSU (Disjoint Set Union), системой непересекающихся множеств, создания несвязного графа
 */
public class DSU {
  private final int[] parent;
  private final int[] rank;
  private final int size;

  public DSU(int size) {
    parent = new int[size];
    rank = new int[size];
    this.size = size;

    for (int i = 0; i < size; i++) {
      parent[i] = i;
      rank[i] = 0;
    }
  }

  /**
   * Метод для поиска корня множества (представитель)
   * @param num представитель множества, чей корень ищем
   * @return корень множества
   */
  public int find(int num) {
    checkBounds(num);
    if (parent[num] != num) {
      parent[num] = find(parent[num]);
    }

    return parent[num];
  }

  /**
   * Объединение двух множеств. Они объединяются, если являются представителями разных множеств
   * @param first представитель первого множества
   * @param second представитель второго множества
   */
  public void union(int first, int second) {
    int rootFirst = find(first);
    int rootSecond = find(second);

    if (rootFirst != rootSecond) {
      if (rank[rootFirst] > rank[rootSecond]) {
        parent[rootSecond] = rootFirst;
      } else if (rank[rootFirst] < rank[rootSecond]) {
        parent[rootFirst] = rootSecond;
      } else {
        parent[rootSecond] = rootFirst;
        ++rank[rootFirst];
      }
    }
  }

  /**
   * Проверка на то, что <code>first</code> и <code>second</code> являются представителями одного множества
   * @param first представитель первого множества
   * @param second представитель второго множества
   * @return <code>first</code> и <code>second</code> являются представителями одного множества
   */
  public boolean areConnected(int first, int second) {
    return find(first) == find(second);
  }

  /**
   * Создание множества из одного элемента
   * @param value представитель множества
   */
  public void makeSet(int value) {
    checkBounds(value);
    parent[value] = value;
  }

  private void checkBounds(int value) {
    if (value >= size) {
      throw new ArrayIndexOutOfBoundsException("value is out of bounds");
    }
    if (value < 0) {
      throw new ArrayIndexOutOfBoundsException("value cannot be less than 0");
    }
  }
}
