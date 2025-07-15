package src.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {
    private static JFrame mainWindow;
    private static GamePanel gamePanel;
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main window once
            mainWindow = new JFrame();
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainWindow.setResizable(false);
            mainWindow.setTitle("Whisky Way");

            // Show start screen initially
            showStartScreen();
        });
    }

    private static void showStartScreen() {
        GamePanel.backgroundMusic();
        StartScreenPanel startPanel = new StartScreenPanel();
        mainWindow.getContentPane().removeAll();
        mainWindow.add(startPanel);
        mainWindow.pack();
        // mainWindow.setSize(gameSize());
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    public static void startGame() {
        GamePanel.stopMusic();
        mainWindow.getContentPane().removeAll();
        
        gamePanel = new GamePanel();
        mainWindow.add(gamePanel);
        mainWindow.pack();
        
        gamePanel.setupGame();
        gamePanel.startGameThread();
    }
}

class StartScreenPanel extends JPanel {
    public StartScreenPanel() {
        setLayout(new BorderLayout());
        
        // Load background image
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/res/background&button/startgame1.png"));
        Image img = backgroundIcon.getImage();

        // Calculate scaled dimensions
        Dimension gameSize = Toolkit.getDefaultToolkit().getScreenSize();
        int originalWidth = backgroundIcon.getIconWidth();
        int originalHeight = backgroundIcon.getIconHeight();
        double aspectRatio = (double)originalWidth/originalHeight;

        int newWidth, newHeight;
        if (gameSize.width/aspectRatio <= gameSize.height) {
            newWidth = gameSize.width;
            newHeight = (int)(newWidth/aspectRatio);
        } else {
            newHeight = gameSize.height;
            newWidth = (int)(newHeight*aspectRatio);
        }

        Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        backgroundIcon = new ImageIcon(scaledImg);
        JLabel background = new JLabel(backgroundIcon);
        background.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(gameSize);
        
        // Add background
        background.setBounds(0, 0, newWidth, newHeight);
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        
        // Load and scale title image
        ImageIcon titleIcon = new ImageIcon(getClass().getResource("/res/background&button/titlewhiskyway.png"));
        int titleWidth = (int)(gameSize.width * 0.5);
        int titleHeight = (int)(titleWidth * ((double)titleIcon.getIconHeight()/titleIcon.getIconWidth()));
        Image titleImg = titleIcon.getImage().getScaledInstance(titleWidth, titleHeight, Image.SCALE_SMOOTH);
        titleIcon = new ImageIcon(titleImg);
        JLabel titleLabel = new JLabel(titleIcon);

        // Create start button
        JButton startButton = createButton(
            "/res/background&button/startb.png",
            "/res/background&button/bhover.png",
            200,
            150
        );
        startButton.addActionListener(e -> Main.startGame());
        
        // Layout components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 30, 10);
        
        buttonPanel.add(startButton, gbc);
        
        // Position panels
        buttonPanel.setBounds(0, 130, gameSize.width, gameSize.height);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.add(titleLabel, gbc);
        contentPanel.setBounds(0, -70, gameSize.width, gameSize.height);
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);
        
        add(layeredPane);
    }
    
    private JButton createButton(String normalImgPath, String hoverImgPath, int width, int height) {
        ImageIcon normalIcon = new ImageIcon(getClass().getResource(normalImgPath));
        Image normalImage = normalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        normalIcon = new ImageIcon(normalImage);

        ImageIcon hoverIcon = new ImageIcon(getClass().getResource(hoverImgPath));
        Image hoverImage = hoverIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        hoverIcon = new ImageIcon(hoverImage);
        
        JButton button = new JButton(normalIcon);
        button.setRolloverIcon(hoverIcon);
        button.setPressedIcon(hoverIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
}