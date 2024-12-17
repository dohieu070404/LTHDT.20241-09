package source;
import java.awt.*;
class CircuitElement {
    String name;
    String type; // R, L, hoặc C
    double value;
    double voltage;
    double current;
    Rectangle bounds; // Vị trí phần tử trên sơ đồ

    CircuitElement(String name, String type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.bounds = new Rectangle();
    }
}