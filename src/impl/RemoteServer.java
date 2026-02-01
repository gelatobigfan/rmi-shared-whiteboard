package impl;

import inter.ISyncData;
import inter.IRemoteClient;
import inter.IRemoteServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remote server implementation class, responsible for managing clients and canvas
 */
public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
    private static final Logger LOGGER = Logger.getLogger(RemoteServer.class.getName());
    
    // Canvas image
    private static BufferedImage image;
    
    // Client management
    private final Map<String, IRemoteClient> clientMap = new ConcurrentHashMap<>();
    private final DefaultListModel<String> managerModel = new DefaultListModel<>();
    private final JTextArea chatArea = new JTextArea();
    private String managerName;
    
    // System messages
    private static final String SYSTEM_PREFIX = "System: ";
    private static final String MANAGER_NEW_CANVAS = SYSTEM_PREFIX + "Manager opened a new canvas.";
    private static final String MANAGER_CLOSED_CANVAS = SYSTEM_PREFIX + "Manager closed canvas.";
    private static final String USER_LEFT = SYSTEM_PREFIX + "%s has left.";
    private static final String USER_JOINED = SYSTEM_PREFIX + "%s has joined";

    /**
     * Constructor
     * 
     * @throws RemoteException If RMI error occurs during initialization
     */
    public RemoteServer() throws RemoteException {
        super();
    }

    /**
     * Get current canvas image as byte array
     * 
     * @return Byte array of the image, or null if no image exists
     * @throws IOException If IO error occurs during image processing
     */
    @Override
    public byte[] updateImage() throws IOException {
        if (image != null) {
            return imageToByteArray(image);
        }
        return null;
    }

    /**
     * Convert image to byte array
     * 
     * @param img Image to convert
     * @return Byte array of the image
     * @throws IOException If IO error occurs during image processing
     */
    private byte[] imageToByteArray(BufferedImage img) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(img, "png", data);
        return data.toByteArray();
    }

    /**
     * Update server's canvas image from byte array
     * 
     * @param imageData Byte array of the image
     * @throws IOException If IO error occurs during image processing
     */
    @Override
    public void receiveImage(byte[] imageData) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        image = ImageIO.read(data);
    }

    /**
     * Broadcast canvas to all clients except the sender
     * 
     * @param remoteCanvas Canvas to broadcast
     * @throws IOException If IO error occurs during broadcasting
     */
    @Override
    public void broadcastCanvas(ISyncData remoteCanvas) throws IOException {
        String senderUsername = remoteCanvas.getUsername();
        for (IRemoteClient client : clientMap.values()) {
            if (!client.getUsername().equals(senderUsername)) {
                try {
                    client.syncCanvas(remoteCanvas);
                } catch (RemoteException e) {
                    handleRemoteException(e, "Error broadcasting canvas to client: " + client.getUsername());
                }
            }
        }
    }

    /**
     * Add a client to the server
     * 
     * @param client Client to add
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public void registerClient(IRemoteClient client) throws RemoteException {
        clientMap.put(client.getUsername(), client);
    }

    /**
     * Set the manager name
     * 
     * @param name Manager name
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public void assignManagerName(String name) throws RemoteException {
        this.managerName = name;
        updateManagerModel(name);
    }

    /**
     * Update manager model with new manager name
     * 
     * @param name Manager name
     */
    private void updateManagerModel(String name) {
        SwingUtilities.invokeLater(() -> {
            managerModel.clear();
            if (name != null && !name.trim().isEmpty()) {
                managerModel.addElement(name);
            }
        });
    }

    /**
     * Handle manager leaving, notify all clients
     * 
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public void handleManagerExit() throws RemoteException {
        for (IRemoteClient client : clientMap.values()) {
            try {
                client.requestExit(managerName);
            } catch (RemoteException e) {
                handleRemoteException(e, "Error notifying client of manager leaving: " + client.getUsername());
            }
        }
        clientMap.clear();
    }

    /**
     * Broadcast message to all clients
     * 
     * @param message Message to broadcast
     * @throws IOException If IO error occurs during broadcasting
     */
    @Override
    public void broadcastMessage(String message) throws IOException {
        for (IRemoteClient client : clientMap.values()) {
            try {
                client.syncMessage(message);
            } catch (RemoteException e) {
                handleRemoteException(e, "Error broadcasting message to client: " + client.getUsername());
            }
        }
    }

    /**
     * Update client list for all clients
     * 
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public void refreshUserList() throws RemoteException {
        DefaultListModel<String> tempModel = new DefaultListModel<>();
        tempModel.addAll(clientMap.keySet());
        
        for (IRemoteClient client : clientMap.values()) {
            try {
                client.syncUserList(tempModel);
            } catch (RemoteException e) {
                handleRemoteException(e, "Error updating client list for: " + client.getUsername());
            }
        }
    }

    /**
     * Get chat area
     * 
     * @return Chat area
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public JTextArea getChatArea() throws RemoteException {
        return chatArea;
    }

    /**
     * Create new canvas and notify all clients
     * 
     * @throws IOException If IO error occurs
     */
    @Override
    public void newCanvas() throws IOException {
        for (IRemoteClient client : clientMap.values()) {
            try {
                client.clearCanvasRequest();
            } catch (RemoteException e) {
                handleRemoteException(e, "Error creating new canvas for client: " + client.getUsername());
            }
        }
        broadcastMessage(MANAGER_NEW_CANVAS);
    }

    /**
     * Update canvas for all clients except manager
     * 
     * @throws IOException If IO error occurs
     */
    @Override
    public void refreshCanvas() throws IOException {
        if (image == null) {
            LOGGER.warning("Attempted to update canvas with null image");
            return;
        }
        
        byte[] imageBytes = imageToByteArray(image);
        
        for (IRemoteClient client : clientMap.values()) {
            try {
                if (!client.getUsername().equals(managerName)) {
                    client.loadCanvasImage(imageBytes);
                }
            } catch (RemoteException e) {
                handleRemoteException(e, "Error updating canvas for client: " + client.getUsername());
            }
        }
    }

    /**
     * Close canvas and notify all clients except manager
     * 
     * @throws IOException If IO error occurs
     */
    @Override
    public void terminateCanvas() throws IOException {
        for (IRemoteClient client : clientMap.values()) {
            if (!client.getUsername().equals(managerName)) {
                notifyClientOfCanvasClose(client);
            }
        }
        broadcastMessage(MANAGER_CLOSED_CANVAS);
    }
    
    /**
     * Notify client of canvas close in a separate thread
     * 
     * @param client Client to notify
     */
    private void notifyClientOfCanvasClose(IRemoteClient client) {
        Thread t = new Thread(() -> {
            try {
                client.requestCanvasClose();
            } catch (RemoteException e) {
                handleRemoteException(e, "Error closing canvas for client: " + getClientUsername(client));
            }
        });
        t.start();
    }
    
    /**
     * Get client username safely
     * 
     * @param client Client
     * @return Username or "unknown" if error occurs
     */
    private String getClientUsername(IRemoteClient client) {
        try {
            return client.getUsername();
        } catch (RemoteException e) {
            return "unknown";
        }
    }

    /**
     * Check if user exists in the server
     * 
     * @param name Username to check
     * @return True if user exists, false otherwise
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public boolean hasUser(String name) throws RemoteException {
        if (name.equals(managerName)) {
            return true;
        }
        return clientMap.containsKey(name);
    }

    /**
     * Get canvas closed state from manager
     * 
     * @return True if canvas is closed, false otherwise
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public boolean isWhiteboardClosed() throws RemoteException {
        IRemoteClient manager = clientMap.get(managerName);
        if (manager != null) {
            try {
                return manager.isRoomClosed();
            } catch (RemoteException e) {
                handleRemoteException(e, "Error getting canvas state from manager");
                return false;
            }
        }
        return false;
    }

    /**
     * Kick user from the server
     * 
     * @param userToKick Username to kick
     * @param managerID Manager ID
     * @throws RemoteException If RMI error occurs
     */
    @Override
    public void removeClientByManager(String userToKick, String managerID) throws RemoteException {
        IRemoteClient clientToKick = clientMap.get(userToKick);
        if (clientToKick != null) {
            try {
                clientToKick.requestExit(managerID);
                clientMap.remove(userToKick);
                refreshUserList();
            } catch (RemoteException e) {
                handleRemoteException(e, "Error kicking user: " + userToKick);
            }
        }
    }

    /**
     * Remove user from the server
     * 
     * @param name Username to remove
     * @throws IOException If IO error occurs
     */
    @Override
    public void kickOutUser(String name) throws IOException {
        clientMap.remove(name);
        refreshUserList();
        broadcastMessage(String.format(USER_LEFT, name));
    }
    
    /**
     * Handle remote exception
     * 
     * @param e Exception
     * @param message Error message
     */
    private void handleRemoteException(RemoteException e, String message) {
        LOGGER.log(Level.SEVERE, message, e);
        System.err.println(message + ": " + e.getMessage());
    }
}
