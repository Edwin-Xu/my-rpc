package cn.edw.mynetty.sample;

import cn.edw.mynetty.core.client.NioClient;

import java.io.IOException;

/**
 * @author taoxu.xu
 * @date 9/3/2021 5:42 PM
 */
public class SampleClient {
    public static void main(String[] args) throws IOException {
        final NioClient client = new NioClient("localhost", 6666);
        final Object edw = client.request(new Req("edw"));
        System.out.println(edw);
    }
}
