package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author taoxu.xu
 * @date 8/13/2021 4:10 PM
 */
public class Test01 {
    @Test
    public void testByte() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            byte val = (byte) (127 * Math.random() - 127 * Math.random());
            seri.writeByte(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final byte val2 = deseri.readByte();
            assertEquals(val, val2);
        }
    }

    @Test
    public void testShort() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            short val = (short) (Short.MAX_VALUE * Math.random() - Short.MAX_VALUE * Math.random());
            seri.writeShort(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final short val2 = deseri.readShort();
            assertEquals(val, val2);
        }
    }

    @Test
    public void testLong() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            long val = (long) (1000034343400L * Math.random() - 1000033034340L * Math.random());
            seri.writeLong(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final long val2 = deseri.readLong();
            assertEquals(val, val2);
        }
    }


    @Test
    public void testInt01() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            int val = (int) (1000403 * Math.random() - 1000033 * Math.random());
            seri.writeInt(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final int val2 = deseri.readInt();
            assertEquals(val, val2);
        }
    }

    @Test
    public void testInt02() throws TypeNotFoundException {
        final Seri seri = new Seri();
        for (int i = 0; i < 1000; i++) {
            seri.writeInt(i);
        }
        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);

        for (int i = 0; i < 1000; i++) {
            Integer a = deseri.readInt();
            assertEquals(i, a + 0);
        }
    }

    @Test
    public void testString() throws TypeNotFoundException {
        for (int i = 0; i < 10; i++) {
            final Seri seri = new Seri();
            String str1 = "str-" + i + " str" + (1000 - i);

            seri.writeString(str1);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final String str2 = deseri.readString();
            assertEquals(str1, str2);
        }
    }

    @Test
    public void testChar() throws TypeNotFoundException {
        final Seri seri = new Seri();
        for (int i = 0; i < 1000; i++) {
            seri.writeChar((char) (i % 127));
        }
        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);

        for (int i = 0; i < 1000; i++) {
            assertEquals(i % 127, deseri.readChar() + 0);
        }
    }

    @Test
    public void testFloat() throws TypeNotFoundException {
        final Seri seri = new Seri();
        for (int i = 0; i < 1000; i++) {
            seri.writeFloat(i + 0.02548f);
        }
        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);

        for (int i = 0; i < 1000; i++) {
            assertEquals(i + 0.02548f, deseri.readFloat(), 0.0000000001f);
        }
    }

    @Test
    public void testDouble() throws TypeNotFoundException {
        final Seri seri = new Seri();
        for (int i = 0; i < 1000; i++) {
            seri.writeDouble(i + 0.0254823232323);
        }
        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);

        for (int i = 0; i < 1000; i++) {
            assertEquals(i + 0.0254823232323, deseri.readDouble(), 0.0000000000000000001f);
        }
    }


    @Test
    public void testBoolean() throws TypeNotFoundException {
        final Seri seri = new Seri();
        for (int i = 0; i < 1000; i++) {
            seri.writeBoolean(i % 2 == 1);
        }
        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);

        for (int i = 0; i < 1000; i++) {
            assertEquals(i % 2 == 1, deseri.readBoolean());
        }
    }

    @Test
    public void testNull() throws TypeNotFoundException {
        final Seri seri = new Seri();
        seri.writeBoolean(null);
        seri.writeFloat(null);
        seri.writeByte(null);
        seri.writeShort(null);
        seri.writeInt(null);
        seri.writeLong(null);
        seri.writeChar(null);
        seri.writeString(null);
        seri.writeDouble(null);

        final byte[] bytes = seri.getBytes();
        final Deseri deseri = new Deseri(bytes);
        assertNull(deseri.readBoolean());
        assertNull(deseri.readFloat());
        assertNull(deseri.readByte());
        assertNull(deseri.readShort());
        assertNull(deseri.readInt());
        assertNull(deseri.readLong());
        assertNull(deseri.readChar());
        assertNull(deseri.readString());
        assertNull(deseri.readDouble());
    }


    @Test
    public void testObj01() throws Exception {
        for (int i = 0; i < 100; i++) {
            final Seri seri = new Seri();
            final Cat cat1 = new Cat(
                    i,
                    "cat" + i,
                    (char) i,
                    (byte) i,
                    (short) i,
                    (long) (Math.random() * Long.MAX_VALUE),
                    true,
                    (float) (Integer.MAX_VALUE * Math.random()),
                    Long.MAX_VALUE * Math.random(),
                    new CatFood((int) (Math.random() * Integer.MAX_VALUE),
                            new FoodType(1))
            );

            seri.writeObject(cat1);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final Cat cat2 = (Cat) deseri.readObject();
            System.out.println(cat2);
            assertEquals(cat1, cat2);
        }
    }


    @Test
    public void testArray01() throws Exception {
        final Seri seri = new Seri();

        final Byte[] bytArr = new Byte[10];
        for (byte i = 0; i < 10; i++) {
            bytArr[i] = i;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Byte[] bytes = (Byte[]) deseri.readArray();

        for (byte i = 0; i < 10; i++) {
            assertEquals(i, bytes[i]+0);
        }
    }

    /**
     * 测试填充
     * */
    @Test
    public void testArray01_1() throws Exception {

        final Seri seri = new Seri();

       final byte[][] bytArr =
                new byte[][]{
                        {1, 1, 1},
                        {2, 2, 2, 2, 2, 2},
                        {2},
                        {}
                };
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Byte[][] bytes = (Byte[][]) deseri.readArray();

        for (Byte[] aByte : bytes) {
            for (Byte aByte1 : aByte) {
                System.out.print(aByte1+" ");
            }
            System.out.println();
        }
    }

    @Test
    public void testArray02() throws Exception {
        final Seri seri = new Seri();

        final Short[] bytArr = new Short[10];
        for (short i = 0; i < 10; i++) {
            bytArr[i] = i;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Short[] bytes = (Short[]) deseri.readArray();

        for (short i = 0; i < 10; i++) {
            assertEquals(i, bytes[i]+0);
        }
    }



    @Test
    public void testArray03() throws Exception {
        final Seri seri = new Seri();

        final Integer[] bytArr = new Integer[100];
        for (int i = 0; i < 100; i++) {
            bytArr[i] = i;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Integer[] bytes = (Integer[]) deseri.readArray();

        for (int i = 0; i < 100; i++) {
            assertEquals(i, bytes[i]+0);
        }
    }

    @Test
    public void testArray04() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final Long[] bytArr = new Long[testSize];
        for (long i = 0; i < testSize; i++) {
            bytArr[(int) i] = i;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Long[] bytes = (Long[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals(i, bytes[i]+0);
        }
    }

    @Test
    public void testArray05() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final Character[] bytArr = new Character[testSize];
        for (int i = 0; i < testSize; i++) {
            bytArr[ i] = (char)i;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Character[] bytes = (Character[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals(i, bytes[i]+0);
        }
    }

    @Test
    public void testArray06() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final Float[] bytArr = new Float[testSize];
        for (int i = 0; i < testSize; i++) {
            bytArr[ i] = i + 0.345f;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Float[] bytes = (Float[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals( i+0.345f, bytes[i]+0, 0.001);
        }
    }

    @Test
    public void testArray07() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final Double[] bytArr = new Double[testSize];
        for (int i = 0; i < testSize; i++) {
            bytArr[ i] = i + 0.36534346545;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final Double[] bytes = (Double[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals( i+0.36534346545, bytes[i]+0, 0.000000001);
        }
    }

    @Test
    public void testArray08() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final String[] bytArr = new String[testSize];
        for (int i = 0; i < testSize; i++) {
            bytArr[ i] = "str-"+ i ;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final String[] bytes = (String[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals("str-"+ i , bytes[i]);
        }
    }


    @Test
    public void testArray09() throws Exception {
        int testSize= 100;
        final Seri seri = new Seri();

        final FoodType[] bytArr = new FoodType[testSize];
        for (int i = 0; i < testSize; i++) {
            bytArr[ i] = new FoodType(i) ;
        }
        seri.writeArray(bytArr);

        final Deseri deseri = new Deseri(seri.getBytes());

        final FoodType[] bytes = (FoodType[]) deseri.readArray();

        for (int i = 0; i < testSize; i++) {
            assertEquals( new FoodType(i), bytes[i]);
        }
    }



}

