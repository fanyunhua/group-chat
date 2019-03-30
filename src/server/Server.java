import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server implements Runnable {
    //服务器端口监听 在发现新用户时
    private ServerSocket ss;
    private int port=1024;
    public Server(int port){
        setPort(port);
    }
    public Server(){}
    public void setPort(int port) {
        this.port = port;
    }
    public Thread thread=new Thread(this);
    public void start(){
        //非阻塞式
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            ss=new ServerSocket(this.port);
            Main.logger.info("Server is Created.Port is "+port+".");
            while (true){
                Socket s=ss.accept();
                Connect a=new Connect();
                a.socket=s;
                a.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
