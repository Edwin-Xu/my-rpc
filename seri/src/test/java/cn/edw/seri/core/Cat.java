package cn.edw.seri.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxu.xu
 * @date 8/22/2021 9:01 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cat{
    private int id;
    private String name ;
    char ch;
    private byte byt;
    private short sho;
    private Long lon;
    private Boolean isMan;
    private float flo;
    private Double dou;

    private CatFood catFood;
}
