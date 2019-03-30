
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GUI extends JFrame {
	private String SEND_MSG_TITLE = "post:send ";
	private String GET_ALL_ONLINE = "post:alluser \n";
	private JTextField ip;
	private JButton login, clear, send, get_all_client;
	public TextArea text, msg_text;
	private String HEARTBEA_CLIENT = "heart:client initiative\\n";
	Socket socket;
	public GUI(String s) {
		// 设置标题
		super(s);
		// 设置窗口大小
		this.setSize(650, 550);
		// 启动时窗口居中
		this.setLocationRelativeTo(null);
		// 设置窗口大小不可变
		this.setResizable(false);

		// 创建面板组件
		JPanel jp = new JPanel();
		jp.setLayout(null);
		init_view(jp);
		
		

		this.add(jp);
		// 设置关闭方式
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 设置界面可视化
		this.setVisible(true);
	}
	

	//初始化面板控件
	private void init_view(JPanel jp) {
		// 获取IP输入
		ip = new JTextField(8);
		ip.setBounds(50, 20, 180, 30);
		// 限制键盘输入
		ip.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char keyChar = e.getKeyChar();
				// 限制键盘输入
				if ((keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) || keyChar == '.') {
				} else {
					e.consume(); // 关键，屏蔽掉非法输入
				}
			}
		});
		// 设置字体
		ip.setFont(new Font("宋体", Font.BOLD, 20));

		// 登陆到服务器
		login = new JButton("Login");
		login.setBounds(290, 20, 80, 30);

		// 广播提示框
		text = new TextArea();
		text.setBounds(50, 80, 510, 300);

		// 清除按钮
		clear = new JButton("clear");
		clear.setBounds(390, 20, 80, 30);

		// 获取所有在线
		get_all_client = new JButton("LOOK");
		get_all_client.setBounds(480, 20, 80, 30);

		// 获取输入消息
		msg_text = new TextArea();
		msg_text.setBounds(50, 400, 450, 80);

		// 发送按钮
		send = new JButton("Send");
		send.setBounds(500, 400, 80, 60);

		jp.add(send);
		jp.add(msg_text);
		jp.add(ip);
		jp.add(login);
		jp.add(text);
		jp.add(clear);
		jp.add(get_all_client);
	}
	//向服务器广播消息
	public void send_msg(Socket client , DataOutputStream a) {
		//点击事件
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//构造消息体
				if(msg_text.getText().length()>0)
				{
					String msg = SEND_MSG_TITLE+msg_text.getText().toString().length()+" "+msg_text.getText().toString()+"\n";
					
					try {
						//发送消息
						a.writeUTF(msg);
						msg_text.setText("");
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				
				
			}
		});
		}
	public void send_heart(Socket client , DataOutputStream a,String str) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
										
							try {
								//发送消息
								a.writeUTF(str);
								msg_text.setText("");
							}
							catch (IOException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						
						
					}
				}).start();
				//构造消息体
				
				
			
		}
		//清屏
	public void clear_data_msg()
	{
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//清屏
				text.setText("");
			}
		});
	}
	//获取所有在线客户
	public void cat_all_online(Socket client, DataOutputStream a)
	{
		get_all_client.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							a.writeUTF(GET_ALL_ONLINE);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}
	//未实现预定功能---弃用
//	public void logion() throws UnknownHostException, IOException
//	{
//		login.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				try {
//					socket = new Socket(ip.getText().toString(), 1024);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
//
//	}
	//向面板发送消息
	void setmess(String str)
	{
		text.setText(text.getText()+"\n"+str);
	}
}
