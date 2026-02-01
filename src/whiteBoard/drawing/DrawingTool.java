package whiteBoard.drawing;

import java.awt.*;

public interface DrawingTool {
    void draw(Graphics2D g2d, Point startPoint, Point endPoint, Color color, float strokeSize);

    String getToolType();
}
