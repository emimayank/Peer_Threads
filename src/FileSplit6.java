import java.io.File;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
public class FileSplit6 {

	public static void main(String[] args) {
		try{
			String user_input=null;
	        new FileSplit6(user_input);
	    }catch (Exception ex){
	        Logger.getLogger(FileSplit6.class.getName()).log(Level.SEVERE, null, ex);
	    }

	    }
	    File f=null;
	    int readInt;
	    RandomAccessFile fin, fout;
	    byte b[] = new byte[2048];
	    long e= 0L;
	    int j= 1;
	    String usr_inpt=null;
	    public FileSplit6(String user_input) throws Exception{
	    	String s = "C:\\input\\";
	    	s= s.concat(user_input);
	    	File temp=new File(s);
	    	usr_inpt= user_input;
	    	f=temp;
	        fin=new RandomAccessFile(f,"r");
	        try{
            	doPart();
            }
            catch (Exception e){
            	//do nothing
            }
	    }
	    public void doPart() throws Exception
	    {	String formatted = String.format("%03d", j);
	    	j=j+1;
	        fout=new RandomAccessFile("C:/results/"+usr_inpt+"."+formatted,"rw");
	        while((readInt=fin.read(b))!=-1){
	            fout.write(b,0,readInt);
	            e+= readInt;
	            if(e==102400){
	                e=0L;
	                fout.close();
	                try{
	                	doPart();
	                }
	                catch (Exception e){
	                	//do nothing
	                }
	            }
	        }
	    fout.close();
	    fin.close();

	}

}
