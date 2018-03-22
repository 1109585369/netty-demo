package websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/9
 * @Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class Main {


    public static void main(String[] args) {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();

        try {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new MyWebStockHandler());
            System.out.println("服务器开启等待客户链接");
            Channel ch=b.bind(8888).sync().channel();
            ch.closeFuture().sync();
        }catch (Exception e){

        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
