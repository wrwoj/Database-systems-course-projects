import java.io.*;
import java.util.Random;
import java.util.Scanner;


public class DataGenerator {

    private static final Scanner scanner = new Scanner(System.in);
    public static void generateRandomRecords(int recordCount) throws IOException {
        Random rand = new Random();
        String filePath = "data/input_" + recordCount + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < recordCount; i++) {
                double probA = Math.max(rand.nextDouble(), 1e-5);
                double probB = Math.max(rand.nextDouble(), 1e-5);

                // Ograniczyłem rozmiar obszaru wspólnego
                double minProbUnion = Math.max(probA, probB);
                double maxProbUnion = Math.min(1.0, probA + probB - 1e-5);

                if (maxProbUnion < minProbUnion) {
                    probB = Math.min(probB, 1.0 - probA + 1e-5);
                    minProbUnion = Math.max(probA, probB);
                    maxProbUnion = Math.min(1.0, probA + probB - 1e-5);
                }
                if (maxProbUnion < minProbUnion) {
                    probA = Math.min(probA, 1.0 - probB + 1e-5);
                    minProbUnion = Math.max(probA, probB);
                    maxProbUnion = Math.min(1.0, probA + probB - 1e-5);
                }
                if (maxProbUnion < minProbUnion) {
                    maxProbUnion = minProbUnion;
                }

                double probUnion = minProbUnion + rand.nextDouble() * (maxProbUnion - minProbUnion);

                writer.write(String.format("%.8f %.8f %.8f", probA, probB, probUnion));
                writer.newLine();
            }
        }
        System.out.println("Zapisano w  " + filePath);
    }


    public static void readRecordsFromKeyboard(int recordCount) throws IOException {
        String filePath = "data/input_" + recordCount + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            System.out.println("Wpisz  " + recordCount + " rekordów w schemacie (probA probB probUnion(A,B)):");
            for (int i = 0; i < recordCount; i++) {
                String line = scanner.nextLine();
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("Zapisano w " + filePath);
    }
}
