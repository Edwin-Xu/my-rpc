package cn.edw.seri.exception;

/**
 * @author taoxu.xu
 * @date 8/11/2021 3:34 PM
 */
public class UnsupportedTypeException extends UnsupportedOperationException {
    public UnsupportedTypeException() {
        super("The type is not supported as yet");
    }
}
