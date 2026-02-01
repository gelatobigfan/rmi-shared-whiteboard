package utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    public static void handleRemoteException(RemoteException e, Component parent) {
        logger.log(Level.SEVERE, "RMI Connection Error", e);
        JOptionPane.showMessageDialog(parent, "Failed to connect to server, please check your network connection",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void handleIOException(IOException e, Component parent) {
        logger.log(Level.SEVERE, "IO Error", e);
        JOptionPane.showMessageDialog(parent, "File operation failed: " + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
    }
}
