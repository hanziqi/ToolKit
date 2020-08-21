package com.trip.ibu.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellUtil {
    public static List<ProcessHandler> sProcessHandlers;

    public static void destroyAllProcess() {
        for (ProcessHandler processHandler : ShellUtil.sProcessHandlers) {
            processHandler.destroyProcess();
        }
    }

    public static void removeHandler(ProcessHandler processHandler) {
        ShellUtil.sProcessHandlers.remove(processHandler);
    }

    public static ProcessHandler runCmd(String basePath, String command, ProcessAdapter adapter) {
        GeneralCommandLine generalCommandLine = new GeneralCommandLine(new String[]{"bash", "-c", command});
        generalCommandLine.setWorkDirectory(new File(basePath));
        generalCommandLine.setRedirectErrorStream(true);
        ProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(generalCommandLine);
            ShellUtil.sProcessHandlers.add(processHandler);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assert processHandler != null;
        processHandler.addProcessListener(adapter);
        processHandler.startNotify();
        return processHandler;
    }

    public static String getCmdOutput(String path, String cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(path));
        processBuilder.command("bash", "-c", cmd);
        String PATH = processBuilder.environment().get("PATH");
        PATH += ":/usr/local/bin";
        processBuilder.environment().put("PATH", PATH);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        ShellUtil.sProcessHandlers = new ArrayList<>();
    }
}
