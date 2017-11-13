package uk.co.mhr.filemover.app;

import javax.swing.JApplet;

import uk.co.mhr.filemover.gui.MainWindow;

/**
 * The main driver class for the FileMover app.
 * @author Jonathan Kilgallon
 * @since Version 0.2.0
 */
public class FileMoverApp extends JApplet {

    private static final long serialVersionUID = 25381584487362L;

    public static void main(final String[] args) {
        new FileMoverApp().displayGUI();
    }

    private void displayGUI() {
        new MainWindow();
    }

}
