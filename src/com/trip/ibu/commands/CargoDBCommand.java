package com.trip.ibu.commands;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.trip.ibu.utils.IBUToolKitUtil;

public class CargoDBCommand extends ConsoleCommand {
    public CargoDBCommand(Project project, ConsoleView terminal) {
        super(project, terminal);
    }

    @Override
    protected String getCommand() {
        String path = getProject().getBasePath();
        String version = IBUToolKitUtil.getIBUVersion(getProject());
        return String.format("/usr/bin/python %s/script/CargoDBPackage.py 37 ANDROID %s %s/ibu_main/src/main/assets/cargo", path, version, path);
    }
}
