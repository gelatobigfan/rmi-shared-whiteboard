package whiteBoard.ui;

import impl.SyncData;
import inter.IRemoteServer;
import utils.Config;
import whiteBoard.command.CommandManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Drawing panel model, responsible for managing the drawing state and interactions.
 */
public class DrawPanelModel {
    private final IRemoteServer remoteServer;
    private final boolean isManager;
    private final String name;
    private final CommandManager commandManager = new CommandManager();

    private int x1, y1, x2, y2;
    private Color color;
    private String toolType;
    private Graphics2D g2d;
    private BufferedImage frame;
    private BufferedImage savedFrame;
    private Point startPoint;
    private Point endPoint;

    private boolean hasMouseDragged = false;
    private boolean hasMousePressed = false;
    private boolean hasMouseReleased = false;
    private boolean isClosed = false;
    private boolean isMotion = false;


    public DrawPanelModel(IRemoteServer remoteServer, boolean isManager, String name) {
        this.remoteServer = remoteServer;
        this.isManager = isManager;
        this.name = name;

        x1 = x2 = y1 = y2 = 0;
        isClosed = false;
    }


    public void init() {
        frame = new BufferedImage( Config.GUI_WIDTH - 210, Config.GUI_HEIGHT - 135, BufferedImage.TYPE_INT_RGB);
        g2d = (Graphics2D) frame.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.WHITE);
        g2d.setStroke(new BasicStroke(Config.DEFAULT_STROKE));
        cleanCanvas();
    }


    public void cleanCanvas() {
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, Config.GUI_WIDTH, Config.GUI_HEIGHT);
        g2d.setPaint(color);
    }


    public void saveCanvas() {
        ColorModel colorModel = frame.getColorModel();
        WritableRaster raster = frame.copyData(null);
        savedFrame = new BufferedImage(colorModel, raster, false, null);
    }


    public void newCanvas() {
        init();
    }


    public byte[] imageToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(frame, "png", baos);
        return baos.toByteArray();
    }


    public void byteArrayToImage(byte[] imageData) throws IOException {
        frame = ImageIO.read(new ByteArrayInputStream(imageData));
        g2d = (Graphics2D) frame.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    public void broadcastDrawingState(float eraserSize) throws IOException {
        SyncData syncData = new SyncData(toolType, color, startPoint, endPoint,
                name, null, 0, eraserSize);
        remoteServer.broadcastCanvas(syncData);
    }

    /**
     * Send image to the server
     */
    public void sendImage() throws IOException {
        remoteServer.receiveImage(imageToByteArray());
    }

    /**
     * Send saved image to the server
     */
    public void sendSavedImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        remoteServer.receiveImage(baos.toByteArray());
    }

    // Getters and Setters

    public BufferedImage getFrame() {
        return frame;
    }

    public BufferedImage getSavedFrame() {
        return savedFrame;
    }

    public Graphics2D getG2d() {
        return g2d;
    }

    public void setG2d(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public boolean isHasMouseDragged() {
        return hasMouseDragged;
    }

    public void setHasMouseDragged(boolean hasMouseDragged) {
        this.hasMouseDragged = hasMouseDragged;
    }

    public boolean isHasMousePressed() {
        return hasMousePressed;
    }

    public void setHasMousePressed(boolean hasMousePressed) {
        this.hasMousePressed = hasMousePressed;
    }

    public boolean isHasMouseReleased() {
        return hasMouseReleased;
    }

    public void setHasMouseReleased(boolean hasMouseReleased) {
        this.hasMouseReleased = hasMouseReleased;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isMotion() {
        return isMotion;
    }

    public void setMotion(boolean motion) {
        isMotion = motion;
    }

    public IRemoteServer getRemoteServer() {
        return remoteServer;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getName() {
        return name;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void resetMouseFlags() {
        hasMouseReleased = false;
        hasMouseDragged = false;
        hasMousePressed = false;
    }

    public void resetCoordinates() {
        x1 = x2 = y1 = y2 = 0;
    }
}