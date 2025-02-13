
public class Record {
    private final double probA;
    private final double probB;
    private final double probUnion;

    public Record(double probA, double probB, double probUnion) {
        this.probA = probA;
        this.probB = probB;
        this.probUnion = probUnion;
    }

    public double getProbA() {
        return probA;
    }

    public double getProbB() {
        return probB;
    }

    public double getProbUnion() {
        return probUnion;
    }

    @Override
    public String toString() {
        return probA + " " + probB + " " + probUnion;
    }
}
