package cn.edw.mynetty.exception;

/**
 * @author taoxu.xu
 * @date 9/2/2021 2:27 PM
 */
public class NotAcceptableException extends RuntimeException{
    public NotAcceptableException() {
        this("Selection Key is not acceptable!");
    }

    public NotAcceptableException(String message) {
        super(message);
    }
}
