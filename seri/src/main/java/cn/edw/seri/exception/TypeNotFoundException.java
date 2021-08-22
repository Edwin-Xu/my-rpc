package cn.edw.seri.exception;

/**
 * @author taoxu.xu
 * @date 8/11/2021 7:29 PM
 */
public class TypeNotFoundException extends RuntimeException{
    public TypeNotFoundException(String msg){
        super(msg);
    }
    public TypeNotFoundException(){
        super("The type can not be found!");
    }
}
