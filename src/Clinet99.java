import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Clinet99
{

	int i, flag;
	static String iaddr;
	static int port;
	String messagein, messageout;
	public DataInputStream instream;
	public InputStreamReader isr;
	public DataOutputStream outstream;
	public BufferedReader br;

	public Clinet99(String iaddr, int port)
	{
		this.iaddr = iaddr;
		this.port = port;
	}

	public void linkStart()
	{
		try
		{
			Socket socket = new Socket(InetAddress.getByName(iaddr), port);

			outstream = new DataOutputStream(socket.getOutputStream());
			instream = new DataInputStream(socket.getInputStream());

			isr = new InputStreamReader(System.in);
			br = new BufferedReader(isr);
			System.out.println("Input data on keyboard...");

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
			e.printStackTrace();
		}
	}
	// public static void main(String[] args)
	// {
	// if (args.length < 2)
	// {
	// System.out.println("USAGE:java Client99 [iaddr][port]");
	// System.exit(1);
	// }
	//
	// iaddr = args[0];
	// port = Integer.parseInt(args[1]);
	// // Clinet99 ClinetStart = new Clinet99();
	//
	// }

}
