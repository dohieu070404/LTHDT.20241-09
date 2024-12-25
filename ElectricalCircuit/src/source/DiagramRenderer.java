package source;
import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
class DiagramRenderer extends JPanel {
    private ArrayList<CircuitElement> elements = new ArrayList<>();
    private String circuitType = "Nối tiếp";
    private boolean showDiagram = false;

    public void setElements(ArrayList<CircuitElement> elements, String circuitType) {
        this.elements = elements;
        this.circuitType = circuitType;
        showDiagram = true;
        repaint();
    }
    
    public void resetDiagram() {
        elements.clear();
        showDiagram = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        int x = 50, y = 100;
        
        if (circuitType.equals("Nối tiếp")) {
            // Draw series circuit
            for (CircuitElement element : elements) {
                g2.drawLine(x, y, x + 50, y); // Draw connecting wire
                x += 50;
                drawElement(g2, element, x, y);
                element.bounds = new Rectangle(x, y - 10, 40, 20);
                x += 50;
            }
            g2.drawLine(x, y, x + 50, y); // Final wire
        } else {
            // Draw parallel circuit
        	int startX = x;
            for (CircuitElement element : elements) {
            	g2.drawLine(startX, y, startX + 50, y); // Dây nối ngang
                drawElement(g2, element, startX + 50, y);
                element.bounds = new Rectangle(startX + 50, y - 10, 40, 20);
                g2.drawLine(startX + 90, y, startX + 140, y); // Dây đi ra
                y += 50;
            }
            g2.drawLine(startX, 100, startX, y - 50);
            g2.drawLine(startX + 140, 100, startX + 140, y - 50);
        }
    }

    private void drawElement(Graphics2D g2, CircuitElement element, int x, int y) {
        switch (element.type) {
            case "R":
                g2.drawRect(x, y - 10, 40, 20); // Resistor
                g2.drawString(element.name + " " + element.value + " \u03A9", x + 10, y - 15);
                break;
            case "C":
                g2.drawLine(x + 10, y - 10, x + 10, y + 10); // Capacitor plates
                g2.drawLine(x + 30, y - 10, x + 30, y + 10);
                g2.drawString(element.name + " " + element.value + " F", x + 10, y - 15);

                break;
            case "L":
                g2.drawArc(x, y - 10, 20, 20, 0, 180); // Inductor arcs
                g2.drawArc(x + 20, y - 10, 20, 20, 0, 180);
                g2.drawString(element.name + " " + element.value + " H", x + 10, y - 15);

                break;
        }
    }
}
