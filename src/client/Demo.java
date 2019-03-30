
import java.io.IOException;

public class Demo {
	static final int port = 1024;
	public static void main(String[] args) throws IOException {
		ConnectToServer se = new ConnectToServer(port);
		se.get_input_stream();	
	}
}
