/**
 * Author: Junyi Pan
 * Email: junyip1@student.unimelb.edu.au
 * StudentID: 1242599
 */

import impl.RemoteClient;

import javax.swing.*;
import java.io.IOException;

public class JoinWhiteBoard extends WhiteBoardBase {

    public static void main(String[] args) {
        JoinWhiteBoard joinWhiteBoard = new JoinWhiteBoard();
        joinWhiteBoard.start(args);
    }

    @Override
    public void start(String[] args) {
        if (!parseArguments(args)) {
            return;
        }

        server = connectToServer();
        if (server == null) {
            return;
        }

        if (!checkUserAndCanvas()) {
            return;
        }

        startClientWhiteboard();
    }

    /**
     * Check username and canvas status
     *
     * @return Whether check passed
     */
    private boolean checkUserAndCanvas() {
        try {
            // Check if username already exists
            if (server.hasUser(username)) {
                showErrorMessage("Username already exists: " + username + "\nPlease try another name.", "Warning");
                return false;
            }

            // Check if manager has closed the canvas
            while (server.isWhiteboardClosed()) {
                Object[] options = {"Retry", "Close"};
                int answer = JOptionPane.showOptionDialog(null,
                        "Manager has not opened a new file yet.",
                        "From Manager",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (answer == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            System.out.println("Waiting for manager authorization...");

            int answer = JOptionPane.showConfirmDialog(null,
                    username + " wants to share your whiteboard", "Share Request", JOptionPane.YES_NO_OPTION);
            boolean result = (answer == JOptionPane.YES_OPTION);

            if (!result) {
                showErrorMessage("Access denied, please contact the manager", "Warning");
                return false;
            }


            client = new RemoteClient(username, false, server);


            server.registerClient(client);
            return true;
        } catch (Exception e) {
            System.err.println("Error checking user and canvas status: " + e.getMessage());
            showErrorMessage("An error occurred", "Warning");
            return false;
        }
    }

    /**
     * Start client whiteboard
     */
    private void startClientWhiteboard() {
        try {
            // Create whiteboard and open GUI

            System.out.println("Client connected to server");
            client.triggerListRefresh();
            client.notifySystemJoin();
            System.out.println("Whiteboard ready");

            // Used to catch user quit, user may normally close the app, or force quit the app
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.kickOutUser(username);
                } catch (IOException ignored) {
                }
            }));
        } catch (Exception e) {
            System.err.println("Error starting client whiteboard: " + e.getMessage());
            showErrorMessage("An error occurred", "Warning");
        }
    }
}
