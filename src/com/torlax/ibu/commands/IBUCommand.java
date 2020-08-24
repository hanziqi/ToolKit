package com.torlax.ibu.commands;

import com.intellij.openapi.project.Project;

public abstract class IBUCommand implements ICommand {
    private Project project;

    public IBUCommand(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }
}
