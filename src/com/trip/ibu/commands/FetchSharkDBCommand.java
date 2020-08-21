package com.trip.ibu.commands;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.trip.ibu.utils.IBUToolKitUtil;

public class FetchSharkDBCommand extends ConsoleCommand {
    public FetchSharkDBCommand(Project project, ConsoleView terminal) {
        super(project, terminal);
    }

    @Override
    protected String getCommand() {
        String path = getProject().getBasePath();
        String version = IBUToolKitUtil.getIBUVersion(getProject());
        String sharkVersion = IBUToolKitUtil.getSharkVersion(getProject());
        return String.format("/usr/bin/python %s/script/fetch_shark_db.py 37 en_US %s %s/ibu_main/src/main/assets/storage android FAT %s", path, version, path, sharkVersion);
    }
}
