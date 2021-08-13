package cn.edw.seri.core;

import cn.edw.seri.exception.TypeNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * @author taoxu.xu
 * @date 8/13/2021 4:10 PM
 */
public class Test01 {
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
    public void testObj01() throws IOException, IllegalAccessException, TypeNotFoundException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        for (int i = 0; i < 10; i++) {
            final Seri seri = new Seri();
            final Cat cat1 = new Cat(i, "cat"+i, 'c', (byte) i, (short) i, 1225555L, true, 0.25f, 25.2);

            seri.writeObject(cat1);
            final byte[] bytes = seri.getBytes();

            final Deseri deseri = new Deseri(bytes);
            final Cat cat2 = (Cat) deseri.readObject();
            assertEquals(cat1, cat2);
        }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Cat{
    private int id;
    private String name ;
    char ch;
    private byte byt;
    private short sho;
    private Long lon;
    private Boolean isMan;
    private float flo;
    private Double dou;
}