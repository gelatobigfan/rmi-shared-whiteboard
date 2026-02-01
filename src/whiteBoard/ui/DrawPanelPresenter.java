package whiteBoard.ui;

import impl.SyncData;
import inter.ISyncData;
import utils.Config;
import whiteBoard.command.ShapeDrawCommand;
import whiteBoard.drawing.DrawingTool;
import whiteBoard.drawing.DrawingToolFactory;
import whiteBoard.drawing.TextTool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Drawing Panel Presenter, responsible for handling user interactions and updating the model.
 */
public class DrawPanelPresenter {
    private final DrawPanelModel model;
    private final IDrawPanelView view;

    /**
     * Constructor
     */
    public DrawPanelPresenter(DrawPanelModel model, IDrawPanelView view) {
        this.model = model;
        this.view = view;

        if (model.getFrame() == null) {
            initializeCanvas();
        }
    }

    /**
     * Initialize the drawing canvas.
     */
    private void initializeCanvas() {
        if (model.isManager()) {
            // Manager creates a new canvas
            model.init();
            view.renderFrame(model.getFrame());
            try {
                model.sendImage();
            } catch (IOException e) {
                Config.PaneIOError();
                System.err.println("IOException: " + e);
            }
        } else {
            // Guest retrieves the canvas from the server
            try {
                byte[] imageData = model.getRemoteServer().updateImage();
                model.byteArrayToImage(imageData);
                view.renderFrame(model.getFrame());
            } catch (IOException e) {
                Config.PaneIOError();
                System.err.println("IOException: " + e);
            }
        }
    }

    /**
     * Pressed mouse event handler
     */
    public void handleMousePressed(MouseEvent e) {
        System.out.println("mousePressed");
        model.setHasMousePressed(true);

        // Check if the canvas is closed
        if (model.isClosed()) {
            view.showWarningMessage(
                    "Canvas closed, you need to create a new file or open a exist file.",
                    "Warning");
            return;
        }

        // Get start point coordinates
        model.setX1(e.getX());
        model.setY1(e.getY());
        model.setStartPoint(new Point(model.getX1(), model.getY1()));

        // Choose current color and tool type
        model.setColor(view.getToolBar().getColor());
        model.setToolType(view.getToolBar().getToolType());

        // Save the current frame
        model.saveCanvas();
        model.setMotion(true);

        // If the tool is text, show the text input dialog
        if (Config.TEXT.equals(model.getToolType())) {
            view.showTextInputDialog(model.getStartPoint());
            model.setMotion(false);
        }
    }

    /**
     * Dragging mouse event handler
     */
    public void handleMouseDragged(MouseEvent e) {
        model.setHasMouseDragged(true);
        model.setX2(e.getX());
        model.setY2(e.getY());

        model.setStartPoint(new Point(model.getX1(), model.getY1()));
        model.setEndPoint(new Point(model.getX2(), model.getY2()));

        DrawingTool tool = DrawingToolFactory.getTool(model.getToolType());
        if (tool != null) {
            // For non-drawing tools, render the saved frame
            if (!Config.DRAW.equals(model.getToolType()) && !Config.ERASER.equals(model.getToolType())) {
                view.renderFrame(model.getSavedFrame());
            }

            // Set the stroke size for the drawing tool
            if (Config.ERASER.equals(model.getToolType())) {
                model.getG2d().setStroke(new BasicStroke(view.getToolBar().getEraserSize()));
                tool.draw(model.getG2d(), model.getStartPoint(), model.getEndPoint(), Color.WHITE, view.getToolBar().getEraserSize());
            } else {
                model.getG2d().setStroke(new BasicStroke(Config.DEFAULT_STROKE));
                tool.draw(model.getG2d(), model.getStartPoint(), model.getEndPoint(), model.getColor(), Config.DEFAULT_STROKE);
            }

            // Live broadcast drawing state
            if (Config.DRAW.equals(model.getToolType()) || Config.ERASER.equals(model.getToolType())) {
                try {
                    model.broadcastDrawingState(view.getToolBar().getEraserSize());
                } catch (IOException ex) {
                    Config.PaneIOError();
                    System.err.println("IOException: " + ex);
                }
                model.setX1(model.getX2());
                model.setY1(model.getY2());
            }
        }
        view.repaint();
    }

