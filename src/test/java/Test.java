import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/2, MarkHuang,new
 * </ul>
 * @since 2018/2/2
 */
public class Test {
    String fileName = "config.properties";

    public static void main(String[] args) {


        List<File> fileList = new ArrayList<File>();
        fileList.add(new File("C:\\Users\\1710002NB01\\Documents\\project"));


//        File file = new Test().searchFileInDirectory(fileList);
//        System.out.println(file);
    }

//    public File searchFileInDirectory(List<File> fileList) {
//        List<File> tempFileList = new ArrayList<File>();
//        boolean lastFile = true;
//        for (File outerFile : fileList) {
//            for (File innerFile : outerFile.listFiles()) {
//                if (innerFile.isFile()) {
//                    if (fileName.equals(innerFile.getName())) return innerFile;
//                } else if (innerFile.isDirectory()) {
//                    if (innerFile.listFiles().length != 0) lastFile = false;
//                    tempFileList.add(innerFile);
//                }
//            }
//        }
//        fileList = tempFileList;
//        if (lastFile) return null;
//        return searchFileInDirectory(fileList);
//    }

}
