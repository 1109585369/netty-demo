package nettybeginner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sun.applet.Main;
import websocket.DiscardServerHandler;

/**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/8
 * @Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port){
        super();
        this.port=port;
    }

    public void run()throws InterruptedException{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();
        System.out.println("准备运行端口"+port);
        try {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture f=b.bind(port).sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {

            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("关闭");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        int port;
        if(args.length>0) {
            port = Integer.parseInt(args[0]);
        }else{
            port=9090;
        }
        new DiscardServer(port).run();
        System.out.println("server is run");
    }
}
                               