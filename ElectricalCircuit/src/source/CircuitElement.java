package source;

class CircuitElement {
    String name;
    String type;
    double value;
    double voltage;
    double current;

    public CircuitElement(String name, String type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}