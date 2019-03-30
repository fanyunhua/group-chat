
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;

public class Message {
    private long time;
    private String message;
    private String user;
    public Message(/*final*/ String message, final InetAddress isa){
        time=new Date().getTime();
        this.message=message;
        if (isa==null)
            user="Server";
        else
            user=isa.toString();
    }
    public long getTime() {
        return time;
    }
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return new StringBuilder()
                .append("User:")
                .append(user)
                .append(":")
                .append(message)
                .toString();
    }
    public void addtomessages(){
        Dyke.messages.add(this);
        Dyke.end_message_time=this.time;
        Main.logger.log(Level.INFO,"new msg"+toString());
    }
}
