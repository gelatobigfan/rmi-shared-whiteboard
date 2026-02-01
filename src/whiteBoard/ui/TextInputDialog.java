package whiteBoard.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Text input dialog component for getting user input text and font size
 * Supports custom title, default text and font size range
 */
public class TextInputDialog {
    // Component references
    private final Component parentComponent;
    private final JTextField textField;
    private final JSpinner fontSizeSpinner;

    // Data fields
    private String inputText;
    private int fontSize;
    private boolean confirmed;

    // Configuration parameters
    private String dialogTitle = "Input Text";
    private String textLabel = "Input Text";
    private String fontSizeLabel = "Select Font Size";
    private int minFontSize = 15;
    private int maxFontSize = 50;
    private int fontSizeStep = 5;

    /**
     * Constructor
     *
     * @param parentComponent Parent component, dialog will be centered relative to this component
     */
    public TextInputDialog(Component parentComponent) {
        this.parentComponent = parentComponent;
        this.fontSize = 30; // Default font size

        // Initialize components
        this.textField = new JTextField(10);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                fontSize, minFontSize, maxFontSize, fontSizeStep);
        this.fontSizeSpinner = new JSpinner(spinnerModel);
    }

    public String getText() {
        return textField.getText();
    }

    /**
     * Show dialog and wait for user input
     *
     * @return true if user confirms input, false otherwise
     */
    public boolean show() {
        // Create custom panel
        JPanel panel = createDialogPanel();

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                panel,
                dialogTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // Process result
        if (result == JOptionPane.OK_OPTION) {
            inputText = textField.getText();
            fontSize = (Integer) fontSizeSpinner.getValue();
            confirmed = true;
            return true;
        } else {
            confirmed = false;
            return false;
        }
    }

    /**
     * Create dialog panel
     *
     * @return Configured JPanel
     */
    private JPanel createDialogPanel() {
        // Create main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Text input area
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        JLabel textPrompt = new JLabel(textLabel);
        textPanel.add(textPrompt, BorderLayout.NORTH);
        textPanel.add(textField, BorderLayout.CENTER);
        panel.add(textPanel);

        // Add some spacing
        panel.add(Box.createVerticalStrut(10));

        // Font size selection area
        JPanel fontPanel = new JPanel(new BorderLayout(5, 5));
        JLabel fontPrompt = new JLabel(fontSizeLabel + " (Default " + fontSize + "):");
        fontPanel.add(fontPrompt, BorderLayout.NORTH);
        fontPanel.add(fontSizeSpinner, BorderLayout.CENTER);
        panel.add(fontPanel);

        return panel;
    }

    /**
     * Get user input text
     *
     * @return Text content, may be null if user cancels
     */
    public String getInputText() {
        return inputText;
    }

    /**
     * Get user selected font size
     *
     * @return Font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Check if user confirmed input
     *
     * @return true if user clicked OK button, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Set dialog title
     *
     * @param title Title text
     * @return this object for chaining
     */
    public TextInputDialog setDialogTitle(String title) {
        this.dialogTitle = title;
        return this;
    }

    /**
     * Set text input label
     *
     * @param label Label text
     * @return this object for chaining
     */
    public TextInputDialog setTextLabel(String label) {
        this.textLabel = label;
        return this;
    }

    /**
     * Set font size label
     *
     * @param label Label text
     * @return this object for chaining
     */
    public TextInputDialog setFontSizeLabel(String label) {
        this.fontSizeLabel = label;
        return this;
    }

    /**
     * Set default text
     *
     * @param text Default text
     * @return this object for chaining
     */
    public TextInputDialog setDefaultText(String text) {
        this.textField.setText(text);
        return this;
    }

    /**
     * Set default font size
     *
     * @param size Font size
     * @return this object for chaining
     */
    public TextInputDialog setDefaultFontSize(int size) {
        this.fontSize = size;
        this.fontSizeSpinner.setValue(size);
        return this;
    }

    /**
     * Set font size range
     *
     * @param min  Minimum font size
     * @param max  Maximum font size
     * @param step Adjustment step
     * @return this object for chaining
     */
    public TextInputDialog setFontSizeRange(int min, int max, int step) {
        this.minFontSize = min;
        this.maxFontSize = max;
        this.fontSizeStep = step;
        SpinnerNumberModel model = new SpinnerNumberModel(
                fontSize, minFontSize, maxFontSize, fontSizeStep);
        this.fontSizeSpinner.setModel(model);
        return this;
    }

    /**
     * Configure dialog text using resource bundle
     *
     * @param bundle Resource bundle
     * @return this object for chaining
     */
    public TextInputDialog configureFromResourceBundle(ResourceBundle bundle) {
        try {
            if (bundle.containsKey("text.dialog.title")) {
                this.dialogTitle = bundle.getString("text.dialog.title");
            }
            if (bundle.containsKey("text.dialog.input.label")) {
                this.textLabel = bundle.getString("text.dialog.input.label");
            }
            if (bundle.containsKey("text.dialog.fontsize.label")) {
                this.fontSizeLabel = bundle.getString("text.dialog.fontsize.label");
            }
        } catch (Exception e) {
            System.err.println("Error loading resource bundle: " + e.getMessage());
        }
        return this;
    }


    public void setVisible(boolean b) {
        parentComponent.setVisible(b);
    }
}
