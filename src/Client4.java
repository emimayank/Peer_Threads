import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Client4 {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

		Client4 cl4 = new Client4();
		cl4.socket_clnt();

	}

	public void socket_clnt() throws IOException, InterruptedException {
		int SPORT =0;
		BufferedReader read = new BufferedReader(new FileReader("C:/config.txt"));
		String tempStr;
		while ((tempStr = read.readLine()) != null) {
			// System.out.println(tempStr.charAt(0));
			if (tempStr.charAt(0) == '1') {
				// System.out.println ("Listening port = " +
				// tempStr.substring(7, 11));
				try {
					SPORT = Integer.parseInt(tempStr.substring(2, 7));
				} catch (Exception e) {
					// TODO: handle exception
					SPORT = Integer.parseInt(tempStr.substring(2, 6));
				}
			}
		}
		read.close();
		Socket socket = null;
		try{
			socket = new Socket("localhost", SPORT);
		}
		catch (Exception e){
			System.out.println("Client4 could not connect to the server...");
		}
		System.out.println("Client4 connected to the Main Server for receiving it's set of chunks at " + socket.getInetAddress().getHostAddress()+":"+SPORT);
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
		// while(true){
		// inform the client about the client number
		String readerInput = "4";
		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
		DataInputStream dis = new DataInputStream(bis);
		printWriter.println(readerInput);
		String dirPath = "C:/output4/", full_filename=null;
		int filesCount = dis.readInt();
		File[] files = new File[filesCount];
		// Receive the input array with incoming chunk information
		int arrlnth = dis.readInt();
		full_filename = dis.readUTF();
		System.out.println("Name of the Incoming File: "+full_filename);
		int[] anArray;
		anArray = new int[arrlnth];
		for (int k = 0; k < arrlnth; k++) {
			int l = dis.readInt();
			anArray[k] = l;
			if(anArray[k]==1){
				System.out.println("Receiving the chunk "+(k+1)+"from the Main Server");
			}
		}

		for (int i = 0; i < filesCount; i++) {
			long fileLength = dis.readLong();
			String fileName = dis.readUTF();
			files[i] = new File(dirPath + "/" + fileName);

			FileOutputStream fos = new FileOutputStream(files[i]);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			for (int j = 0; j < fileLength; j++)
				bos.write(bis.read());

			bos.close();
		}
		dis.close();
		socket.close();

		ClientClient4 CC = new ClientClient4(anArray, arrlnth);
		CC.start();
		ClientServer4 CS = new ClientServer4(anArray, arrlnth);
		CS.start();

	}
}

class ClientClient4 extends Thread {
	int dwldScktNos;
	private int[] anArray;
	private int arrlnth;
	Socket dwldSckt;

	public ClientClient4(int[] anArray, int arrlnth) {
		this.anArray = anArray;
		this.arrlnth = arrlnth;
	}

