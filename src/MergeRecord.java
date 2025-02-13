
public class MergeRecord implements Comparable<MergeRecord> {
    private final Record record;
    private final int fileIndex;

    public MergeRecord(Record record, int fileIndex) {
        this.record = record;
        this.fileIndex = fileIndex;
    }

    public Record getRecord() {
        return record;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    @Override
    public int compareTo(MergeRecord other) {
        double intersectionA = Utility.calculateIntersection(this.record);
        double intersectionB = Utility.calculateIntersection(other.record);
        return Double.compare(intersectionA, intersectionB);
    }
}
