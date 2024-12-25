package source;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CircuitCalculator extends JFrame {
    private JComboBox<String> sourceTypeComboBox, circuitTypeComboBox, elementComboBox;
    private JTextField voltageField, frequencyField, elementValueField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton addElementButton, calculateButton, clearButton, addInductor, addResistor, addCapacitor;
    private ArrayList<CircuitElement> elements = new ArrayList<>();
    private int elementCount = 0;
    private DiagramRenderer diagramPanel;

    public CircuitCalculator() {
        setTitle("Circuit Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for user inputs
        JPanel inputPanel = new JPanel(new GridLayout(9, 2));
        sourceTypeComboBox = new JComboBox<>(new String[]{"Chọn loại nguồn", "DC", "AC"});
        circuitTypeComboBox = new JComboBox<>(new String[]{"Chọn loại mạch", "Nối tiếp", "Song song"});
//        elementComboBox = new JComboBox<>(new String[]{"R", "L", "C"});

        voltageField = new JTextField();
        frequencyField = new JTextField();
        frequencyField.setEnabled(false);
        elementValueField = new JTextField();

//        addElementButton = new JButton("Thêm phần tử");
        addInductor = new JButton("Thêm cuộn cảm");
        addResistor = new JButton("Thêm điện trở");
        addCapacitor = new JButton("Thêm tụ điện");
        
        calculateButton = new JButton("Tính toán");
        clearButton = new JButton("Xóa tất cả");

        // Table setup
        String[] columnNames = {"Phần tử", "U (Điện áp)", "I (Dòng điện)", "R (Điện trở)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);

        // Diagram Renderer Panel
        diagramPanel = new DiagramRenderer();
        diagramPanel.setPreferredSize(new Dimension(400, 400));

        // Add components to input panel
        inputPanel.add(new JLabel("Loại nguồn điện:"));
        inputPanel.add(sourceTypeComboBox);
        inputPanel.add(new JLabel("Giá trị điện áp (V):"));
        inputPanel.add(voltageField);
        inputPanel.add(new JLabel("Tần số (Hz):"));
        inputPanel.add(frequencyField);
        inputPanel.add(new JLabel("Loại mạch:"));
        inputPanel.add(circuitTypeComboBox);
//        inputPanel.add(new JLabel("Loại phần tử:"));
//        inputPanel.add(elementComboBox);
//        inputPanel.add(new JLabel("Giá trị phần tử (Đơn vị \u03A9, H hoặc F):"));
//        inputPanel.add(elementValueField);
        
        inputPanel.add(addInductor);
        inputPanel.add(addResistor);
        inputPanel.add(addCapacitor);
        
        inputPanel.add(calculateButton);
        inputPanel.add(new JLabel());
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);

        // Event handling
        sourceTypeComboBox.addActionListener(e -> {
            boolean isAC = sourceTypeComboBox.getSelectedItem().equals("AC");
            frequencyField.setEnabled(isAC);
        });

        addResistor.addActionListener(e -> addElement("R"));
        addInductor.addActionListener(e -> addElement("L"));
        addCapacitor.addActionListener(e -> addElement("C"));
        
        calculateButton.addActionListener(e -> showCalculationWindow());
        clearButton.addActionListener(e -> clearElements());
    }

    // Tạo cửa sổ hiển thị kết quả tính toán
    private void showCalculationWindow() {
        if (elements.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy thêm ít nhất một phần tử.");
            return;
        }

        JFrame calculationWindow = new JFrame("Kết quả tính toán");
        calculationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        calculationWindow.setSize(800, 800);
        calculationWindow.setLayout(new BorderLayout());

        // ScrollPane cho bảng và sơ đồ
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        JScrollPane diagramScrollPane = new JScrollPane(diagramPanel);

        calculationWindow.add(tableScrollPane, BorderLayout.CENTER);
        calculationWindow.add(diagramScrollPane, BorderLayout.SOUTH);

        calculationWindow.setVisible(true);

        // Tiến hành tính toán mạch
        calculateCircuit();
    }
    // Phương thức thêm phần tử mạch
    private void addElement(String type) {
        if (elementCount >= 5) {
            JOptionPane.showMessageDialog(this, "Chỉ được thêm tối đa 5 phần tử.");
            return;
        }

//        String type = elementComboBox.getSelectedItem().toString();
        String name = type + (elementCount + 1);
        double value;
        try {
            value = Double.parseDouble(elementValueField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá trị phần tử không hợp lệ.");
            return;
        }

        elements.add(new CircuitElement(name, type, value));
        JOptionPane.showMessageDialog(this, "Đã thêm phần tử: " + name);
        String message = String.format("Đã thêm phần tử: %s (Loại: %s, Giá trị: %.2f)", name, type, value);
        JOptionPane.showMessageDialog(null, message, "Thông tin phần tử", JOptionPane.INFORMATION_MESSAGE);
        elementCount++;
        diagramPanel.setElements(elements, circuitTypeComboBox.getSelectedItem().toString());
    }
    // Phương thức tính toán mạch
    private void calculateCircuit() {
        if (elements.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy thêm ít nhất một phần tử.");
            return;
        }

        String sourceType = sourceTypeComboBox.getSelectedItem().toString();
        String circuitType = circuitTypeComboBox.getSelectedItem().toString();
        double voltage;
        double frequency = 0;
        
        try {
            voltage = Double.parseDouble(voltageField.getText());
            if (sourceType.equals("AC")) {
                frequency = Double.parseDouble(frequencyField.getText());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điện áp hoặc tần số không hợp lệ.");
            return;
        }
        diagramPanel.source(sourceTypeComboBox.getSelectedItem().toString(), voltage, frequency);
        tableModel.setRowCount(0); // Clear previous results
        if (sourceType.equals("DC")) {
            calculateDCCircuit(voltage, circuitType);
        } else {
            calculateACCircuit(voltage, frequency, circuitType);
        }
    }
    
    // Đối với mạch điện 1 chiều
    private void calculateDCCircuit(double voltage, String circuitType) {
        double totalResistance = 0;
        boolean hasCapacitor = false;
        boolean hasInductor = false;

        // Kiểm tra phần tử trong mạch và tính tổng điện trở
        for (CircuitElement e : elements) {
            if (e.type.equals("R")) {
                totalResistance += e.value;
            } else if (e.type.equals("C")) {
                hasCapacitor = true; // Phát hiện tụ điện trong mạch
            } else if (e.type.equals("L")) {
                hasInductor = true; // Phát hiện cuộn cảm trong mạch
            }
        }

        // Trường hợp mạch song song
        if (circuitType.equals("Song song")) {
            if (hasInductor) {
                JOptionPane.showMessageDialog(this, "Ngắn mạch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (totalResistance == 0) {
                JOptionPane.showMessageDialog(this, "Ngắn mạch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                for (CircuitElement e : elements) {
                    if (e.type.equals("C")) {
                        // Tính U và I cho tụ điện trong mạch song song DC
                        double current = 0;
                        tableModel.addRow(new Object[]{e.name, voltage, current, e.value});
                    } else if (e.type.equals("R")) {
                        double current = voltage / e.value;
                        tableModel.addRow(new Object[]{e.name, voltage, current, e.value});
                    }
                }
                return;
            }
        }     
        if (circuitType.equals("Nối tiếp")) {
            if(hasCapacitor) {
                for (CircuitElement e : elements) {
                    tableModel.addRow(new Object[]{e.name, voltage, 0.0, e.value});
                    }
                return;
            } else if (totalResistance == 0) {
                JOptionPane.showMessageDialog(this, "Ngắn mạch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                double current = voltage / totalResistance; // Dòng điện tổng
	            for (CircuitElement e : elements) {
	                double voltageDrop = 0.0;
	
	                if (e.type.equals("R")) {
	                    voltageDrop = current * e.value; // Điện áp rơi trên điện trở
	                    tableModel.addRow(new Object[]{e.name, voltageDrop, current, e.value});
	                } else if (e.type.equals("L")) {
	                    // Cuộn cảm trong mạch DC nối tiếp: U và I bằng 0
	                    tableModel.addRow(new Object[]{e.name, 0.0, current, e.value});
	                }
	            }
            }
        }
    }

    // Đối với mạch điện 2 chiều
    private void calculateACCircuit(double voltage, double frequency, String circuitType) {
        double omega = 2 * Math.PI * frequency;
        double totalResistance = 0, totalReactance = 0;

        // Kiểm tra nếu tần số = 0
        if (frequency == 0) {
            JOptionPane.showMessageDialog(this, "Tần số bằng 0, mạch AC trở thành mạch DC.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (CircuitElement e : elements) {
            if (e.type.equals("R")) totalResistance += e.value;
            else if (e.type.equals("L")) totalReactance += omega * e.value;
            else if (e.type.equals("C")) totalReactance -= 1 / (omega * e.value);
        }

        // Kiểm tra cộng hưởng và ngắn mạch
        double impedance = Math.sqrt(totalResistance * totalResistance + totalReactance * totalReactance);

        if (impedance == 0) {
            JOptionPane.showMessageDialog(this, "Mạch AC ngắn mạch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tính dòng điện trong mạch
        double current = voltage / impedance;

        for (CircuitElement e : elements) {
            double voltageDrop = 0;
            if (e.type.equals("R")) voltageDrop = current * e.value;
            else if (e.type.equals("L")) voltageDrop = current * omega * e.value;
            else if (e.type.equals("C")) voltageDrop = current * (1 / (omega * e.value));

            tableModel.addRow(new Object[]{e.name, voltageDrop, current, e.value});
        }
    }
    // Xóa các phần tử
    private void clearElements() {
        elements.clear();
        elementCount = 0;
        tableModel.setRowCount(0);
        diagramPanel.resetDiagram();
        JOptionPane.showMessageDialog(this, "Đã xóa tất cả các phần tử.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CircuitCalculator gui = new CircuitCalculator();
            gui.setVisible(true);
        });
    }
}