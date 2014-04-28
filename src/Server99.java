import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server99
{
	static int port;
	int flag, i;
	String messagein, messageout;
	public DataInputStream instream;
	public InputStreamReader isr;
	public DataOutputStream outstream;
	public BufferedReader br;

	public Server99(int port)
	{
		this.port = port;
	}

	public void linkStart()
	{
		try
		{
			ServerSocket SS = new ServerSocket(port);
			System.out.println("Server is created and waiting Clinet to connect..." + InetAddress.getLocalHost());

			Socket socket = SS.accept();

			System.out.println("Clinet IP = " + socket.getInetAddress().getHostAddress());

			instream = new DataInputStream(socket.getInputStream());
			outstream = new DataOutputStream(socket.getOutputStream());

			System.out.println("Input data on keyborad...");

		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public String getMsgin()
	{
		try
		{
			messagein = instream.readUTF();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return messagein;
	}

	public void setMsgOut(InputStream in)
	{

		try
		{
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);

			messageout = br.readLine();
			outstream.writeUTF(messageout);

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main(String[] args)
	// {
	//
	// // port = Integer.parseInt(args[0]);
	// // Server99 ServerStart = new Server99(6776);
	// }
}
