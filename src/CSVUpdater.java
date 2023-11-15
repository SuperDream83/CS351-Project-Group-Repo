import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVUpdater {
    private static final String CSV_FILE_NAME = "C:\\Users\\Aidan\\Documents\\Uni Work\\Year 3\\CS351\\Group Project\\OnlineSystem\\Resources\\TEST.csv";
    private static Scanner x;
    public static void main(String[] args) {
        String editTerm = "4444";
        String newID = "2222";
        String newName = "Donald";
        String newAge = "69";

        editRecord(CSV_FILE_NAME, editTerm, newID, newName, newAge);
    }

    public static void editRecord(String filePath, String editTerm, String newID, String newName, String newAge){
        String tempFile = "temp.txt";
        File oldFile = new File(filePath);
        File newFile = new File("C:\\Users\\Aidan\\Documents\\Uni Work\\Year 3\\CS351\\Group Project\\OnlineSystem\\Resources\\" + tempFile);
        String ID = "";
        String name = "";
        String age = "";

        try {
            FileWriter fw = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            x = new Scanner(new File(filePath));
            x.useDelimiter("[,]");

            while (x.hasNext()){
                ID = x.next();
                name = x.next();
                age = x.next();

                if (ID.equals(editTerm)){
                    pw.println(newID + "," + newName + "," + newAge);
                } else {
                    pw.println(ID + "," + name + "," + age);
                }
            }
            x.close();
            pw.flush();
            pw.close();
            oldFile.delete();
            File dump = new File(filePath);
            newFile.renameTo(dump);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
