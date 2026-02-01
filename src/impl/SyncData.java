package impl;

import inter.ISyncData;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SyncData extends UnicastRemoteObject implements ISyncData {
    private final String drawType;
    private final Color color;
    private final Point startPos;
    private final Point endPos;
    private final String username;
    private final String text;
    private final int textSize;
    private final float eraserSize;

    public SyncData(String drawType, Color color, Point startPos,
                    Point endPos, String username, String text,
                    int textSize, float eraserSize) throws RemoteException {
        super();
        this.drawType = drawType;
        this.color = color;
        this.startPos = startPos;
        this.endPos = endPos;
        this.username = username;
        this.text = text;
        this.textSize = textSize;
        this.eraserSize = eraserSize;
    }

    @Override
    public String getDrawingMode() throws RemoteException {
        return drawType;
    }

    @Override
    public Color getColor() throws RemoteException {
        return color;
    }

    @Override
    public Point getStartPosition() throws RemoteException {
        return startPos;
    }

    @Override
    public Point getEndPosition() throws RemoteException {
        return endPos;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public float getEraserSize() throws RemoteException {
        return eraserSize;
    }

    @Override
    public String getText() throws RemoteException {
        return text;
    }

    @Override
    public int getFontSize() throws RemoteException {
        return textSize;
    }

}
