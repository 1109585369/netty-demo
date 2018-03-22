package websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import websocket.NettyConfig;

import javax.naming.Context;
import javax.xml.soap.Text;
import java.util.Date;

/**
 * @Created with IntelliJ IDEA.
 * @author: Administrator
 * @Date: 2018/3/8
 * @Time: 20:54
 * To change this template use File | Settings | File Templates.
 */

/**
 * 接收处理、响应、客户端webStock请求的核心业务
 */
public class MyWebStockHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    private static final String WEB_SOTCKET_URL="ws://localhost:8888/websocket";


    /**
     * 客户端与服务端创建连接的调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启:");
    }

    /**
     * 客户端与服务端断开连接的调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //super.channelInactive(ctx);
        NettyConfig.group.remove(ctx.channel());
        System.out.println("客户端与服务端已断开");
    }

    /**
     *  客户端与服务端发送过来的数据结束之后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //super.channelReadComplete(ctx);
           ctx.flush();
    }

    /**
     * 工程出现异常的时候调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     * <p>
     * Is called for each message of type {@link }.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    /**
     * 五福段处理客户端websocket请求的核心方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //处理客户端向服务端发起的http握手请求的业务
        if(msg instanceof FullHttpRequest){
            handHttpRequest(ctx, (FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){
            //处理websocket连接业务
            handWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 处理客户端与服务端之前的websocket业务
     * @param ctx
     * @param frame
     */
    private void handWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //判断是否关闭websocket的指令
        if(frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        }
        //判断是否是Ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //判断是否是二进制消息，如果是。抛出异常
        if(!(frame instanceof TextWebSocketFrame)){
            System.out.println("目前我们不支持二进制消息");
            throw new RuntimeException("【"+this.getClass().getName()+"】不支持消息");
        }

        //返回应答消息
        //获取客户端向服务端发送的消息
        String request=((TextWebSocketFrame)frame).text();
        System.out.println("服务端收到客户端的消息====>>>"+request);
        TextWebSocketFrame tws=
                new TextWebSocketFrame(new Date().toString()+ctx.channel().id()+"=====>>>"+request);


        //群发，服务端向每个链接上的客户端群发消息
        NettyConfig.group.writeAndFlush(tws);

    }


    /**
     * 处理客户端向服务端发起的http握手请求的业务
     */
    private  void handHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        if(!req.getDecoderResult().isSuccess()||!("websotcket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory=new WebSocketServerHandshakerFactory(WEB_SOTCKET_URL,null,false);
        handshaker=wsFactory.newHandshaker(req);
        if(handshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }


    /**
     *    服务端向客户端响应消息
     * @param ctx
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req,DefaultFullHttpResponse res){

        if(res.getStatus().code()!=200){
            ByteBuf buf= Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);

            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f=ctx.channel().writeAndFlush(res);
        if(res.getStatus().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}


