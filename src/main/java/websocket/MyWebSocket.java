package websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/9
 * @Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public class MyWebSocket extends ChannelInitializer<SocketChannel> {
  
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("http-codec",new HttpServerCodec());
        ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        ch.pipeline().addLast("http-chinked",new ChunkedWriteHandler());
        ch.pipeline().addLast("handler",new MyWebStockHandler());
    }

}
