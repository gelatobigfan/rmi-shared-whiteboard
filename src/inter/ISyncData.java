package inter;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISyncData extends Remote {
    String getDrawingMode() throws RemoteException;

    Color getColor() throws RemoteException;

    Point getStartPosition() throws RemoteException;

    Point getEndPosition() throws RemoteException;

    String getUsername() throws RemoteException;

    float getEraserSize() throws RemoteException;

    String getText() throws RemoteException;
    int getFontSize() throws RemoteException;

}
