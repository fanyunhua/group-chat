
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Connect extends Thread implements Runnable{
    /**
     * 对象：链接
     * 用处：服务器与客户端的上下文
     * */
    private final static HashMap<String,InSubProcessor> isubProcessor=new HashMap<>();
    private final static HashMap<String,OutSubProcessor> osubProcessor=new HashMap<>();
    static {
        //将函数放置到HashMap中
        isubProcessor.put("post:send",AchieveSubProcessor::post_send);
        isubProcessor.put("post:alluser",AchieveSubProcessor::post_alluser);
        isubProcessor.put("heart:client",AchieveSubProcessor::heart_client);

        osubProcessor.put("broadcast:new_client",AchieveSubProcessor::broadcast_new_client);
        osubProcessor.put("broadcast:end_client",AchieveSubProcessor::broadcast_end_client);
        osubProcessor.put("broadcast:message",AchieveSubProcessor::broadcast_message);
        osubProcessor.put("server:getsend",AchieveSubProcessor::server_getsend);
        osubProcessor.put("server:alluser",AchieveSubProcessor::server_alluser);
        osubProcessor.put("heart:server",AchieveSubProcessor::heart_server);
        osubProcessor.put("pass",(var0,var1)-> "");
    }
    Connect(){
        super(Dyke.thread_pool,"");
    }
    public Socket socket;
    private volatile boolean is_over=false;
    public long end_message_time=0;
        //最后一次推送message的时间
    private long last_input_time=0;
        //最后一次客户端向服务端发送消息的时间 用于处理心跳
    public Heart heart=Heart.Idea;
    public long end_change_connect_time=0;
        //最后一位新连接者
    public long end_change_disconnect_time=0;
        //最后一位断开者
    public long time=0;
    private DataInputStream is;
    private DataOutputStream os;
    private void sendNewMessage() throws IOException {
        //发出新
        for (String data:osubProcessor.get("broadcast:message").run(this,null).split("\n"))
            if (data.length()>3)
                os.writeUTF(data);
    }
    private void sendNewConnect() throws IOException{
        for (String data:osubProcessor.get("broadcast:new_client").run(this,null).split("\n"))
            if (data.length()>3)
                os.writeUTF(data);
    }
    public void over() throws IOException {
        is_over=true;
        os.close();
        is.close();
        socket.close();
        Dyke.put_disconnect(this);
    }
    private void sendNewDisconnect()throws IOException{
        for (String data:osubProcessor.get("broadcast:end_client").run(this,null).split("\n"))
            if (data.length()>3)
                os.writeUTF(data);
    }
    void pullmessage() throws IOException {
        last_input_time=new Date().getTime();
        String mes=is.readUTF();
        heart=Heart.Idea;
        try {
            int s_0=mes.indexOf(' ');
            String command=mes.substring(0,s_0);
            String message=mes.substring(1+s_0);
            InDatatOut ido=isubProcessor.get(command).run(this,message);
            String s=osubProcessor.get(ido.d).run(this,ido.p);
            if(s.length()>3)
                os.writeUTF(s);
        }catch (NullPointerException ignored) { }
    }
    public void heart_hand() throws IOException {
        switch (heart){
            case Idea:
                if(new Date().getTime()-this.last_input_time>20000){
                    //如果对方在20s内没有回应 发出主动心跳
                    heart=Heart.Issue;
                    os.writeUTF(osubProcessor.get("heart:server").run(this,"initiative"));
                }
                break;
            case Issue:
                if (new Date().getTime()-this.last_input_time>6000)
                    is_over=true;
        }
    }
    public void run() {
        try {
            is=new DataInputStream(socket.getInputStream());
            os=new DataOutputStream(socket.getOutputStream());
            end_message_time=new Date().getTime();
            end_change_connect_time=new Date().getTime();
            end_change_disconnect_time=new Date().getTime();
            Dyke.put_new_connect( this);
            setPriority(MIN_PRIORITY);
            last_input_time=new Date().getTime();
            while (!is_over){
                if (end_message_time < Dyke.end_message_time)
                    //服务器内有新消息
                    sendNewMessage();
                else if(end_change_connect_time<Dyke.end_change_connect_time)
                    //有新连接者
                    sendNewConnect();
                else if(end_change_disconnect_time<Dyke.end_change_disconnect_time)
                    //有新断开者
                    sendNewDisconnect();
                else if(socket.isClosed()) {
                    //链接被关闭
                    over();
                    break;
                }
                else if(is.available()>0)
                    //接受客户端消息
                    pullmessage();
                else {
                    //没有新任务 线程开始休眠
                    Thread.yield();
                    sleep(0x20);
                    Thread.yield();
                }
                heart_hand();
            }
            over();
            //Main.logger.info(socket.getInetAddress().toString()+"用户退出");
        } catch (InterruptedException|IOException ia) {
            try {
                over();
            }catch (IOException i){}
            System.out.println("error ：="+socket.getInetAddress().toString()+" ERROR\n"+ia.getMessage());
        }
   }
   public enum Heart{
       Idea,
       Issue
   }
}
