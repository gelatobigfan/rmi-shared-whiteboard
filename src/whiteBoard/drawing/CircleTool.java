package whiteBoard.drawing;

import utils.Config;

import java.awt.*;

public class CircleTool implements DrawingTool {
    @Override
    public void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeSize));

        int radius = (int) Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2)) / 2;
        int centerX = (startPoint.x + endPoint.x) / 2;
        int centerY = (startPoint.y + endPoint.y) / 2;
        int startX = centerX - radius;
        int startY = centerY - radius;

        g2d.drawOval(startX, startY, radius * 2, radius * 2);
    }

    @Override
    public String getToolType() {
        return Config.CIRCLE;
    }
}
