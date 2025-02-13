import java.io.*;
import java.util.Scanner;
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            System.out.println("Wybierz::");
            System.out.println("1. Generowanie losowe rekordów");
            System.out.println("2. Wprowadzenie rekordow z klawiatury");
            System.out.println("3. Zaladowanie rekordow z pliku");
            int choice = Integer.parseInt(scanner.nextLine());

            String inputFilePath;
            int recordCount = 0;

            switch (choice) {
                case 1:
                    System.out.println("Liczba rekordow:");
                    recordCount = Integer.parseInt(scanner.nextLine());
                    DataGenerator.generateRandomRecords(recordCount);
                    inputFilePath = "data/input_" + recordCount + ".txt";
                    break;
                case 2:
                    System.out.println("Liczba rekordow:");
                    recordCount = Integer.parseInt(scanner.nextLine());
                    DataGenerator.readRecordsFromKeyboard(recordCount);
                    inputFilePath = "data/input_" + recordCount + ".txt";
                    break;
                case 3:
                    System.out.println("Sciezka do pliku:");
                    inputFilePath = scanner.nextLine();
                    recordCount = Utility.countLines(inputFilePath);
                    break;
                default:
                    System.out.println("Zly input.");
                    scanner.close();
                    return;
            }
            System.out.println("Czy chcesz sprawdzic zawartosc pliku wejsciowego? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                Utility.displayFileContents(inputFilePath);
            }

            System.out.println("Wybierz:");
            System.out.println("1. Wyswietl zawartosc pliku po kazdej fazie");
            System.out.println("2. Zapisz zawartosc pliku po kazdej fazie do osobnego pliku");
            System.out.println("3. Zadno z powyzszych");
            String intermediateOption = scanner.nextLine();
            if ("1".equals(intermediateOption)) {
                LargeBufferSort.setDisplayIntermediateFiles(true);
            } else if ("2".equals(intermediateOption)) {
                LargeBufferSort.setSaveIntermediateFiles(true);
            }

            LargeBufferSort.sort(inputFilePath, recordCount);

            System.out.println("Czy chcesz sprawdzic zawartosc pliku wyjsciowego? (y/n)");
            String sortedFilePath = "data/input_" + recordCount + "_sorted.txt";
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                Utility.displayFileContents(sortedFilePath);
            }

            System.out.println("Czy wygenerowac plik wyjsciowy z dodanym praw. iloczynu w kazdej linii? (y/n)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                String outputFilePath = "data/input_" + recordCount + "_sorted_with_intersection.txt";
                Utility.addIntersectionProbability(sortedFilePath, outputFilePath);
                System.out.println("Zapisano w  " + outputFilePath);

                System.out.println("Czy wyswietlic? (y/n)");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    Utility.displayFileContents(outputFilePath);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
