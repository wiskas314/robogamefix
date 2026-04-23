package gui;

import gui.state.ApplicationStateManager;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            ApplicationStateManager stateManager = new ApplicationStateManager();
            String savedTag = stateManager.getSavedLocaleTag();
            if (savedTag != null && !savedTag.isEmpty()) {
                Localization.getInstance().setLocaleFromTag(savedTag);
            }
            MainApplicationFrame frame = new MainApplicationFrame(stateManager);
            stateManager.register("locale", Localization.getInstance());
            frame.restoreAllStates();
            frame.setVisible(true);
        });
    }
}
