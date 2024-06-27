import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageBitManipulator {
    public static void main(String[] args) {

        String inputFilePath = "imgk.bmp";
        //String outputFilePath = "img_output.png";
        int numberOfBitsToChange = 1000000;

        try {
            byte[] fileBytes = readFileToByteArray(inputFilePath);
            for(int i = 0; i < 100; i++) {
                byte[] fileBytes1 = fileBytes.clone();
                changeRandomBits(fileBytes1, numberOfBitsToChange);
                writeByteArrayToFile("imgs_bmp\\img" + i + ".bmp", fileBytes1);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFileToByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytesArray = new byte[(int) file.length()];
        fis.read(bytesArray);
        fis.close();
        return bytesArray;
    }

    private static void writeByteArrayToFile(String filePath, byte[] bytesArray) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(bytesArray);
        fos.close();
    }

    private static void changeRandomBits(byte[] bytesArray, int numberOfBits) {
        Random random = new Random();

        for (int i = 0; i < numberOfBits; i++) {
            int byteIndex = random.nextInt(54, bytesArray.length);
            //int byteIndex = random.nextInt(34, 1078);
            int bitIndex = random.nextInt(8);


            bytesArray[byteIndex] |= (1 << bitIndex);
        }
    }
}
