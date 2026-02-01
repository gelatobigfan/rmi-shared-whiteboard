package whiteBoard.ui;

import utils.Config;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JPanel {
    private final JPanel toolBar;
    private final JPanel colorBar;
    private final JPanel eraserSizePanel;
    private final JLabel currentTool;
    private final JLabel currentColor;
    private final JLabel colorPreview;  // ✅ 颜色预览小方块
    private String toolType;
    private Color colorType;
    private float eraserSize = 50.00f;
    public static final Color BROWN = new Color(139, 69, 19);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color NAVY = new Color(0, 0, 128);


    public ToolBar() {
        toolType = Config.LINE;
        colorType = Color.BLACK;
        toolBar = new JPanel();
        colorBar = new JPanel();
        eraserSizePanel = new JPanel();
        currentTool = new JLabel("Current type: " + toolType);
        currentColor = new JLabel("Current color: " + colorToString(colorType));
        colorPreview = new JLabel("     ");  // 小方块
        colorPreview.setOpaque(true);
        colorPreview.setBackground(colorType);
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        this.setLayout(new BorderLayout());

        toolBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        String[] toolOptions = {
                Config.LINE,
                Config.OVAL,
                Config.RECTANGLE,
                Config.DRAW,
                Config.ERASER,
                Config.TEXT
        };

        JComboBox<String> toolComboBox = new JComboBox<>(toolOptions);
        toolComboBox.setSelectedItem(toolType);
        toolComboBox.addActionListener(e -> {
            toolType = (String) toolComboBox.getSelectedItem();
            currentTool.setText("Current tool: " + toolType);
        });

        JLabel toolSelectLabel = new JLabel("Select tool: ");
        toolBar.add(toolSelectLabel);
        toolBar.add(toolComboBox);

        this.add(toolBar, BorderLayout.WEST);

        JComboBox<String> colorComboBox = new JComboBox<>();
        colorComboBox.addItem("WHITE");
        colorComboBox.addItem("LIGHT_GRAY");
        colorComboBox.addItem("GRAY");
        colorComboBox.addItem("DARK_GRAY");
        colorComboBox.addItem("BLACK");
        colorComboBox.addItem("RED");
        colorComboBox.addItem("PINK");
        colorComboBox.addItem("ORANGE");
        colorComboBox.addItem("YELLOW");
        colorComboBox.addItem("BROWN");
        colorComboBox.addItem("PURPLE");
        colorComboBox.addItem("NAVY");
        colorComboBox.addItem("GREEN");
        colorComboBox.addItem("MAGENTA");
        colorComboBox.addItem("CYAN");
        colorComboBox.addItem("BLUE");

        colorComboBox.setRenderer(new ColorComboBoxRenderer());
        colorComboBox.setSelectedItem("BLACK");
        colorComboBox.addActionListener(e -> {
            String selectedColor = (String) colorComboBox.getSelectedItem();
            switch (selectedColor) {
                case "WHITE":
                    colorType = Color.WHITE;
                    break;
                case "LIGHT_GRAY":
                    colorType = Color.LIGHT_GRAY;
                    break;
                case "GRAY":
                    colorType = Color.GRAY;
                    break;
                case "DARK_GRAY":
                    colorType = Color.DARK_GRAY;
                    break;
                case "BLACK":
                    colorType = Color.BLACK;
                    break;
                case "RED":
                    colorType = Color.RED;
                    break;
                case "PINK":
                    colorType = Color.PINK;
                    break;
                case "ORANGE":
                    colorType = Color.ORANGE;
                    break;
                case "YELLOW":
                    colorType = Color.YELLOW;
                    break;
                case "BROWN":
                    colorType = BROWN;
                    break;
                case "PURPLE":
                    colorType = PURPLE;
                    break;
                case "NAVY":
                    colorType = NAVY;
                    break;
                case "GREEN":
                    colorType = Color.GREEN;
                    break;
                case "MAGENTA":
                    colorType = Color.MAGENTA;
                    break;
                case "CYAN":
                    colorType = Color.CYAN;
                    break;
                case "BLUE":
                    colorType = Color.BLUE;
                    break;
            }
            currentColor.setText("color : " + selectedColor);
        });

        colorBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel colorSelectLabel = new JLabel("Select color: ");
        colorBar.add(colorSelectLabel);
        colorBar.add(colorComboBox);
        this.add(colorBar, BorderLayout.EAST);

        JButton customColorButton = new JButton("Customize Color");
        customColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(null, "Choose a Custom Color", colorType);
            if (chosenColor != null) {
                colorType = chosenColor;
                currentColor.setText("color : " + colorToString(colorType));
                colorPreview.setBackground(colorType);
            }
        });
        colorBar.add(customColorButton);


        eraserSizePanel.setLayout(new BoxLayout(eraserSizePanel, BoxLayout.Y_AXIS));
        JLabel sizeLabel = new JLabel("erase size");
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sizeValueLabel = new JLabel(String.valueOf(eraserSize));
        sizeValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        eraserSizePanel.add(sizeLabel);

        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 150, (int) eraserSize);
        sizeSlider.setMajorTickSpacing(20);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.addChangeListener(e -> {
            eraserSize = sizeSlider.getValue();
            sizeValueLabel.setText(String.valueOf(eraserSize));
        });

        eraserSizePanel.add(sizeSlider);
        eraserSizePanel.add(sizeValueLabel);
        this.add(eraserSizePanel, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // ✅ 添加边距
        northPanel.setPreferredSize(new Dimension(100, 30)); // ✅ 添加高度，防止被挤掉
        currentTool.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentColor.setAlignmentX(Component.RIGHT_ALIGNMENT);
        northPanel.add(currentTool);
        northPanel.add(Box.createHorizontalGlue());
        northPanel.add(currentColor);
        northPanel.add(Box.createRigidArea(new Dimension(10, 0)));  // 加一点间隔
        northPanel.add(colorPreview);  // ✅ 添加颜色预览

        this.add(northPanel, BorderLayout.NORTH);

    }

    public String getToolType() {
        return toolType;
    }

    public Color getColor() {
        return colorType;
    }

    public float getEraserSize() {
        return eraserSize;
    }

    public String colorToString(Color color) {
        if (color.equals(Color.WHITE)) {
            return "WHITE";
        } else if (color.equals(Color.LIGHT_GRAY)) {
            return "LIGHT_GRAY";
        } else if (color.equals(Color.GRAY)) {
            return "GRAY";
        } else if (color.equals(Color.DARK_GRAY)) {
            return "DARK_GRAY";
        } else if (color.equals(Color.BLACK)) {
            return "BLACK";
        } else if (color.equals(Color.RED)) {
            return "RED";
        } else if (color.equals(Color.PINK)) {
            return "PINK";
        } else if (color.equals(Color.ORANGE)) {
            return "ORANGE";
        } else if (color.equals(Color.YELLOW)) {
            return "YELLOW";
        } else if (color.equals(BROWN)) {
            return "BROWN";
        } else if (color.equals(PURPLE)) {
            return "PURPLE";
        } else if (color.equals(NAVY)) {
            return "NAVY";
        } else if (color.equals(Color.GREEN)) {
            return "GREEN";
        } else if (color.equals(Color.MAGENTA)) {
            return "MAGENTA";
        } else if (color.equals(Color.CYAN)) {
            return "CYAN";
        } else if (color.equals(Color.BLUE)) {
            return "BLUE";
        }
        return "";
    }

    private class ColorComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof String) {
                String colorName = (String) value;
                Color backgroundColor = null;
                switch (colorName) {
                    case "WHITE":
                        backgroundColor = Color.WHITE;
                        break;
                    case "LIGHT_GRAY":
                        backgroundColor = Color.LIGHT_GRAY;
                        break;
                    case "GRAY":
                        backgroundColor = Color.GRAY;
                        break;
                    case "DARK_GRAY":
                        backgroundColor = Color.DARK_GRAY;
                        break;
                    case "BLACK":
                        backgroundColor = Color.BLACK;
                        break;
                    case "RED":
                        backgroundColor = Color.RED;
                        break;
                    case "PINK":
                        backgroundColor = Color.PINK;
                        break;
                    case "ORANGE":
                        backgroundColor = Color.ORANGE;
                        break;
                    case "YELLOW":
                        backgroundColor = Color.YELLOW;
                        break;
                    case "BROWN":
                        backgroundColor = BROWN;
                        break;
                    case "PURPLE":
                        backgroundColor = PURPLE;
                        break;
                    case "NAVY":
                        backgroundColor = NAVY;
                        break;
                    case "GREEN":
                        backgroundColor = Color.GREEN;
                        break;
                    case "MAGENTA":
                        backgroundColor = Color.MAGENTA;
                        break;
                    case "CYAN":
                        backgroundColor = Color.CYAN;
                        break;
                    case "BLUE":
                        backgroundColor = Color.BLUE;
                        break;
                }

                if (backgroundColor != null) {
                    setBackground(backgroundColor);
                    // For dark backgrounds, use white text
                    if (backgroundColor.equals(Color.BLACK) ||
                            backgroundColor.equals(Color.BLUE) ||
                            backgroundColor.equals(Color.DARK_GRAY) ||
                            backgroundColor.equals(Color.MAGENTA)) {
                        setForeground(Color.WHITE);
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
            }
            return component;
        }
    }
}


