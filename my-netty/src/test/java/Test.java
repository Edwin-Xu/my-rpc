import java.nio.ByteBuffer;

/**
 * @author taoxu.xu
 * @date 8/30/2021 5:36 PM
 */
public class Test {
    public static void main(String[] args) {

        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put("hi".getBytes());

        System.out.println(buffer.hasRemaining());

        buffer.flip();
        System.out.println(buffer.array().length);

        System.out.println(new String(buffer.array()));

        System.out.println(buffer.toString());




    }
}
