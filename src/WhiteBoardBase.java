import inter.IRemoteClient;
import inter.IRemoteServer;
import utils.ExceptionHandler;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public abstract class WhiteBoardBase {
    public static final String REGISTRY_NAME = "SharedWhiteBoard";

    protected String serverIPAddress;
    protected int serverPort;
    protected String username;
    protected IRemoteServer server;
    protected IRemoteClient client;

    /**
     * Parse command line arguments
     *
     * @param args Command line arguments
     * @return Whether parsing was successful
     */
    protected boolean parseArguments(String[] args) {
        if (args.length != 3) {
            showErrorMessage("Insufficient arguments.\nFormat: <server IP address> <server port> <username>", "Warning");
            return false;
        }

        serverIPAddress = args[0];

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            showErrorMessage("Port number must be an integer: " + args[1], "Warning");
            return false;
        }

        username = args[2];
        return true;
    }

    /**
     * Connect to remote server
     *
     * @return Remote server object, or null if connection fails
     */
    protected IRemoteServer connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);
            return (IRemoteServer) registry.lookup(CreateWhiteBoard.REGISTRY_NAME);
        } catch (RemoteException e) {
            ExceptionHandler.handleRemoteException(e, null);
            return null;
        } catch (NotBoundException e) {
            System.err.println("Server not started");
            showErrorMessage("Server not found. Please check your IP address or port number.", "Warning");
            return null;
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            showErrorMessage("Error connecting to server", "Warning");
            return null;
        }
    }

    /**
     * Display error message and exit program
     *
     * @param message Error message
     * @param title   Dialog title
     */
    protected void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    /**
     * Start whiteboard application
     *
     * @param args Command line arguments
     */
    public abstract void start(String[] args);
}
