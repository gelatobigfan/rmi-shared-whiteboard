package inter;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteServer extends Remote {
    byte[] updateImage() throws IOException;

    void receiveImage(byte[] imageData) throws IOException;

    void broadcastCanvas(ISyncData remoteCanvas) throws IOException;

    void registerClient(IRemoteClient remoteClient) throws RemoteException;

    void assignManagerName(String name) throws RemoteException;

    void kickOutUser(String name) throws IOException;

    void handleManagerExit() throws RemoteException;

    void broadcastMessage(String message) throws IOException;

    void refreshUserList() throws RemoteException;

    JTextArea getChatArea() throws RemoteException;

    void newCanvas() throws IOException;

    void refreshCanvas() throws IOException;

    void terminateCanvas() throws IOException;

    boolean hasUser(String name) throws RemoteException;

    boolean isWhiteboardClosed() throws RemoteException;

    void removeClientByManager(String userToKick, String managerID) throws RemoteException;
}
