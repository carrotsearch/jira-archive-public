package org.carrot2.lingo2g;

public class StrictfpTest {
    private static double defaultDmul(double a, double b) {
      return a * b;
    }

    private static strictfp double strictDmul(double a, double b) {
      return a * b;
    }

    private static double defaultDdiv(double a, double b) {
      return a / b;
    }

    private static strictfp double strictDdiv(double a, double b) {
      return a / b;
    }

    public static void main(String[] args) {
      double a, b, c;

      /* multiplication */
      a = Double.longBitsToDouble(0x0008008000000000L);
      b = Double.longBitsToDouble(0x3ff0000000000001L);

      System.out.println(a + " (0x0008008000000000)");
      System.out.println("  * " + b + " (0x3ff0000000000001)");

      c = defaultDmul(a, b);
      System.out.println("default : " + c +
          " (0x" + Long.toHexString(Double.doubleToLongBits(c)) + ")");

      c = strictDmul(a, b);
      System.out.println("strictfp: " + c +
          " (0x" + Long.toHexString(Double.doubleToLongBits(c)) + ")");

      System.out.println();

      /* division */
      a = Double.longBitsToDouble(0x000fffffffffffffL);
      b = Double.longBitsToDouble(0x3fefffffffffffffL);

      System.out.println(a + " (0x000fffffffffffff)");
      System.out.println("  / " + b + " (0x3fefffffffffffff)");

      c = defaultDdiv(a, b);
      System.out.println("default : " + c +
          " (0x" + Long.toHexString(Double.doubleToLongBits(c)) + ")");

      c = strictDdiv(a, b);
      System.out.println("strictfp: " + c +
          " (0x" + Long.toHexString(Double.doubleToLongBits(c)) + ")");
    }
  }
