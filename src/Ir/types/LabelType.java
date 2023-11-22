package Ir.types;

public class LabelType implements IRType {
    private int label;
    private static int LABEL = 0;

    public LabelType() {
        label = LABEL++;
    }

    @Override
    public String toString() {
        return "label_" + label;
    }
}
