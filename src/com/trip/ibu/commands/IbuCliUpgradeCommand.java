package com.trip.ibu.commands;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.trip.ibu.utils.IBUToolKitUtil;

import javax.swing.*;

public class IbuCliUpgradeCommand extends ConsoleCommand {
    private JLabel versionNumber;
    private JLabel hasNew;

    public IbuCliUpgradeCommand(Project project, ConsoleView terminal, JLabel versionNumber, JLabel hasNew) {
        super(project, terminal);
        this.versionNumber = versionNumber;
        this.hasNew = hasNew;
    }

    @Override
    protected String getCommand() {
        return "/usr/local/bin/com.trip.ibu-cli upgrade";
    }

    @Override
    protected void onSuccess() {
        super.onSuccess();
        IBUToolKitUtil.getIbuCliVersion(getProject(), new IBUToolKitUtil.OnVersionListener() {
            @Override
            public void onVersionGet(String version) {
                versionNumber.setText(version);
            }

            @Override
            public void hasNewVersion(boolean has) {
                hasNew.setVisible(has);
            }
        });
    }

    @Override
    protected void onFail() {
        super.onFail();
    }
}
