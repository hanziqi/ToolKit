package com.torlax.ibu.commands;

import java.awt.Window;

import com.torlax.ibu.IBUEnvForm;
import com.torlax.ibu.utils.IBUToolKitUtil;
import javax.swing.JFrame;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

public class RNPackageDownload extends ConsoleCommand
{
    public RNPackageDownload(final Project project, final ConsoleView terminal) {
        super(project, terminal);
    }

    @Override
    protected String getCommand() {
        return null;
    }

    @Override
    public void run() {
        final JFrame jFrame = new JFrame();
        final Window window;
        final String path;
        final String version;
        String rnEnv;
        final Object[] args;
        String string;
        final Object o;
        final String format;
        final String cmd;
        final IBUEnvForm envForm = new IBUEnvForm((env, hybridUrl) -> {
            window.setVisible(false);
            window.dispose();
            path = this.getProject().getBasePath();
            version = IBUToolKitUtil.getIBUVersion(this.getProject());
            if (env == IBUEnvForm.ENV.FAT) {
                rnEnv = "FAT";
            }
            else if (env == IBUEnvForm.ENV.UAT) {
                rnEnv = "UAT";
            }
            else {
                rnEnv = "Product";
            }
            args = new Object[] { path, version, rnEnv, path, null };
            if (hybridUrl.isEmpty()) {
                string = "";
            }
            else {
                string = " " + hybridUrl;
            }
            args[o] = string;
            cmd = String.format(format, args);
            this.runCmd(cmd);
            return;
        });
        jFrame.setContentPane(envForm.getMainPanel());
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}
