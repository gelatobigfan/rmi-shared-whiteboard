package whiteBoard.ui;

import inter.ISyncData;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * CanvasView - Interface for drawing panel view
 */
public interface IDrawPanelView {
    // Get the drawing tool factory
    Graphics2D getGraphics2D();

    // Get the drawing tool
    void renderFrame(BufferedImage frame);

    // Re-paint the canvas
    void repaint();

    // Get current drawing tool
    BufferedImage getCanvasImage();

    // Display the text input dialog
    void showTextInputDialog(Point position);

    // Display warning message
    void showWarningMessage(String message, String title);

    ToolBar getToolBar();

    void handleMousePressed(MouseEvent e);

    void handleMouseDragged(MouseEvent e);

    void handleMouseReleased(MouseEvent e);

    // Synchronize the canvas with the server
    void syncCanvas(ISyncData remoteCanvas);

    void getCanvasFromServer(byte[] imageData);

    // Send the image to the server
    void sendImage();

    // Send the saved image to the server
    void sendSavedImage(BufferedImage image);

    // Change the state of the canvas
    void changeIsClosedState(boolean state);

    // Get the closed state of the canvas
    boolean isClosedState();
}