package websocket; /**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/8
 * @Time: 20:51
 * To change this template use File | Settings | File Templates.
 */


import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 存储工程的全局配置
 */
public class NettyConfig {

    /**
     * 存储每个客户端接入进来的channel对象
     */
    public static ChannelGroup group=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
