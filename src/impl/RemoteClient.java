package impl;

import inter.ISyncData;
import inter.IRemoteClient;
import inter.IRemoteServer;
import utils.Config;
import whiteBoard.ui.DrawPanelView;
import whiteBoard.ui.ToolBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remote client implementation class, responsible for handling user interface and communication with the server
 */
public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
    private static final Logger LOGGER = Logger.getLogger(RemoteClient.class.getName());
    
    // User information
    private final String username;
    private final boolean isManager;
    
    // UI components
    private final JFrame frame;
    private final JPanel chatBox;
    private final DrawPanelView drawPanel;
    private final ToolBar toolBar;
    
    // Chat components
    private DefaultListModel<String> userModel;
    private JList<String> userList;
    private JTextArea chatArea;
    private JTextField chatInputField;
    
    // Server connection
    private final IRemoteServer remoteServer;
    
    // File related
    private String filePath;
    
    // Constants
    private static final String TITLE_PREFIX = "White Board - ";
    private static final String SYSTEM_MESSAGE_PREFIX = "System: ";

    /**
     * Constructor
     * 
     * @param userID User ID
     * @param isManager Whether is manager
     * @param remoteServer Remote server interface
     * @throws IOException If IO error occurs during initialization
     */
    public RemoteClient(String userID, boolean isManager, IRemoteServer remoteServer) throws IOException {
        this.username = userID;
        this.isManager = isManager;
        this.remoteServer = remoteServer;

        // Initialize UI components
        frame = initializeMainFrame(userID);
        toolBar = new ToolBar();
        drawPanel = initializeDrawPanel(toolBar);
        chatBox = new JPanel();
        
        // Setup UI layout
        setupUILayout();
    }
    
    /**
     * Initialize main frame
     * 
     * @param userID User ID
     * @return Initialized JFrame
     */
    private JFrame initializeMainFrame(String userID) {
        JFrame newFrame = new JFrame();
        newFrame.setTitle(TITLE_PREFIX + userID);
        newFrame.setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        newFrame.setMinimumSize(new Dimension(Config.GUI_WIDTH, Config.GUI_HEIGHT));
        newFrame.setLocationRelativeTo(null);
        newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        newFrame.addWindowListener(createWindowCloseListener());
        
        // Setup menu bar if user is manager
        if (isManager) {
            newFrame.setJMenuBar(createMenuBar());
        } else {
            newFrame.setJMenuBar(new JMenuBar());
        }
        
        return newFrame;
    }
    
    /**
     * Initialize drawing panel
     * 
     * @param toolBar Tool bar
     * @return Initialized DrawPanelView
     * @throws IOException If IO error occurs
     */
    private DrawPanelView initializeDrawPanel(ToolBar toolBar) throws IOException {
        return new DrawPanelView(toolBar, remoteServer, isManager, username);
    }
    
    /**
     * Setup UI layout
     * 
     * @throws RemoteException If RMI error occurs
     */
    private void setupUILayout() throws RemoteException {
        // Setup chat components
        setupChatBox();
        
        // Add components to frame
        frame.add(drawPanel, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.SOUTH);
        frame.add(chatBox, BorderLayout.EAST);
        
        // Make frame visible
        frame.setVisible(true);
    }

    /**
     * Create window close listener
     * 
     * @return WindowAdapter for handling window closing
     */
    private WindowAdapter createWindowCloseListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                handleWindowClosing();
            }
        };
    }

    /**
     * Handle window closing event
     */
    private void handleWindowClosing() {
        String message;
        if (isManager) {
            message = "Exiting will close the canvas for all users.\n" +
                    "Please save your work before continuing.\n" +
                    "Do you still want to exit?";
        } else {
            message = "Are you sure you want to exit?";
        }

        int result = JOptionPane.showConfirmDialog(
                frame,
                message,
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            if (isManager) {
                try {
                    remoteServer.handleManagerExit();
                } catch (RemoteException e) {
                    handleRemoteException(e, "Error occurred when manager leaves");
                }
            }
            frame.dispose();
            System.exit(0);
        }
    }


    /**
     * Set up chat box
     */
    private void setupChatBox() throws RemoteException {
        chatBox.setLayout(new BorderLayout());

        // Initialize user list panel
        JPanel userListPanel = createUserListPanel();
        
        // Initialize chat area
        setupChatArea();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(200, 200));
        
        // Initialize chat input panel
        JPanel chatInputPanel = createChatInputPanel();
        
        // Create bottom split pane (chat area + input)
        JSplitPane bottomSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                chatScrollPane,
                chatInputPanel
        );
        bottomSplitPane.setDividerLocation(250);
        bottomSplitPane.setResizeWeight(1);
        
        // Create main split pane (user list + bottom pane)
        JSplitPane mainSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                userListPanel,
                bottomSplitPane
        );
        mainSplitPane.setDividerLocation(150);
        mainSplitPane.setResizeWeight(0);
        
        chatBox.add(mainSplitPane, BorderLayout.CENTER);
    }

    /**
     * Create user list panel
     * 
     * @return User list panel
     */
    private JPanel createUserListPanel() {
        // Initialize user list
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 200));

        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.add(userListScrollPane, BorderLayout.CENTER);

        // Add kick button for manager
        if (isManager) {
            JButton kickButton = createKickButton();
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(kickButton);
            userListPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add double-click kick user functionality
            setupUserListDoubleClickHandler();
        }
        
        return userListPanel;
    }

    /**
     * Create kick button
     * 
     * @return Kick button
     */
    private JButton createKickButton() {
        JButton kickButton = new JButton("Kick");
        kickButton.addActionListener(e -> kickSelectedUser());
        return kickButton;
    }
    
    /**
     * Kick selected user
     */
    private void kickSelectedUser() {
        String selectedUser = userList.getSelectedValue();
        
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(chatBox, "Please choose a user before proceeding.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedUser.equals(username)) {
            JOptionPane.showMessageDialog(chatBox, "Cannot kick yourself", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(chatBox,
                "Do you want to kick " + selectedUser + "?",
                "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                remoteServer.removeClientByManager(selectedUser, username);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Unable to remove user: " + selectedUser, ex);
                JOptionPane.showMessageDialog(chatBox, "Unable to remove user: " + selectedUser, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Set up user list double click handler
     */
    private void setupUserListDoubleClickHandler() {
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    JList list = (JList) evt.getSource();
                    int index = list.locationToIndex(evt.getPoint());
                    String name = (String) list.getModel().getElementAt(index);
                    
                    if (!name.equals(username)) {
                        if (JOptionPane.showConfirmDialog(chatBox,
                                "Do you want to kick " + name + "?",
                                "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            try {
                                requestExit(name);
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "Unable to remove user: " + name, e);
                                JOptionPane.showMessageDialog(chatBox, "Unable to remove user: " + name, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Set up chat area
     */
    private void setupChatArea() throws RemoteException {
        chatArea = remoteServer.getChatArea();
        chatArea.setEditable(false);
    }

    /**
     * Create chat input panel
     * 
     * @return Chat input panel
     */
    private JPanel createChatInputPanel() {
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputField = new JTextField();
        JButton submitButton = new JButton("Send");
        submitButton.setPreferredSize(new Dimension(100, 10));

        // Listen to submit button
        submitButton.addActionListener(e -> sendChatMessage());
        
        // Listen to enter key
        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendChatMessage();
                }
            }
        });

        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(submitButton, BorderLayout.EAST);
        chatInputPanel.setPreferredSize(new Dimension(200, 200));
        
        return chatInputPanel;
    }

    /**
     * Send chat message
     */
    private void sendChatMessage() {
        String message = chatInputField.getText();
        if (message.isEmpty()) {
            return;
        }
        
        try {
            remoteServer.broadcastMessage(username + ": " + message);
            chatInputField.setText("");
        } catch (IOException ex) {
            handleIOException(ex, "IO error occurred when sending message");
        }
    }

    /**
     * Create menu bar
     * 
     * @return Menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Only show file menu to manager
        if (isManager) {
            JMenu fileMenu = new JMenu("File");
            
            // New file option
            JMenuItem newItem = new JMenuItem("New");
            newItem.addActionListener(e -> {
                try {
                    newFile();
                } catch (IOException ex) {
                    handleIOException(ex, "Error occurred when creating new file");
                }
            });
            
            // Open file option
            JMenuItem openItem = new JMenuItem("Open");
            openItem.addActionListener(e -> openFile());
            
            // Save file option
            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener(e -> save());
            
            // Save as option
            JMenuItem saveAsItem = new JMenuItem("Save As");
            saveAsItem.addActionListener(e -> saveAs());
            
            // Close file option
            JMenuItem closeItem = new JMenuItem("Close");
            closeItem.addActionListener(e -> {
                try {
                    close();
                } catch (IOException ex) {
                    handleIOException(ex, "Error occurred when closing file");
                }
            });
            
            // Add menu items to file menu
            fileMenu.add(newItem);
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
            fileMenu.add(closeItem);
            
            // Add file menu to menu bar
            menuBar.add(fileMenu);
        }
        
        return menuBar;
    }

    /**
     * New file method
     */
    private void newFile() throws IOException {
        if (drawPanel.isClosedState()) {
            initNewCanvas();
        } else {
            int answer = JOptionPane.showConfirmDialog(frame,
                    "Do you want to create a new canvas?\n" +
                    "The existing canvas will be deleted.", 
                    "Warning", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                initNewCanvas();
            }
        }
    }
    
    /**
     * Create new canvas
     */
    private void initNewCanvas() throws IOException {
        remoteServer.newCanvas();
        filePath = null;
        drawPanel.changeIsClosedState(false);
        JOptionPane.showMessageDialog(frame, "Canvas has been created! 🎉", "Canvas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Open file method
     */
    private void openFile() {
        FileDialog fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            return;
        }
        
        filePath = fileDialog.getDirectory() + fileDialog.getFile();
        
        try {
            if (!filePath.toLowerCase().endsWith(".png")) {
                JOptionPane.showMessageDialog(frame, "Please select a .png file",
                        "Invalid File", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BufferedImage image = ImageIO.read(new File(filePath));
            drawPanel.renderFrame(image);
            drawPanel.sendSavedImage(image);
            remoteServer.refreshCanvas();
            remoteServer.broadcastMessage(SYSTEM_MESSAGE_PREFIX + "An existing canvas has been opened by the manager.");
            
            if (drawPanel.isClosedState()) {
                drawPanel.changeIsClosedState(false);
                JOptionPane.showMessageDialog(frame, "Canvas has been opened", "Canvas",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            handleIOException(ex, "Error occurred when opening file");
        }
    }

    /**
     * Save current canvas method
     */
    private void save() {
        if (filePath != null) {
            saveToFile(filePath);
        } else {
            int answer = JOptionPane.showConfirmDialog(frame,
                    "You haven't saved it as a file yet\n" +
                    "Press Yes to save now",
                    "Warning", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                saveAs();
            }
        }
    }
    
    /**
     * Save to file
     * 
     * @param path File path
     */
    private void saveToFile(String path) {
        try {
            ImageIO.write(drawPanel.getCanvasImage(), "png", new File(path));
        } catch (IOException ex) {
            handleIOException(ex, "Error occurred when saving file");
        }
    }

    /**
     * Save as current canvas method
     */
    private void saveAs() {
        FileDialog fileDialog = new FileDialog(frame, "Save As", FileDialog.SAVE);
        fileDialog.setVisible(true);
        
        if (fileDialog.getFile() == null) {
            return;
        }
        
        filePath = fileDialog.getDirectory() + fileDialog.getFile();
        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath += ".png";
        }
        
        saveToFile(filePath);
    }

    /**
     * Close current canvas method
     */
    private void close() throws IOException {
        if (drawPanel.isClosedState()) {
            JOptionPane.showMessageDialog(frame, "Canvas is already closed",
                    "Canvas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {"Save", "Don't Save", "Cancel"};
        int answer = JOptionPane.showOptionDialog(frame,
                "The canvas will be closed. ❌\n" +
                "Do you want to save the canvas?",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (answer == JOptionPane.YES_OPTION) {
            save();
        } else if (answer == JOptionPane.CANCEL_OPTION) {
            return;
        }

        drawPanel.newCanvas();
        drawPanel.changeIsClosedState(true);
        remoteServer.terminateCanvas();
    }

    /**
     * Handle IO exception
     */
    private void handleIOException(IOException ex, String message) {
        LOGGER.log(Level.SEVERE, message, ex);
        Config.PaneIOError();
    }

    /**
     * Handle remote exception
     */
    private void handleRemoteException(RemoteException ex, String message) {
        LOGGER.log(Level.SEVERE, message, ex);
        Config.PaneRMIError();
    }

    // IRemoteClient interface implementation

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void syncCanvas(ISyncData data) throws RemoteException {
        drawPanel.syncCanvas(data);
    }

    @Override
    public void syncMessage(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }

    @Override
    public void requestExit(String managerName) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Manager (" + managerName + ") has closed your access. ❌ " +
                    "The whiteboard will close", "Message from Manager", JOptionPane.WARNING_MESSAGE);
            frame.dispose();
            System.exit(0);
        });
    }

    @Override
    public void syncUserList(DefaultListModel<String> tempModel) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (int i = 0; i < tempModel.getSize(); i++) {
                userModel.addElement(tempModel.getElementAt(i));
            }
            userList.revalidate();
            userList.repaint();
        });
    }

    @Override
    public void triggerListRefresh() throws RemoteException {
        remoteServer.refreshUserList();
    }

    @Override
    public void notifySystemJoin() throws IOException {
        remoteServer.broadcastMessage(SYSTEM_MESSAGE_PREFIX + username + " has joined");
    }

    @Override
    public void clearCanvasRequest() throws RemoteException {
        drawPanel.newCanvas();
    }

    @Override
    public void loadCanvasImage(byte[] imageData) throws IOException {
        drawPanel.getCanvasFromServer(imageData);
    }

    @Override
    public void requestCanvasClose() throws RemoteException {
        int answer = JOptionPane.showConfirmDialog(frame,
                "Canvas closed by manager. Reconnect?", "Warning",
                JOptionPane.YES_NO_OPTION);
                
        if (answer == JOptionPane.YES_OPTION) {
            handleReconnectAttempt();
        } else {
            closeApplication();
        }
    }
    
    /**
     * Handle reconnect attempt
     */
    private void handleReconnectAttempt() {
        Thread reconnectThread = new Thread(() -> {
            try {
                while (remoteServer.isWhiteboardClosed()) {
                    Object[] options = {"Retry", "Close"};
                    int result = JOptionPane.showOptionDialog(frame,
                            "No new file has been opened by the manager.",
                            "Message",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                            
                    if (result != JOptionPane.YES_OPTION) {
                        closeApplication();
                        break;
                    }
                    
                    // Wait before retrying
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (RemoteException e) {
                handleRemoteException(e, "Error occurred during reconnection");
                closeApplication();
            }
        });
        
        reconnectThread.setDaemon(true);
        reconnectThread.start();
    }
    
    /**
     * Close application
     */
    private void closeApplication() {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            System.exit(0);
        });
    }

    @Override
    public boolean isRoomClosed() throws RemoteException {
        return drawPanel.isClosedState();
    }
}
