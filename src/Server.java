import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) throws Exception {
		int PORT = 0;

		BufferedReader read = new BufferedReader(new FileReader("C:/config.txt"));
		String tempStr, user_input=null;
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the File Name below...");
	    user_input = in.nextLine();
	    in.close();
	    System.out.println("Splitting the file "+user_input+"...");
	    new FileSplit6(user_input);
	    System.out.println("Splitting Completed!");
		while ((tempStr = read.readLine()) != null) {
			// System.out.println(tempStr.charAt(0));
			if (tempStr.charAt(0) == '1') {
				try {
					PORT = Integer.parseInt(tempStr.substring(2, 7));
				} catch (Exception e) {
					// TODO: handle exception
					PORT = Integer.parseInt(tempStr.substring(2, 6));
				}
				// }
				// return outPort;

				// return Integer.parseInt(tempStr.substring(7, 11));
			}
		}
		read.close();
		// System.out.println(PORT);

		new Server().runServer(PORT, user_input);
	}

	public void runServer(int PORT, String user_input) throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server Up and Ready for connections at Port " + PORT + "...");
		while (true) {
			Socket socket = serverSocket.accept();
			new ServerThread(socket, user_input).start();
		}
	}

	public class ServerThread extends Thread {
		Socket socket;
		String user_input;

		ServerThread(Socket socket, String user_input) {
			this.socket = socket;
			this.user_input = user_input;
		}

		public void run() {
			try {
				String message = null;

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//
				while ((message = bufferedReader.readLine()) != null) {
					// System.out.println("Incoming Client Message:" +message);
					int client = Integer.parseInt(message);
					System.out.println("Connection Established with Client:" + message);
					int mod = client;
					int count = 0;
					String directory = "C:/results/";
					File[] files = new File(directory).listFiles();
					// finding the number of files to be sent to a particular
					// peer
					// declares an array of integers
					int[] anArray;

					// allocates memory for chunks
					anArray = new int[files.length];

					for (File file : files) {
						String name = file.getName();
						String formatted = String.format("%03d", mod);
						if (name.indexOf(String.valueOf(formatted)) > -1) {
							count = count + 1;
							anArray[mod - 1] = 1;
							mod = mod + 5;
						}
					}
					BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
					DataOutputStream dos = new DataOutputStream(bos);
					// informing the peer about the number of the incoming files
					dos.writeInt(count);
					//dos.flush();
					// sending the corresponding file to the peers
					mod = client;
					// Send the information about the chunks sent to the client
					dos.writeInt(anArray.length); // Sender sends the array
					//dos.flush();								// length prior to data
													// transmission

					// Send all data
					String full_filename =null;
					full_filename = user_input;
					dos.writeUTF(full_filename);
					dos.flush();
					for (int k = 0; k < anArray.length; k++) {
						dos.writeInt(anArray[k]);
					}
					// Sender sends the array length prior to data transmission
					System.out.println("Transmission of chunks initiated for Client" + message + "...");
					for (File file : files) {
						String name = file.getName();
						String formatted = String.format("%03d", mod);
						if (name.indexOf(String.valueOf(formatted)) > -1) {
							System.out.println("Chunk " + mod + " Sent to the Client" + client);
							mod = mod + 5;
							long length = file.length();
							dos.writeLong(length);

							String fname = file.getName();
							dos.writeUTF(fname);

							FileInputStream fis = new FileInputStream(file);
							BufferedInputStream bis = new BufferedInputStream(fis);

							int theByte = 0;
							while ((theByte = bis.read()) != -1)
								bos.write(theByte);
							bos.flush();
							bis.close();

						}
					}
					System.out.println("Chunks Transmission for Client" + message + " Complete");
					System.out.print("/*******************************/");
					System.out.print("/*******************************/");
					if (client == 5) {
						// Everything must be closed
						// dos.close();
						// serverSocket.close();
						return;
					}
				}
				//
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

//