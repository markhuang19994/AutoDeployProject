import java.io.*;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/1, MarkHuang,new
 * </ul>
 * @since 2018/2/1
 */
public class CopyLog {
    public static void main(String args[]) throws IOException, InterruptedException {
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\1710002NB01\\3D Objects\\1234555.txt"));
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\1710002NB01\\3D Objects\\1.txt"));
        int temp = 0;
        while(true){
            while((temp = fis.read())!=-1){
                fos.write(temp);
            }
            Thread.sleep(200);
        }
    }
}
