package Launcher;

import java.util.Locale;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatIntelliJLaf;

import Controller.Controller;
import Model.Model;
import View.MainWindow;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
		FlatIntelliJLaf.setup();
        Model m = new Model();
        Controller c = new Controller(m);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow(c);
            }
        });
    }
}
