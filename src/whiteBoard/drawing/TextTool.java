package whiteBoard.drawing;

import utils.Config;

import java.awt.*;

public class TextTool implements DrawingTool {
    private String text;
    private int fontSize;

    public TextTool() {
        this.text = "";
        this.fontSize = 30;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize) {
        if (text == null || text.isEmpty()) {
            return;
        }

        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g2d.drawString(text, startPoint.x, startPoint.y);
    }

    @Override
    public String getToolType() {
        return Config.TEXT;
    }
}
