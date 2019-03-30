
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;



public class ConnectToServer {
	private InputStream in_put;
	private OutputStream out_put;
	private Socket socket;
	private DataInputStream data_in_put;
	private DataOutputStream data_out_put;
	private String host;
	private Scanner sc;
	private String SEND_MSG_TITLE = "post:send ";
	private String GET_ALL_ONLINE = "post:alluser \n";
	private String HEARTBEA_CLIENT = "heart:client initiative\n";
	private String HEARTBEA_SERVER = "heart:server initiative\n";
	private String GET_SEND = "server:getsend \n";
	private String MESSAGE = "broadcast:message ";
	private String SERVER_ALLUSER = "server:alluser";
	private String NEW_CLIENT = "broadcast:new_client";
	static GUI gui;
	public ConnectToServer( int port) {
		try {
			//初始化socket
			System.out.println("input ip");
			String a = null;
			
			sc = new Scanner(System.in);
			//获取ip
			a = sc.next();
			
			//初始化界面对象
			gui = new GUI("Client");
			//监听清屏按钮
			gui.clear_data_msg();
			
			//初始化socket
			socket = new Socket(a, port);
			
			System.out.println("connect to server is OK");
			//初始化流
			out_put = socket.getOutputStream();
			//初始化数据流
			data_in_put = new DataInputStream(in_put);
			data_out_put = new DataOutputStream(out_put);
			//监听按钮的动作
			listener_Button();
		} catch (Exception e) {
			System.out.println("Invalid IP address");
		}
	}
	private void listener_Button()
	{
		//用一个线程来维护按钮的监听
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				gui.send_msg(socket,data_out_put);
			}
		}).start();
		
		//用一个线程来维护按钮的监听
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				gui.cat_all_online(socket, data_out_put);
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true)
				{
					// TODO Auto-generated method stub
					
					try {
						Thread.sleep(10000);
						gui.send_heart(socket, data_out_put, HEARTBEA_CLIENT);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}).start();
	}
	//关闭所有连接
	public void close()
	{
		try {
			data_in_put.close();
			data_out_put.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//监听input Stream
	public void get_input_stream()
	{
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true)
					{
						in_put = socket.getInputStream();
						DataInputStream data_data = new DataInputStream(in_put);
						String data = data_data.readUTF();
						System.out.println(data);
						
						
						if(data.substring(0,9 ).equals(SERVER_ALLUSER.substring(0,9)))
						{
							//server:alluser 213 4 Socket[addr=/192.168.1.12,port=51159,localport=1024]
							//数据格式未定  等待处理
							gui.setmess(data.substring(10,data.length()));
						}
						
//						if(data.substring(0,10).equals(HEARTBEA_SERVER.substring(0,10)))
//						{
//							System.out.println(data);
//							//发送被动心跳
//							new Thread(new Runnable() {
//								
//								@Override
//								public void run() {
//									// TODO Auto-generated method stub
////									gui.send_heart(socket, data_out_put,HEARTBEA_CLIENT);
//									gui.send_msg(socket, data_out_put);
//								}
//							}).start();
//							
//						}
						if(data.substring(0, 19).equals(NEW_CLIENT.substring(0, 19)))
						{
							gui.setmess("新用户上线");
						}
						if(data.subSequence(0, 15).equals(MESSAGE.subSequence(0, 15)))
						{
							System.out.println("data"+data);
							String[] a = data.split("User:/");
							gui.setmess(a[1]);
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		th.start();
	}
	
		
}
