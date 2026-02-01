/**
 * Author: Junyi Pan
 * Email: junyip1@student.unimelb.edu.au
 * StudentID: 1242599
 */

import impl.RemoteClient;
import impl.RemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class CreateWhiteBoard extends WhiteBoardBase {

    public static void main(String[] args) {
        CreateWhiteBoard createWhiteBoard = new CreateWhiteBoard();
        createWhiteBoard.start(args);
    }

    @Override
    public void start(String[] args) {
        if (!parseArguments(args)) {
            return;
        }

        if (!startServer()) {
            return;
        }

        startManagerWhiteboard();
    }

    /**
     * Start RMI server
     *
     * @return Whether server startup was successful
     */
    private boolean startServer() {
        try {
            server = new RemoteServer();
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.bind(REGISTRY_NAME, server);
            System.out.println("RMI service ready");
            return true;
        } catch (ExportException e) {
            System.err.println("Server exception: Port " + serverPort + " is already in use.");
            showErrorMessage("Port number " + serverPort + " is already in use, please try another port number.", "Warning");
            return false;
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            showErrorMessage("RMI Error", "Warning");
            return false;
        }
    }

    /**
     * Start manager whiteboard
     */
    private void startManagerWhiteboard() {
        try {
            client = new RemoteClient(username, true, server);
            server.assignManagerName(username);
            server.registerClient(client);

            System.out.println("Client connected to server");
            client.triggerListRefresh();
            client.notifySystemJoin();
            System.out.println("Manager whiteboard ready");
        } catch (Exception e) {
            System.err.println("Error: " + e);
            showErrorMessage("An error occurred", "Warning");
        }
    }
}
