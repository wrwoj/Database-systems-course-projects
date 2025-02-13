import java.io.*;
import java.util.*;

public class LargeBufferSort {

    public static final int BUFFER_SIZE = 250;  // (b)
    public static final int NUM_BUFFERS = 20;   //  (n)
    private static final int BLOCK_SIZE = BUFFER_SIZE * NUM_BUFFERS;
    //liczniki
    private static int tmpFileCounter = 0;
    private static int phasesCounter = 0;
    private static int readOperationsCounter = 0;
    private static int writeOperationsCounter = 0;
    private static int mergeOperationsCounter = 0;

    private static boolean displayIntermediateFiles = false;
    private static boolean saveIntermediateFiles = false;

    public static void setDisplayIntermediateFiles(boolean value) {
        displayIntermediateFiles = value;
    }

    public static void setSaveIntermediateFiles(boolean value) {
        saveIntermediateFiles = value;
    }


    public static void sort(String inputFilePath, int recordCount) throws IOException {
        tmpFileCounter = 0;
        phasesCounter = 0;
        readOperationsCounter = 0;
        writeOperationsCounter = 0;
        mergeOperationsCounter = 0;

        String outputFilePath = "data/input_" + recordCount + "_sorted.txt";
        String tmpFilesPath = "data/tmp/tmp_file";
        List<String> tmpFilesPaths = new ArrayList<>();

        File tmpDir = new File("data/tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        int phaseNumber = 1;

        // Satge 1
        try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFilePath))) {
            while (inputReader.ready()) {
                List<Record> buffer = new ArrayList<>();
                for (int i = 0; i < BLOCK_SIZE && inputReader.ready(); i++) {
                    String line = inputReader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        Record record = Utility.processRecord(line);
                        buffer.add(record);
                    }
                }
                if (buffer.isEmpty()) break;

                buffer.sort(Comparator.comparingDouble(Utility::calculateIntersection));

                String tmpFilePath = tmpFilesPath + tmpFileCounter++ + ".txt";
                tmpFilesPaths.add(tmpFilePath);

                try (BufferedWriter tmpWriter = new BufferedWriter(new FileWriter(tmpFilePath))) {
                    for (Record record : buffer) {
                        tmpWriter.write(record.toString());
                        tmpWriter.newLine();
                    }
                }
                writeOperationsCounter++;
            }
        }

        // Stage 2
        mergeRuns(tmpFilesPaths, outputFilePath, phaseNumber);

        displayOperationCounts(recordCount);
    }

    private static void mergeRuns(List<String> tmpFilesPaths, String outputFile,  int phaseNumber) throws IOException {
        List<String> currentRuns = new ArrayList<>(tmpFilesPaths);

        while (currentRuns.size() > 1) {
            List<String> newRuns = new ArrayList<>();
            int i = 0;

            while (i < currentRuns.size()) {
                int end = Math.min(i + NUM_BUFFERS - 1, currentRuns.size());
                List<String> mergeGroupPaths = currentRuns.subList(i, end);

                List<BufferedReader> mergeGroup = new ArrayList<>();
                for (String path : mergeGroupPaths) {
                    mergeGroup.add(new BufferedReader(new FileReader(path)));
                }

                String newTmpPath = mergeBuffers(mergeGroup, phaseNumber);
                newRuns.add(newTmpPath);

                for (BufferedReader reader : mergeGroup) {
                    reader.close();
                }
                for (String path : mergeGroupPaths) {
                    new File(path).delete();
                }

                i = end;
            }

            currentRuns = newRuns;

            phasesCounter++;
            phaseNumber++;
        }

        if (!currentRuns.isEmpty()) {
            try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
                 BufferedReader finalReader = new BufferedReader(new FileReader(currentRuns.get(0)))) {

                List<Record> finalData;
                while (!(finalData = blockRead(finalReader, BLOCK_SIZE)).isEmpty()) {
                    for (Record record : finalData) {
                        String recordString = record.toString();
                        outputWriter.write(recordString);
                        outputWriter.newLine();

                    }
                    writeOperationsCounter += 1;
                }
            }
            new File(currentRuns.get(0)).delete();
        }
    }


    private static String mergeBuffers(List<BufferedReader> bufferFiles, int phaseNumber) throws IOException {
        String outputPath = "data/tmp/tmp_file" + tmpFileCounter++ + ".txt";

        try (BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputPath))) {
            PriorityQueue<MergeRecord> minHeap = new PriorityQueue<>();
            Queue<Record>[] buffers = new Queue[bufferFiles.size()];

            // Initialize buffers and heap
            for (int i = 0; i < bufferFiles.size(); i++) {
                buffers[i] = new LinkedList<>(blockRead(bufferFiles.get(i), BUFFER_SIZE));
                if (!buffers[i].isEmpty()) {
                    minHeap.add(new MergeRecord(buffers[i].poll(), i));
                }
            }

            List<Record> outputBuffer = new ArrayList<>();

            while (!minHeap.isEmpty()) {
                MergeRecord top = minHeap.poll();
                outputBuffer.add(top.getRecord());
                int bufferIndex = top.getFileIndex();

                if (outputBuffer.size() == BUFFER_SIZE) {
                    writeOutputBuffer(outputFile, outputBuffer, phaseNumber);
                }

                if (!buffers[bufferIndex].isEmpty()) {
                    minHeap.add(new MergeRecord(buffers[bufferIndex].poll(), bufferIndex));
                } else if (bufferFiles.get(bufferIndex).ready()) {
                    buffers[bufferIndex] = new LinkedList<>(blockRead(bufferFiles.get(bufferIndex), BUFFER_SIZE));
                    if (!buffers[bufferIndex].isEmpty()) {
                        minHeap.add(new MergeRecord(buffers[bufferIndex].poll(), bufferIndex));
                    }
                }
            }

            if (!outputBuffer.isEmpty()) {
                writeOutputBuffer(outputFile, outputBuffer, phaseNumber);
            }

            mergeOperationsCounter++;
            return outputPath;
        }
    }


    private static List<Record> blockRead(BufferedReader reader, int blockSize) throws IOException {
        List<Record> records = new ArrayList<>();
        String line;
        while (records.size() < blockSize && (line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                records.add(Utility.processRecord(line));
            }
        }
        readOperationsCounter++;
        return records;
    }


    private static void writeOutputBuffer(BufferedWriter writer, List<Record> outputBuffer, int phaseNumber) throws IOException {
        for (Record record : outputBuffer) {
            writer.write(record.toString());
            writer.newLine();
        }
        outputBuffer.clear();
        writeOperationsCounter++;
    }


    private static void displayOperationCounts(int recordCount) {
        double expectedOperations = calculateExpectedDriveOperations(recordCount, BUFFER_SIZE, NUM_BUFFERS);
        int expectedPhases = calculateExpectedPhases(recordCount, BUFFER_SIZE, NUM_BUFFERS);
        System.out.println("Liczba wykonanych operacji:");
        System.out.println("Liczba faz: " + phasesCounter);
        System.out.println("Oczekiwana liczba faz: " + expectedPhases);
        System.out.println("Liczba odczytów z dysku: " + readOperationsCounter);
        System.out.println("Liczba zapisów na dysk: " + writeOperationsCounter);
        System.out.println("Liczba operacji scalania: " + mergeOperationsCounter);
        System.out.println("Laczna liczba operacji dyskowych: " + (readOperationsCounter + writeOperationsCounter));
        System.out.println("Oczekiwana liczba operacji dyskowych: " + expectedOperations);
    }
    private static double calculateExpectedDriveOperations(int N, int b, int n) {
        double log_n = Math.log(n) / Math.log(2);           // lg(n)
        double log_N_over_b = Math.log((double) N / b) / Math.log(2); // lg(N/b)

        return 2 * ((double) N / (b * log_n)) * log_N_over_b;
    }


    public static int getPhasesCounter() {
        return phasesCounter;
    }

    public static int getReadOperationsCounter() {
        return readOperationsCounter;
    }

    public static int getWriteOperationsCounter() {
        return writeOperationsCounter;
    }

    public static int calculateExpectedPhases(int N, int b, int n) {
        double logBaseN = Math.log(N / (double) b) / Math.log(n); // log_n(N / b)
        return (int) Math.ceil(logBaseN) - 1;
    }
}
