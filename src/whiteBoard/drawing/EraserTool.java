package whiteBoard.drawing;

import utils.Config;

import java.awt.*;

public class EraserTool implements DrawingTool {
    @Override
    public void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize) {
        Color originalColor = g2d.getColor();

        g2d.setColor(Color.WHITE);

        g2d.setStroke(new BasicStroke(strokeSize * 2));
        g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);

        g2d.setColor(originalColor);
    }

    @Override
    public String getToolType() {
        return Config.ERASER;
    }
}