    /**
     * Released mouse event handler
     */
    public void handleMouseReleased(MouseEvent e) {
        model.setHasMouseReleased(true);
        model.setEndPoint(new Point(model.getX2(), model.getY2()));

        DrawingTool tool = DrawingToolFactory.getTool(model.getToolType());
        if (tool != null) {
            if (Config.TEXT.equals(model.getToolType())) {
                view.showTextInputDialog(model.getStartPoint());
            } else {
                if (model.isHasMouseReleased() && model.isHasMouseDragged() && model.isHasMousePressed()) {
                    ShapeDrawCommand command = new ShapeDrawCommand(
                            view, tool, model.getStartPoint(), model.getEndPoint(), model.getColor(), Config.DEFAULT_STROKE);
                    model.getCommandManager().executeCommand(command);
                }
            }
            try {
                if (model.isHasMouseReleased() && model.isHasMouseDragged() && model.isHasMousePressed()) {
                    SyncData syncData = new SyncData(model.getToolType(), model.getColor(), model.getStartPoint(), model.getEndPoint(),
                            model.getName(), null, 0, view.getToolBar().getEraserSize());
                    model.getRemoteServer().broadcastCanvas(syncData);
                    model.sendImage();
                    model.resetMouseFlags();
                }
            } catch (Exception ex) {
                System.err.println("Exception: " + ex);
            }
        }

        view.repaint();
        model.setMotion(false);
        model.resetCoordinates();

        System.out.println("mouseReleased");
    }


    /**
     * New canvas creation
     */
    public void newCanvas() {
        model.newCanvas();
        view.renderFrame(model.getFrame());
        try {
            model.sendImage();
        } catch (IOException e) {
            Config.PaneIOError();
            System.err.println("IOException: " + e);
        }
    }

    /**
     * sync canvas with the server
     */
    public void syncCanvas(ISyncData remoteCanvas) throws RemoteException {
        DrawingTool tool = DrawingToolFactory.getTool(remoteCanvas.getDrawingMode());

        if (tool != null) {
            if (Config.ERASER.equals(remoteCanvas.getDrawingMode())) {
                model.getG2d().setStroke(new BasicStroke(remoteCanvas.getEraserSize()));
                tool.draw(model.getG2d(),
                        remoteCanvas.getStartPosition(),
                        remoteCanvas.getEndPosition(),
                        Color.WHITE,
                        remoteCanvas.getEraserSize());
            } else if (Config.TEXT.equals(remoteCanvas.getDrawingMode())) {
                TextTool textTool = new TextTool();
                textTool.setText(remoteCanvas.getText());
                textTool.setFontSize(remoteCanvas.getFontSize());

                textTool.draw(model.getG2d(),
                        remoteCanvas.getStartPosition(),
                        remoteCanvas.getEndPosition(),
                        remoteCanvas.getColor(),
                        0f);
            } else {
                model.getG2d().setStroke(new BasicStroke(Config.DEFAULT_STROKE));
                tool.draw(model.getG2d(),
                        remoteCanvas.getStartPosition(),
                        remoteCanvas.getEndPosition(),
                        remoteCanvas.getColor(),
                        Config.DEFAULT_STROKE);
            }

            view.repaint();
        }
    }


    /**
     * Get canvas from server
     */
    public void getCanvasFromServer(byte[] imageData) {
        try {
            model.byteArrayToImage(imageData);
            view.renderFrame(model.getFrame());
        } catch (IOException e) {
            Config.PaneIOError();
            System.err.println("IOException: " + e);
        }
    }

    /**
     * Change the canvas closed state
     */
    public void changeIsClosedState(boolean state) {
        model.setClosed(state);
    }

    /**
     * Get the canvas closed state
     */
    public boolean getIsClosed() {
        return model.isClosed();
    }
}