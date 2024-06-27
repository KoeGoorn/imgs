import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final int imageAmplitude = 15;
    private static final int noiseAmplitude = 127;

    private static final int number = 7;

    public static void main(String[] args) throws IOException {
        List<Integer> bitListImage = readImageToBitList("imgk.bmp");

        for (int i = 0; i < 7; i++) {
            generateNoiseTxt(bitListImage.size(), noiseAmplitude, "noise" + i + ".txt");
        }

        List<Integer>[] noises = new List[7];
        for (int i = 0; i < number; i++) {
            noises[i] = readTxtFileToList("noises\\noise" + i + ".txt");
        }

        List<Integer>[] images = new List[number];
        for (int i = 0; i < number; i++) {
            images[i] = addNoise(bitListImage, noises[i], 1);
            List<Integer> currentImage = convertToBinaryByAverage(images[i], images[i].stream().mapToInt(Integer::intValue).average().orElse(0));
            restoreBits(bitListImage, currentImage, 1078 * 8);
            saveImage(currentImage, "imgs_main2\\img " + i + ".bmp");
        }
        List<Integer> sumImageBitList = sumImages(images);
        List<Integer> resList = convertToBinaryByAverage(sumImageBitList, sumImageBitList.stream().mapToInt(Integer::intValue).average().orElse(0));

        restoreBits(bitListImage, resList, 1078 * 8);
        saveImage(resList, "imgs_main2\\average_img.bmp");
    }

    private static void restoreBits(List<Integer> original, List<Integer> newList, int count) {
        for (int i = 0; i < count; i++) {
            newList.set(i, original.get(i) == imageAmplitude ? 1 : 0);
        }
    }

    private static List<Integer> sumImages(List<Integer>[] images) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < images[0].size(); i++) {
            int sum = 0;
            for (List<Integer> image : images) {
                sum += image.get(i);
            }
            result.add(sum);
        }
        return result;
    }

    private static List<Integer> readImageToBitList(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        List<Integer> bitList = new ArrayList<>();

        int byteRead;
        while ((byteRead = fis.read()) != -1) {
            String bits = String.format("%8s", Integer.toBinaryString(byteRead)).replace(' ', '0');
            for (char bit : bits.toCharArray()) {
                if (bit == '1') {
                    bitList.add(imageAmplitude);
                } else {
                    bitList.add(0);
                }
            }
        }
        fis.close();
        return bitList;
    }

//    private static void saveListToFile(List<Integer> list, String fileName) {
//        try {
//            FileOutputStream fos = new FileOutputStream(fileName);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(list);
//            oos.close();
//            fos.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    private static List<Integer> generateNoise(int length) {
//        List<Integer> noiseList = new ArrayList<>();
//        Random rand = new Random();
//        for (int i = 0; i < length; i++) {
//            noiseList.add(rand.nextInt(128));
//        }
//        return noiseList;
//    }

//    private static List<Integer> generateBigNoise(int length) {
//        List<Integer> noiseList = new ArrayList<>();
//        Random rand = new Random();
//        for (int i = 0; i < length; i++) {
//            int value = 0;
//            for (int j = 0; j < 7; j++) {
//                value += rand.nextInt(128);
//            }
//            noiseList.add(value);
//        }
//        return noiseList;
//    }

    private static List<Integer> convertToBinaryByAverage(List<Integer> noisyImageList, double average) {
        List<Integer> binaryImageList = new ArrayList<>();
        for (int value : noisyImageList) {
            if (value > average) {
                binaryImageList.add(1);
            } else {
                binaryImageList.add(0);
            }
        }
        return binaryImageList;
    }

    private static List<Integer> addNoise(List<Integer> bitList, List<Integer> noiseList, double coef) {
        List<Integer> noisyImageList = new ArrayList<>();
        for (int i = 0; i < bitList.size(); i++) {
            int noisyValue = (int) (bitList.get(i) + coef * noiseList.get(i));
            noisyImageList.add(noisyValue);
        }
        return noisyImageList;
    }


    private static List<Integer> readTxtFileToList(String fileName) {
        List<Integer> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            if (line != null) {
                String[] numbers = line.split(" ");
                for (String number : numbers) {
                    list.add(Integer.parseInt(number));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //    private static List<Integer> addNoiseAndConvertToBinary(List<Integer> bitList, List<Integer> noiseList, double coef) throws IOException {
//        List<Integer> noisyImageList = new ArrayList<>();
//        for (int i = 0; i < bitList.size(); i++) {
//            int noisyValue = (int) (bitList.get(i) + coef * noiseList.get(i));
//            noisyImageList.add(noisyValue);
//        }
//
//        double average = noisyImageList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
//
//        List<Integer> binaryImageList = convertToBinaryByAverage(noisyImageList, average);
//        return binaryImageList;
//    }
    private static void saveImage(List<Integer> bitList, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < bitList.size(); i += 8) {
            int byteValue = 0;
            for (int bit = 0; bit < 8; bit++) {
                if (i + bit < bitList.size() && bitList.get(i + bit) == 1) {
                    byteValue |= (1 << (7 - bit));
                }
            }
            baos.write(byteValue);
        }

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            baos.writeTo(fos);
        }
    }

    private static void generateNoiseTxt(int size, int maxValue, String fileName) {
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < size; i++) {
                int randomValue = rand.nextInt(maxValue + 1);
                writer.write(String.valueOf(randomValue));
                if (i < size - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
