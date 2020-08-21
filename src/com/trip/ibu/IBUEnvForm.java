package com.trip.ibu;

import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class IBUEnvForm {
    private JPanel mainPane;
    private JRadioButton FAT;
    private JRadioButton UAT;
    private JRadioButton PRD;
    private JTextField hybridInput;
    private JPanel hybridPane;
    private JButton confirmButton;
    private ENV env;

    public IBUEnvForm(OnConfirmListener onConfirmListener) {
        $$$setupUI$$$();
        FAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UAT.setSelected(false);
                PRD.setSelected(false);
                if (FAT.isSelected()) {
                    env = ENV.FAT;
                } else {
                    env = null;
                }
            }
        });
        UAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FAT.setSelected(false);
                PRD.setSelected(false);
                if (UAT.isSelected()) {
                    env = ENV.UAT;
                } else {
                    env = null;
                }
            }
        });
        PRD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UAT.setSelected(false);
                FAT.setSelected(false);
                if (PRD.isSelected()) {
                    env = ENV.PRD;
                } else {
                    env = null;
                }
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (env == null) {
                    Messages.showErrorDialog("Please choose at lease one environment", "Warning");
                } else {
                    onConfirmListener.onConfirm(env, hybridInput.getText().trim());
                }
            }
        });
    }

    public Container getMainPanel() {
        return mainPane;
    }

    private /* synthetic */ void $$$setupUI$$$() {
        JPanel mainPane = new JPanel();
        (mainPane = mainPane).setLayout(new GridLayoutManager(3, 1, new Insets(8, 8, 8, 8), -1, -1, false, false));
        JPanel comp = new JPanel();
        comp.setLayout(new FlowLayout(0, 0, 0));
        mainPane.add(comp, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JRadioButton radioButton = new JRadioButton();
        (FAT = radioButton).setText("FAT");
        comp.add(radioButton);
        JRadioButton radioButton2 = new JRadioButton();
        (UAT = radioButton2).setText("UAT");
        comp.add(radioButton2);
        JRadioButton radioButton3 = new JRadioButton();
        (PRD = radioButton3).setText("PRD");
        comp.add(radioButton3);
        JLabel comp2 = new JLabel();
        comp2.setHorizontalAlignment(2);
        comp2.setHorizontalTextPosition(2);
        comp2.setText(" hybridUrl: (Pull latest when not specify)");
        mainPane.add(comp2, new GridConstraints(1, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        JPanel panel = new JPanel();
        (hybridPane = panel).setLayout(new FlowLayout(0, 0, 0));
        mainPane.add(panel, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JTextField textField = new JTextField();
        (hybridInput = textField).setPreferredSize(new Dimension(300, 30));
        textField.setToolTipText("Pull latest when not specify");
        panel.add(textField);
        JButton button = new JButton();
        (confirmButton = button).setText("Confirm");
        panel.add(button);
    }

    public enum ENV {
        FAT,
        UAT,
        PRD;
    }

    public interface OnConfirmListener {
        void onConfirm(ENV p0, String p1);
    }
}