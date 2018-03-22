package nettybeginner;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/8
 * @Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf in=(ByteBuf) msg;
            System.out.println(in.toString(CharsetUtil.UTF_8));
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }


    /**
     * 异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        //出现异常就关闭
        cause.printStackTrace();
        ctx.close();
    }
}
