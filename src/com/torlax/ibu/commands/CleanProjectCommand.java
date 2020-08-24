package com.torlax.ibu.commands;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;

public class CleanProjectCommand extends ConsoleCommand {
    public CleanProjectCommand(Project project, ConsoleView terminal) {
        super(project, terminal);
    }

    @Override
    protected String getCommand() {
        return "find . -name build -prune -exec rm -rf {} \\;";
    }

    @Override
    protected void onSuccess() {
        super.onSuccess();
        FileDocumentManager.getInstance().saveAllDocuments();
        SaveAndSyncHandler.getInstance().refreshOpenFiles();
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
}