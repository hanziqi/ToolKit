package com.trip.ibu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ListenerUtil;
import com.trip.ibu.utils.IBUToolKitUtil;
import com.trip.ibu.utils.ShellUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class IBUToolKitAction extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e == null) {
            reportNull(0);
        }
        Project project = e.getData(PlatformDataKeys.PROJECT);
        boolean inIBUAndroid = IBUToolKitUtil.checkInIBUAndroid(project);
        if (!inIBUAndroid) {
            Messages.showErrorDialog("This plugin is only allowed to use in IBUAndroid Project", "Warning");
            return;
        }
        JFrame jFrame = new JFrame();
        IBUToolKitForm toolKitForm = new IBUToolKitForm(e, project, jFrame);
        jFrame.setContentPane(toolKitForm.getMainPanel());
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ShellUtil.destroyAllProcess();
            }
        });
        ListenerUtil.addKeyListener(jFrame, new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    jFrame.dispose();
                }
            }
        });
    }

    private static void reportNull(int n) {
        throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "e", "com/trip/com.trip.ibu/IBUToolKitAction", "actionPerformed"));
    }
}
