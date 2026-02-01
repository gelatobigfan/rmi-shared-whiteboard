package whiteBoard.ui;

import impl.SyncData;
import inter.ISyncData;
import inter.IRemoteServer;
import whiteBoard.drawing.DrawingToolFactory;
import whiteBoard.drawing.TextTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Class for the drawing panel view, which handles the drawing canvas and user interactions.
 */
public class DrawPanelView extends JPanel implements IDrawPanelView {
    private final ToolBar toolBar;
    private final DrawPanelPresenter presenter;
    private final DrawPanelModel model;

    /**
     * Mouse motion listener
     */
    private final MouseMotionAdapter motionLister = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            presenter.handleMouseDragged(e);
        }
    };

    /**
     * Mouse pressed listener
     */
    private final MouseListener startListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            presenter.handleMousePressed(e);
        }
    };

    /**
     * Mouse released listener
     */
    private final MouseListener endListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            presenter.handleMouseReleased(e);
        }
    };

    /**
     * Constructor for the drawing panel view.
     */
    public DrawPanelView(ToolBar toolBar, IRemoteServer remoteServer, boolean isManager, String name) {
        this.toolBar = toolBar;

        // create model and presenter
        model = new DrawPanelModel(remoteServer, isManager, name);
        presenter = new DrawPanelPresenter(model, this);

        initializePanel();
    }

    /**
     * Initialize the drawing panel.
     */
    private void initializePanel() {
        addMouseListener(startListener);
        addMouseMotionListener(motionLister);
        addMouseListener(endListener);
        setDoubleBuffered(false);
    }

    /**
     * Rewrite the paintComponent method to draw the current frame.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // To current frame
        if (model.getFrame() != null) {
            g.drawImage(model.getFrame(), 0, 0, this);
        }
    }

    /**
     * Display the text input dialog for user input.
     */
    @Override
    public void showTextInputDialog(Point position) {
        TextInputDialog dialog = new TextInputDialog(SwingUtilities.getWindowAncestor(this));

        if (dialog.show()) {
            String text = dialog.getInputText();
            int fontSize = dialog.getFontSize();

            if (text != null && !text.isEmpty()) {
                TextTool textTool = (TextTool) DrawingToolFactory.getTool("text");
                textTool.setText(text);
                textTool.setFontSize(fontSize);

                textTool.draw(model.getG2d(), position, position, model.getColor(), 0);

                try {
                    SyncData syncData = new SyncData("text", model.getColor(), position, position,
                            model.getName(), text, fontSize, 0);
                    model.getRemoteServer().broadcastCanvas(syncData);
                    model.sendImage();
                } catch (IOException e) {
                    System.err.println("IOException: " + e);
                }

                repaint();
            }
        }
    }


    @Override
    public void handleMousePressed(MouseEvent e) {
        presenter.handleMousePressed(e);
    }

    @Override
    public void handleMouseDragged(MouseEvent e) {
        presenter.handleMouseDragged(e);
    }

    @Override
    public void handleMouseReleased(MouseEvent e) {
        presenter.handleMouseReleased(e);
    }

    @Override
    public Graphics2D getGraphics2D() {
        return model.getG2d();
    }

    @Override
    public void renderFrame(BufferedImage frame) {
        model.getG2d().drawImage(frame, 0, 0, null);
        repaint();
    }

    @Override
    public BufferedImage getCanvasImage() {
        return model.getFrame();
    }

    @Override
    public void showWarningMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public ToolBar getToolBar() {
        return toolBar;
    }

    @Override
    public void syncCanvas(ISyncData remoteCanvas) {
        try {
            presenter.syncCanvas(remoteCanvas);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getCanvasFromServer(byte[] imageData) {
        presenter.getCanvasFromServer(imageData);
    }

    @Override
    public void sendImage() {
        try {
            model.sendImage();
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }

    @Override
    public void sendSavedImage(BufferedImage image) {
        try {
            model.sendSavedImage(image);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }

    @Override
    public void changeIsClosedState(boolean state) {
        presenter.changeIsClosedState(state);
    }

    @Override
    public boolean isClosedState() {
        return presenter.getIsClosed();
    }


    public void newCanvas() {
        presenter.newCanvas();
    }
}