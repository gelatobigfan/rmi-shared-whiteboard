package whiteBoard.drawing;

import utils.Config;

import java.awt.*;

public class RectangleTool implements DrawingTool {
    @Override
    public void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeSize));
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        g2d.drawRect(x, y, width, height);
    }

    @Override
    public String getToolType() {
        return Config.RECTANGLE;
    }
}

