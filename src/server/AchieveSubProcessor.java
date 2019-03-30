
import java.util.ArrayList;
import java.util.logging.Level;

public final class AchieveSubProcessor {

    static InDatatOut post_send(Connect c,String data){
        int f=data.indexOf(' ');
        String a=data.substring(0,f);
        int da=Integer.parseInt(a);
        String message=data.substring(f+1).substring(0,da);
        new Message(message,c.socket.getInetAddress()).addtomessages();
        InDatatOut out=new InDatatOut();
        out.d="server:getsend";
        out.p=message;
        return out;
    }
    static InDatatOut post_alluser(Connect c,String data){
        InDatatOut out=new InDatatOut();
        out.d="server:alluser";
        out.p=null;
        return out;
    }

    static InDatatOut heart_client(Connect c,String data){
        InDatatOut ido=new InDatatOut();
        if(data.contains("initiative"))
        {
            ido.p="heart:server";
            ido.d="passive";
        }
        else if(data.contains("passive"))
        {
            c.heart= Connect.Heart.Idea;
            ido.p="pass";
        }

        return ido;
    }


    static String broadcast_new_client(Connect c,String i){
        ArrayList<String> nuser=new ArrayList<>();
        long[] timee=new long[1];
        Dyke.connect_poll.forEach((s,cc)->{
            if(cc.time>c.end_change_connect_time){
                nuser.add(s);
                timee[0]=timee[0]>cc.time?timee[0]:cc.time;
            }
        });
        c.end_change_connect_time=timee[0];
        ArrayList<String>s=new ArrayList<>();
        nuser.forEach(ss->s.add(String.join(" ",new String[]{"broadcast:new_client",Integer.toString(ss.length()),ss})));
        return String.join("\n",LObject_to_LString(s.toArray()));
    }
    static String broadcast_end_client(Connect c,String i){
        ArrayList<String> dis=new ArrayList<>();
        long[] ll=new long[]{c.end_change_disconnect_time};//解决匿名函数无法修改上层的变量的问题
        Dyke.disconnect_pool.forEach((s,l)-> { if (l > c.end_change_disconnect_time) {dis.add("broadcast:end_client "+s.length()+" "+s);ll[0]=ll[0]>l?ll[0]:l;} });
        c.end_change_disconnect_time=ll[0];
        return String.join("\n",LObject_to_LString(dis.toArray()));
    }
    static String broadcast_message(Connect c,String i){
        long[] time=new long[1];
        ArrayList<String> data=new ArrayList<>();
        Dyke.messages.forEach((m)->{
            long t=m.getTime();
            if(t>c.end_message_time){
                time[0]=time[0]>t?time[0]:t;
                data.add(m.toString());
            }
        });
        c.end_message_time=time[0];
        StringBuilder sb=new StringBuilder();
        data.forEach((s)-> sb.append(String.join(" ",new String[]{"broadcast:message",Integer.toString(s.length()),s})).append("\n"));
        //Main.logger.log(Level.INFO,sb.toString());
        return sb.toString();
    }
    static String server_getsend(Connect c,String i){
        return "server:getsend "+i.hashCode();
    }
    static String server_alluser(Connect c,String i){
        String[] users=LObject_to_LString(Dyke.connect_poll.keySet().toArray());
        String user_message=String.join(" ",new String[]{Integer.toString(users.length),String.join(" ",users)});
        return new StringBuilder()
                .append("server:alluser ")
                .append(user_message.length())
                .append(" ")
                .append(user_message)
                .append("\n")
                .toString();
    }
    static String heart_server(Connect c,String i){
        return "heart:server "+i+"\n";


    }
    static String [] LObject_to_LString(Object[] lo){
        String[] ls=new String[lo.length];
        System.arraycopy(lo,0,ls,0,lo.length);
        return ls;
    }
}
