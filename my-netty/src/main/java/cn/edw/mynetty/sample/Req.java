package cn.edw.mynetty.sample;

import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Writable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.channels.SocketChannel;

/**
 * @author taoxu.xu
 * @date 9/3/2021 5:46 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Req {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
