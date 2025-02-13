import java.io.*;


public class Utility {

    public static Record processRecord(String line) {
        line = line.replace(",", ".");
        String[] parts = line.trim().split("\\s+");
        double probA = Double.parseDouble(parts[0]);
        double probB = Double.parseDouble(parts[1]);
        double probUnion = Double.parseDouble(parts[2]);
        return new Record(probA, probB, probUnion);
    }

    public static double calculateIntersection(Record record) {
        return record.getProbA() + record.getProbB() - record.getProbUnion();
    }


    public static int countLines(String filePath) throws IOException {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                lines++;
            }
        }
        return lines;
    }


    public static void displayFileContents(String filePath) throws IOException {
        System.out.println("Zawartość " + filePath + ":");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.lines().forEach(System.out::println);
        }
    }


    public static void addIntersectionProbability(String inputFilePath, String outputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .forEach(line -> {
                        try {
                            Record record = processRecord(line);
                            double probIntersection = calculateIntersection(record);
                            writer.write(record.getProbA() + " " + record.getProbB() + " " + record.getProbUnion() + " " + probIntersection);
                            writer.newLine();
                        } catch (IOException e) {
                            System.err.println("Problem z zapisem : " + e.getMessage());
                        }
                    });
        }
    }
}
