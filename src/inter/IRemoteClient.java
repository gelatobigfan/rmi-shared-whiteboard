package inter;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

// This interface defines the remote contract for
public interface IRemoteClient extends Remote {
    String getUsername() throws RemoteException;

    void syncCanvas(ISyncData data) throws IOException;

    void syncMessage(String message) throws IOException;

    void requestExit(String managerName) throws RemoteException;

    void syncUserList(DefaultListModel<String> tempModel) throws RemoteException;

    void triggerListRefresh() throws RemoteException;

    void notifySystemJoin() throws IOException;

    void clearCanvasRequest() throws RemoteException;

    void loadCanvasImage(byte[] imageData) throws IOException;

    void requestCanvasClose() throws RemoteException;

    boolean isRoomClosed() throws RemoteException;
}
