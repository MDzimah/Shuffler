package View;
import javax.swing.*;
import java.awt.*;

import Controller.Controller;

public class MainWindow extends JFrame {
    private Controller c;
    public MainWindow(Controller c) {
        super("Sentence Shuffler");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.c = c;
        this.initComps();
        this.setVisible(true);
    }
    
    private void initComps() {
        //Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        JPanel display = new JPanel(new BorderLayout());
        mainPanel.add(display, BorderLayout.CENTER);

        ControlPanel p = new ControlPanel(c, display);
        mainPanel.add(p, BorderLayout.NORTH);

        this.setResizable(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int)(screenSize.width/2), (int)(screenSize.height/1.5)); //For when the screen is dragged or full screen mode is disabled
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //Screen initially extends to the whole screen size
        this.setVisible(true);
    }
}