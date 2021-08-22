package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author taoxu.xu
 * @date 8/13/2021 4:10 PM
 */
public class Test01 {
    @Test
    public void testByte() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            byte val = (byte) (127 * Math.random() -  127 * Math.random());
            seri.writeByte(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final byte val2 = deseri.readByte();
            assertEquals(val,val2);
        }
    }

    @Test
    public void testShort() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            short val = (short) (Short.MAX_VALUE * Math.random() -  Short.MAX_VALUE * Math.random());
            seri.writeShort(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final short val2 = deseri.readShort();
            assertEquals(val,val2);
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
            assertEquals(val,val2);
        }
    }


    @Test
    public void testInt() throws TypeNotFoundException {
        for (int i = 0; i < 1000; i++) {
            final Seri seri = new Seri();
            int val = (int) (1000403 * Math.random() - 1000033 * Math.random());
            seri.writeInt(val);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final int val2 = deseri.readInt();
            assertEquals(val,val2);
        }
    }

    @Test
    public void testString() throws TypeNotFoundException, IOException {
        for (int i = 0; i < 10; i++) {
            final Seri seri = new Seri();
            String str1 = "str-"+i+" str"+(1000-i);

            seri.writeString(str1);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final String str2 = deseri.readString();
            assertEquals(str1, str2);
        }
    }


    @Test
    public void testObj01() throws Exception {
        for (int i = 0; i < 100; i++) {
            final Seri seri = new Seri();
            final Cat cat1 = new Cat(
                    i,
                    "cat"+i,
                    (char)i,
                    (byte) i,
                    (short) i,
                    (long)(Math.random()*Long.MAX_VALUE),
                    true,
                    (float) (Integer.MAX_VALUE*Math.random()),
                    Long.MAX_VALUE*Math.random(),
                    new CatFood((int) (Math.random()*Integer.MAX_VALUE),
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
}

