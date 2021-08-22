package cn.edw.seri.exception;

/**
 * @author taoxu.xu
 * @date 8/22/2021 7:22 PM
 */
public class NoMoreDataException extends RuntimeException{
    public NoMoreDataException() {
        super("No more data left!");
    }

    public NoMoreDataException(String message) {
        super(message);
    }
}
