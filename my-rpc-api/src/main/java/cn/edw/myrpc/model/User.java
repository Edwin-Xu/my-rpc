package cn.edw.myrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxu.xu
 * @date 8/15/2021 1:28 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String name;
    private Boolean gender;
    private Double salary;
}
