package com.trip.ibu.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.trip.ibu.ComboConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.swing.*;

public class IBUToolKitUtil {
    public static void setUpAutoDownload(Project project, List<ComboConfig> comboConfigList) {
        Properties prop = new Properties();
        try {
            prop.load(FileUtils.openInputStream(new File(project.getBasePath() + "/local.properties")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (ComboConfig comboConfig : comboConfigList) {
            String result = prop.getProperty(comboConfig.getProperty());
            boolean enable = "true".equalsIgnoreCase(result) || (!"false".equalsIgnoreCase(result) && !"alwaysDownHybrid".equalsIgnoreCase(comboConfig.getProperty()));
            comboConfig.getComboBox().addItem("Enable");
            comboConfig.getComboBox().addItem("Disable");
            comboConfig.getComboBox().setSelectedIndex(enable ? 0 : 1);
        }
    }

    public static void saveSettings(Project project, List<ComboConfig> comboConfigList) {
        try {
            Properties properties = new Properties();
            properties.load(FileUtils.openInputStream(new File(project.getBasePath() + "/local.properties")));
            for (ComboConfig comboConfig : comboConfigList) {
                String result = (String) comboConfig.getComboBox().getSelectedItem();
                properties.setProperty(comboConfig.getProperty(), "Disable".equalsIgnoreCase(result) ? "false" : "true");
            }
            properties.store(new FileOutputStream(project.getBasePath() + "/local.properties"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveSharkSettings(Project project, String result) {
        try {
            Properties properties = new Properties();
            result = ("Disable".equalsIgnoreCase(result) ? "false" : "true");
            properties.load(FileUtils.openInputStream(new File(project.getBasePath() + "/local.properties")));
            properties.setProperty("enableSharkDBAutoDownload", result);
            properties.store(new FileOutputStream(project.getBasePath() + "/local.properties"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveBundles(Project project, ListModel<String> exclude, ListModel<String> source) {
        try {
            Properties properties = new Properties();
            properties.load(FileUtils.openInputStream(new File(project.getBasePath() + "/local.properties")));
            StringBuilder excludeStr = new StringBuilder();
            StringBuilder sourceStr = new StringBuilder();
            for (int i = 0; i < exclude.getSize(); ++i) {
                if (i == exclude.getSize() - 1) {
                    excludeStr.append(exclude.getElementAt(i));
                } else {
                    excludeStr.append(exclude.getElementAt(i)).append(",");
                }
            }
            for (int i = 0; i < source.getSize(); ++i) {
                if (i == source.getSize() - 1) {
                    sourceStr.append(source.getElementAt(i));
                } else {
                    sourceStr.append(source.getElementAt(i)).append(",");
                }
            }
            properties.setProperty("excludeProject", excludeStr.toString());
            properties.setProperty("sourceProject", sourceStr.toString());
            properties.store(new FileOutputStream(project.getBasePath() + "/local.properties"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkInIBUAndroid(Project project) {
        File file = new File(project.getBasePath() + "/ibu_main");
        return file.exists();
    }

    public static void sync(AnActionEvent e) {
        FileDocumentManager.getInstance().saveAllDocuments();
        SaveAndSyncHandler.getInstance().refreshOpenFiles();
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        ActionManager am = ActionManager.getInstance();
        AnAction sync = am.getAction("Android.SyncProject");
        sync.actionPerformed(e);
    }

    public static void close(JFrame jFrame) {
        jFrame.dispose();
        jFrame.setVisible(false);
    }

    public static Bundles getAllBundles(Project project) {
        Bundles bds = new Bundles();
        try {
            FileInputStream fis = FileUtils.openInputStream(new File(project.getBasePath() + "/bundle_config.json"));
            String jsonTxt = IOUtils.toString(fis, StandardCharsets.UTF_8);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(jsonTxt);
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("allProjects");
            JsonArray jsonArray = jsonObject.getAsJsonArray("IBU_ALL");
            for (JsonElement element : jsonArray) {
                bds.aar.add(element.getAsJsonObject().get("name").getAsString());
            }
            Properties prop = new Properties();
            prop.load(FileUtils.openInputStream(new File(project.getBasePath() + "/local.properties")));
            String sourceStr = (String) prop.get("sourceProject");
            String excludeStr = (String) prop.get("excludeProject");
            if (!sourceStr.isEmpty()) {
                bds.source.addAll(newHashSet(sourceStr.split(",")));
            }
            if (!excludeStr.isEmpty()) {
                bds.exclude.addAll(newHashSet(excludeStr.split(",")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        bds.aar.removeAll(bds.exclude);
        bds.aar.removeAll(bds.source);
        return bds;
    }

    private static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static void getIbuCliVersion(Project project, OnVersionListener listener) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = ShellUtil.getCmdOutput(project.getBasePath(), "/usr/local/bin/com.trip.ibu-cli -v");
                    if (result.contains("\u53d1\u73b0\u65b0\u7248\u672c")) {
                        listener.hasNewVersion(true);
                        listener.onVersionGet(getVersionFromStr(result));
                    } else {
                        listener.hasNewVersion(false);
                        listener.onVersionGet(result.trim().replace(" ", ""));
                    }
                } catch (Exception e) {
                    listener.hasNewVersion(false);
                    listener.onVersionGet(null);
                }
            }
        });
    }

    private static String getVersionFromStr(String str) {
        String s = StringUtils.substringBetween(str, "\u53d1\u73b0\u65b0\u7248\u672c", "\u4f7f\u7528").trim().replace(" ", "");
        s = s.split("->")[0];
        return s;
    }

    public static String getSharkVersion(Project project) {
        Properties prop = new Properties();
        try {
            prop.load(FileUtils.openInputStream(new File(project.getBasePath() + "/third_party_lib_plugin/src/main/resources/version.properties")));
            return prop.getProperty("sharkSDKVersion");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getIBUVersion(Project project) {
        Properties prop = new Properties();
        try {
            prop.load(FileUtils.openInputStream(new File(project.getBasePath() + "/gradle.properties")));
            return prop.getProperty("ibuVersionName");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getPluginVersionStr() {
        try {
            return PluginManager.getPlugin(PluginId.getId("com.trip.com.trip.ibu.tool.kit")).getVersion();
        } catch (Exception e) {
            return "0.0.0";
        }
    }

    public static double getPluginVersion() {
        try {
            String version = PluginManager.getPlugin(PluginId.getId("com.trip.com.trip.ibu.tool.kit")).getVersion();
            String[] versionStr = version.split("\\.");
            return Integer.valueOf(versionStr[0]) * 100 + Integer.valueOf(versionStr[1]) + Double.valueOf(versionStr[2]) / 1000.0;
        } catch (Exception e) {
            return -1.0;
        }
    }

    public static double getPluginLatestVersion(Project project) {
        Properties prop = new Properties();
        try {
            prop.load(FileUtils.openInputStream(new File(project.getBasePath() + "/gradle.properties")));
            String[] versionStr = prop.getProperty("IBU_TOOL_KIT_VERSION").split("\\.");
            return Integer.valueOf(versionStr[0]) * 100 + Integer.valueOf(versionStr[1]) + Double.valueOf(versionStr[2]) / 1000.0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1.0;
        }
    }

    public static class Bundles {
        public Set<String> exclude;
        public Set<String> source;
        public Set<String> aar;

        public Bundles() {
            this.exclude = new HashSet<String>();
            this.source = new HashSet<String>();
            this.aar = new HashSet<String>();
        }
    }

    public interface OnVersionListener {
        void onVersionGet(String p0);

        void hasNewVersion(boolean p0);
    }
}
