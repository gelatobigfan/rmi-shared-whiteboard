package whiteBoard.drawing;

import utils.Config;

import java.awt.*;

public class DrawTool implements DrawingTool {
    @Override
    public void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeSize));
        g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }

    @Override
    public String getToolType() {
        return Config.DRAW;
    }
}
