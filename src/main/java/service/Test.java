package service;

import java.util.*;

public class Test {

  public static int[] evenOddBit(int n) {
    int odd = 0, even = 0;
    List<Integer> ans = new ArrayList<>();
    String str = Integer.toBinaryString(n);

    for (int i = 0; i < str.length(); i++) {
      boolean isOne = str.charAt(i) == '1';
      if (i % 2 != 0 && isOne)
        odd++;
      else if (i % 2 == 0 && isOne)
        even++;
    }
    ans.add(even);
    ans.add(odd);
    return ans.stream().mapToInt(Integer::intValue).toArray();
  }

  public static void main(String[] args) {
    int n = 2;

    System.out.println(Arrays.toString(evenOddBit(n)));
  }

}
