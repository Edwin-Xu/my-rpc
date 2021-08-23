package cn.edw.seri.core;

/**
 * @author taoxu.xu
 * @date 8/17/2021 10:12 AM
 */
public class Test02 {

    public static void main(String[] args) {
        // 基本类型
        System.out.println(int.class.getName()); // int
        System.out.println(int.class.getSimpleName()); // int

        // 一维基本类型数据
        final int[] ints = new int[10];
        System.out.println(ints.getClass().getName()); // [I
        System.out.println(ints.getClass().getSimpleName()); // int[]

        final byte[] bytes = new byte[1];
        System.out.println(bytes.getClass().getName());// [B
        System.out.println(bytes.getClass().getSimpleName()); //byte[]

        final short[] shorts = new short[1];
        System.out.println(shorts.getClass().getName()); // [S
        System.out.println(shorts.getClass().getSimpleName()); //short[]

        final long[] longs = new long[1];
        System.out.println(longs.getClass().getName()); // [J :注意是[J 而不是[L
        System.out.println(longs.getClass().getSimpleName()); // long[]

        // Void也是有对应的class的
        System.out.println(void.class.getName()); // void
        System.out.println(void.class.getSimpleName()); // void

        // void也是有对应包装类的
        System.out.println(Void.class.getName()); // java.lang.Void
        System.out.println(Void.class.getSimpleName()); // Void


        final Integer[] integers = new Integer[1];
        System.out.println(integers.getClass().getName()); // [Ljava.lang.Integer;
        System.out.println(integers.getClass().getSimpleName()); // Integer[]

        final int[][] ints1 = new int[1][1];
        System.out.println(ints1.getClass().getName()); // [[I
        System.out.println(ints1.getClass().getSimpleName()); //int[][]

        final Integer[][] integers1 = new Integer[1][1];
        System.out.println(integers1.getClass().getName());//[[Ljava.lang.Integer;
        System.out.println(integers1.getClass().getSimpleName());

        final Double[][][][][] doubles = new Double[1][1][1][1][1];
        System.out.println(doubles.getClass().getName());//[[[[[Ljava.lang.Double;
        System.out.println(doubles.getClass().getSimpleName());

        final double[][][][][] doubles1 = new double[1][1][1][1][1];
        System.out.println(doubles1.getClass().getName());//[[[[[D
        System.out.println(doubles1.getClass().getSimpleName());//double[][][][][]

        try {
            // 虽然数组有对应的Class，但是不能通过Class.forName找到
            final Class<?> aClass = Class.forName(doubles.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
