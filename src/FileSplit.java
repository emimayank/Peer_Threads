

// http://stackoverflow.com/questions/10864317/how-to-break-a-file-into-pieces-using-java
import java.io.*;

public class FileSplit {
	public static int splitFile(File f) throws IOException {
		int partCounter = 0;// I like to name parts from 001, 002, 003, ...
							// you can change it to 0 if you want 000, 001, ...

		int sizeOfFiles = 100 * 1024;// 100KB
		byte[] buffer = new byte[sizeOfFiles];

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {// try-with-resources
																							// to
																							// ensure
																							// closing
																							// stream
			String name = f.getName();

			int tmp = 0;
			System.out.println("---------------SPLITTING FILE START---------------");
			System.out.println("Part\tFileName\tSize(in KB)");
			while ((tmp = bis.read(buffer)) > 0) {
				// write each chunk of data into separate file with different
				// number in name
				partCounter++;
				String splitFileName = new String(name + "." + String.format("%03d", partCounter));

				File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter));
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, tmp);// tmp is chunk size
					System.out.println((partCounter) + "\t" + splitFileName + "\t" + tmp / 1024);
				}
			}
			System.out.println("Total number of parts = " + (partCounter));
			System.out.println("-----------------SPLITTING FILE END-----------------");
			return partCounter;
		}
	}
	
	public byte[] getFileByteStream(String filePath) {
		try {
			File f = new File(filePath);
			int retryCount = 0;
			while (true) {
				if (f.exists())
					break;
				else {
					try {
						Thread.sleep(1000);
						retryCount++;
						if (retryCount > 50) {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			FileInputStream inputFile = new FileInputStream(filePath);
			byte[] byteStream = new byte[(int) (inputFile.getChannel().size())];
			inputFile.read(byteStream);
			inputFile.close();

			return byteStream;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public void setFileByteStream(String filePath, byte[] byteStream) {
		try {
			FileOutputStream outputFile = new FileOutputStream(filePath);
			outputFile.write(byteStream);
			outputFile.flush();
			outputFile.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void mergeFile(String filePath, int splitCount) {
		FileInputStream inputFile = null;
		FileOutputStream outputFile = null;

		try {
			outputFile = new FileOutputStream(filePath);
			System.out.println("Merging file now....");
			for (int i = 1; i <= splitCount; i++) {
				inputFile = new FileInputStream(filePath + "." + String.format("%03d", i));
				byte[] fullBuffer = new byte[(int) (inputFile.getChannel().size())];
				inputFile.read(fullBuffer);
				inputFile.close();

				outputFile.write(fullBuffer);

				// Delete the split file
				//File fileToDelete = new File(filePath + "." + i);
				//fileToDelete.delete();
			}

			outputFile.flush();
			outputFile.close();

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/*
	 * public static void main(String[] args) throws IOException {
	 * 
	 * splitFile(new File(
	 * "C:\\Users\\neyaz\\Desktop\\Computer Networks Project\\Desert.jpg")); }
	 */
}