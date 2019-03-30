
import java.util.*;
import java.util.logging.Level;

public final class Dyke {
    public final static Vector<Message> messages=new Vector<>();
    //消息存储池
    public final static Hashtable<String,Connect> connect_poll=new Hashtable<>();
    //连接者
    public final static ThreadGroup thread_pool=new ThreadGroup("Connects");
    //函数池
    public final static Hashtable<String,Long> disconnect_pool=new Hashtable<>();
    //断连者存储池
    public static volatile long end_message_time=0;
    //最后一条传来消息的时间
    public static volatile long end_change_connect_time=0;
    //最后一位新连接者连接时间
    public static volatile long end_change_disconnect_time=0;
    //最后一位连接者断开连接的时间
    public final void Dyke(){
        throw new Error();
    }
    public static void put_new_connect(Connect c){
        String s=c.socket.getInetAddress().getHostAddress()+":"+c.socket.getPort();
        connect_poll.put(s,c);
        long cc= new Date().getTime();
        c.time=cc;
        end_change_connect_time=cc;
        Main.logger.log(Level.INFO,"new msg"+s);
    }
    public static void put_disconnect(Connect c){
        String s=c.socket.getInetAddress().getHostAddress()+":"+c.socket.getPort();
        if(!connect_poll.values().contains(c))
            return;
        connect_poll.remove(s);
        long time=new Date().getTime();
        disconnect_pool.put(s,time);
        end_change_disconnect_time=time;
        Main.logger.log(Level.INFO,"closed"+s);
    }
}