	public void run() {
		try {

			boolean status_chk = true;

			while (status_chk) {
				try {
					int DPORT =0;
					BufferedReader read = new BufferedReader(new FileReader("C:/config.txt"));
					String tempStr;
					while ((tempStr = read.readLine()) != null) {
						// System.out.println(tempStr.charAt(0));
						if (tempStr.charAt(0) == '4') {
							// System.out.println ("Listening port = " +
							// tempStr.substring(7, 11));
							try {
								DPORT = Integer.parseInt(tempStr.substring(2, 7));
							} catch (Exception e) {
								// TODO: handle exception
								DPORT = Integer.parseInt(tempStr.substring(2, 6));
							}
							// }
							// return outPort;

							// return Integer.parseInt(tempStr.substring(7, 11));
						}
					}
					read.close();
					dwldSckt = new Socket("localhost", DPORT);
					status_chk = false;
					System.out.println ("Client4 connected to it's Download Neigbour: 3");
					// Write Logic for file Download from Client5
					boolean file_complete = true;
					int i = 0;
					PrintWriter printWriter = new PrintWriter(dwldSckt.getOutputStream(), true);
					BufferedInputStream bis = new BufferedInputStream(dwldSckt.getInputStream());
					DataInputStream dis = new DataInputStream(bis);
					while (file_complete) {
						// send the chunk info to server here!
						if (i == arrlnth) {
                    		try {
								TimeUnit.SECONDS.sleep(5);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							i = 0;
						}
						int chnk_no = i;
						int chnk_val = anArray[i];
						if (chnk_val == 1) {
							i = i + 1;
							printWriter.println(-1);
							printWriter.flush();
							continue;

						}
						printWriter.println(chnk_no);
						printWriter.flush();
						printWriter.println(chnk_val);
						printWriter.flush();

						// get chunk presence from server
						int chnk_presence = dis.readInt();
						File files = null;
						String dirPath = "C:/output4/";
						if (chnk_presence == 1) {
							long fileLength = dis.readLong();
							String fileName = dis.readUTF();
							files = new File(dirPath + "/" + fileName);

							FileOutputStream fos = new FileOutputStream(files);
							BufferedOutputStream bos = new BufferedOutputStream(fos);

							for (int j = 0; j < fileLength; j++)
								bos.write(bis.read());

							bos.flush();
							bos.close();
							fos.close();

							anArray[i] = 1;
							i = i + 1;
							System.out.println("Chunk "+i+" Arrived from Client3");
						} else {
							i = i + 1;
							continue;
						}
						String directory = "C:/output4/";
						File[] files2 = new File(directory).listFiles();
						if (files2.length == arrlnth) {
							file_complete = false;
							String f_name = null, t_name = null;
							for (File file : files2) {
								f_name = file.getName();
								int len = f_name.length();
								len = len - 4;
								t_name = f_name.substring(0, Math.min(f_name.length(), len));
								// System.out.println(name);
								break;
							}
							File[] chunk_files = new File[arrlnth];
							String sourcefilepath = "C:/output4/";
							String targetfilepath = sourcefilepath.concat(t_name);
							File mergedFile = new File(targetfilepath);
							int count = 0;
							for (File file : files2) {
								sourcefilepath = sourcefilepath.concat(file.getName());
								chunk_files[count] = new File(sourcefilepath);
								sourcefilepath="C:/output4/";
								count++;
							}
							System.out.println("Merging Files...");
							mergeFiles(chunk_files, mergedFile);

						}
					}
					//
				} catch (ConnectException err) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException ers) {
						ers.printStackTrace();
					}
				}

			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	private void mergeFiles(File[] chunk_files, File mergedFile) {
		// TODO Auto-generated method stub
		FileWriter fstream = null;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(mergedFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (File f : chunk_files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				long chunkLength = 100*1024;
				if(f.length() < chunkLength)
					chunkLength = f.length();
				byte[] buffer= new byte[(int)chunkLength];
				fis.read(buffer);
				out.write(buffer);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(mergedFile);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

class ClientServer4 extends Thread {
	String line;
	int upldCl;
	int upldScktNos;
	private int[] anArray;
	private int arrlnth;

	public ClientServer4(int[] anArray, int arrlnth) {
		this.anArray = anArray;
		this.arrlnth = arrlnth;
	}

	public void run() {
		try {
			int UPORT =0;
			BufferedReader read = new BufferedReader(new FileReader("C:/config.txt"));
			String tempStr;
			while ((tempStr = read.readLine()) != null) {
				// System.out.println(tempStr.charAt(0));
				if (tempStr.charAt(0) == '5') {
					try {
						UPORT = Integer.parseInt(tempStr.substring(2, 7));
					} catch (Exception e) {
						// TODO: handle exception
						UPORT = Integer.parseInt(tempStr.substring(2, 6));
					}
				}
			}
			read.close();
			ServerSocket upldServSckt = new ServerSocket(UPORT);
			Socket upldSckt = upldServSckt.accept();
			System.out.println("Client4 connected to: " + upldSckt.getInetAddress().getHostAddress()+":"+UPORT+" as Server now for Client5");
			// Write Logic for File Upload for Client2
			boolean file_complete = true;
			String message = null;

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(upldSckt.getInputStream()));
			//
			while (file_complete) {
				// for(int i=0;i<arrlnth;i++){
				// receive the chunk info from client here!
				int chnk_no = 0, chnk_val = 0, chnk_presence = 0;
				message = bufferedReader.readLine();
				chnk_no = Integer.parseInt(message);

				if ((Integer.parseInt(message)) == -1) {
					continue;
				}
				message = bufferedReader.readLine();
				chnk_val = Integer.parseInt(message);
				//System.out.println(message);
				BufferedOutputStream bos = new BufferedOutputStream(upldSckt.getOutputStream());
				DataOutputStream dos = new DataOutputStream(bos);
				/*
				 * if (chnk_val == 1) { chnk_presence = 0;
				 * dos.writeInt(chnk_presence);// ????Error happening here????,
				 * // sending file's absence dos.flush(); }
				 */
				if (chnk_val == 0) {
					String directory = "C:/output4/";
					File[] files = new File(directory).listFiles();
					// check for the chunk in it's directory
					for (File file : files) {
						String name = file.getName();
						long length = file.length();
						String formatted = String.format("%03d", chnk_no + 1);
						if (name.indexOf(String.valueOf(formatted)) > -1) {
							// Send the missing chunk
							chnk_presence = 1;
							dos.writeInt(chnk_presence);
							dos.flush();
							dos.writeLong(length);
							dos.flush();
							String fname = file.getName();
							dos.writeUTF(fname);
							dos.flush();
							FileInputStream fis = new FileInputStream(file);
							BufferedInputStream bis = new BufferedInputStream(fis);

							int theByte = 0;
							while ((theByte = bis.read()) != -1)
								bos.write(theByte);
							bos.flush();
							bis.close();
							System.out.println("Chunk "+(chnk_no+1)+" Sent to the Client5");
							break;
						}

					}
					if (chnk_presence == 0) {
						dos.writeInt(chnk_presence);
						dos.flush();

					}
				}
			}
			upldSckt.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
