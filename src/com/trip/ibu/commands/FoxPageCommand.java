package com.trip.ibu.commands;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.trip.ibu.utils.IBUToolKitUtil;

public class FoxPageCommand extends ConsoleCommand {
    public FoxPageCommand(Project project, ConsoleView terminal) {
        super(project, terminal);
    }

    @Override
    protected String getCommand() {
        String path = getProject().getBasePath();
        String version = IBUToolKitUtil.getIBUVersion(getProject());
        return String.format("/usr/bin/python %s/script/FoxPagePackage.py 37001 %s prd %s/ibu_main/src/main/assets/foxpage", path, version, path);
    }
}
