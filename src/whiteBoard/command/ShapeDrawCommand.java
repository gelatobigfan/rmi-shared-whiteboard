package whiteBoard.command;

import whiteBoard.drawing.DrawingTool;
import whiteBoard.ui.IDrawPanelView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ShapeDrawCommand implements DrawCommand {
    private final IDrawPanelView drawPanel;
    private final DrawingTool tool;
    private final Point startPoint;
    private final Point endPoint;
    private final Color color;
    private final float strokeSize;
    private final BufferedImage previousState;

    public ShapeDrawCommand(IDrawPanelView drawPanel, DrawingTool tool,
                            Point startPoint, Point endPoint,
                            Color color, float strokeSize) {
        this.drawPanel = drawPanel;
        this.tool = tool;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.color = color;
        this.strokeSize = strokeSize;
        this.previousState = drawPanel.getCanvasImage();
    }

    @Override
    public void execute() {
        tool.draw(drawPanel.getGraphics2D(), startPoint, endPoint, color, strokeSize);
        drawPanel.repaint();
    }

    @Override
    public void undo() {
//        drawPanel.restoreCanvas(previousState);
    }
}