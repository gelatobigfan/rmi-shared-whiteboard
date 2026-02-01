package utils;

import javax.swing.*;

public class Config {
    public static final int GUI_WIDTH = 800;
    public static final int GUI_HEIGHT = 600;

    public static final float DEFAULT_STROKE = 3.0f;

    public static final String LINE = "Line";
    public static final String CIRCLE = "Circle";
    public static final String OVAL = "Oval";
    public static final String RECTANGLE = "Rectangle";
    public static final String DRAW = "Draw";
    public static final String ERASER = "Eraser";
    public static final String TEXT = "Text";

    public static void PaneRMIError() {
        JOptionPane.showMessageDialog(null, "RMI Connect Fail", "Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    public static void PaneIOError() {
        JOptionPane.showMessageDialog(null, "IO Error", "Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }
}