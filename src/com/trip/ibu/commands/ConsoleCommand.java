package com.trip.ibu.commands;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.trip.ibu.utils.ShellUtil;

import org.jetbrains.annotations.NotNull;

public abstract class ConsoleCommand extends IBUCommand {
    protected ConsoleView terminal;

    public ConsoleCommand(Project project, ConsoleView terminal) {
        super(project);
        terminal = terminal;
    }

    protected abstract String getCommand();

    protected void onSuccess() {
    }

    protected void onFail() {
    }

    @Override
    public void run() {
        String cmd = getCommand();
        if (cmd == null || cmd.isEmpty()) {
            return;
        }
        runCmd(cmd);
    }

    protected void runCmd(String cmd) {
        terminal.print("Running command: " + cmd + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        ProcessHandler handler = ShellUtil.runCmd(getProject().getBasePath(), cmd, new ProcessAdapter() {
            public void startNotified(@NotNull ProcessEvent event) {
                if (event == null) {
                    $$$reportNull$$$0(0);
                }
                super.startNotified(event);
            }

            public void processTerminated(@NotNull ProcessEvent event) {
                if (event == null) {
                    $$$reportNull$$$0(1);
                }
                super.processTerminated(event);
                ShellUtil.removeHandler(event.getProcessHandler());
                int code = event.getExitCode();
                if (code == 0) {
                    terminal.print("Run command successfully!\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                    onSuccess();
                } else {
                    terminal.print("Run command failed!\n", ConsoleViewContentType.LOG_ERROR_OUTPUT);
                    onFail();
                }
            }

            private static /* synthetic */ void $$$reportNull$$$0(int n) {
                String format = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                Object[] args = {"event", "com/trip/com.trip.ibu/commands/ConsoleCommand$1", null};
                switch (n) {
                    default: {
                        args[2] = "startNotified";
                        break;
                    }
                    case 1: {
                        args[2] = "processTerminated";
                        break;
                    }
                }
                throw new IllegalArgumentException(String.format(format, args));
            }
        });
        terminal.attachToProcess(handler);
    }
}
