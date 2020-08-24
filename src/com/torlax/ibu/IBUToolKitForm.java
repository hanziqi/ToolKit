package com.torlax.ibu;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.torlax.ibu.commands.CargoDBCommand;
import com.torlax.ibu.commands.CleanProjectCommand;
import com.torlax.ibu.commands.FoxPageCommand;
import com.torlax.ibu.commands.IbuCliUpgradeCommand;
import com.torlax.ibu.commands.RNPackageDownload;
import com.torlax.ibu.utils.BundleListCellRenderer;
import com.torlax.ibu.utils.IBUToolKitUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.*;

public class IBUToolKitForm {
    private JPanel terminalPane, mainPanel;
    private ComboBox cbRn, cbShark, cbCargo, cbFoxPage;
    private JButton btnUpgrade, btnSave, btnSaveAndSync, btnSaveSettings, btnSaveSettingAndSync,
            btnPullRN, btnClean, btnPullShark, btnPullCargo, btbPullFoxPage;
    private ConsoleView terminal;
    private JList excludeList, aarList, sourceList;
    private JLabel lbVersionNumber, lbHasNew, lbPrjVersion, lbPrjHasNew, lbPrjNewTip;

    @SuppressWarnings("unchecked")
    public IBUToolKitForm(AnActionEvent anActionEvent, Project project, JFrame jFrame) {
        setupUI();
        ListTransferHandler listTransferHandler = new ListTransferHandler();
        IBUToolKitUtil.Bundles bundles = IBUToolKitUtil.getAllBundles(project);
        Set<String> excludeBundles = bundles.exclude;
        Set<String> aarBundles = bundles.aar;
        Set<String> sourceBundles = bundles.source;

        DefaultListModel<String> excludeModel = new DefaultListModel<>();
        for (String s : excludeBundles) {
            excludeModel.addElement(s);
        }
        excludeList.setModel(excludeModel);
        excludeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        excludeList.setDropMode(DropMode.INSERT);
        excludeList.setDragEnabled(true);
        excludeList.setCellRenderer(new BundleListCellRenderer());
        excludeList.setTransferHandler(listTransferHandler);

        DefaultListModel<String> aarModel = new DefaultListModel<>();
        for (String s2 : aarBundles) {
            aarModel.addElement(s2);
        }
        aarList.setModel(aarModel);
        aarList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        aarList.setDropMode(DropMode.INSERT);
        aarList.setDragEnabled(true);
        aarList.setCellRenderer(new BundleListCellRenderer());
        aarList.setTransferHandler(listTransferHandler);

        DefaultListModel<String> sourceModel = new DefaultListModel<>();
        for (String s3 : sourceBundles) {
            sourceModel.addElement(s3);
        }
        sourceList.setModel(sourceModel);
        sourceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sourceList.setDropMode(DropMode.INSERT);
        sourceList.setDragEnabled(true);
        sourceList.setCellRenderer(new BundleListCellRenderer());
        sourceList.setTransferHandler(listTransferHandler);

        btnSave.addActionListener(e -> IBUToolKitUtil.saveBundles(project, excludeList.getModel(), sourceList.getModel()));
        btnSaveAndSync.addActionListener(e -> {
            IBUToolKitUtil.saveBundles(project, excludeList.getModel(), sourceList.getModel());
            IBUToolKitUtil.sync(anActionEvent);
            IBUToolKitUtil.close(jFrame);
        });

        terminal = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        terminal.getComponent().setSize(terminalPane.getMinimumSize().width, 300);
        terminal.getComponent().setVisible(true);
        terminalPane.add(terminal.getComponent());
        ComboConfig sharkConfig = new ComboConfig(cbShark, btnPullShark, "enableSharkDBAutoDownload", e -> {
            IBUToolKitUtil.saveSharkSettings(project, (String) cbShark.getSelectedItem());
            IBUToolKitUtil.sync(anActionEvent);
        });
        ComboConfig cargoConfig = new ComboConfig(cbCargo, btnPullCargo, "enableCargoDBAutoDownload", e -> new CargoDBCommand(project, terminal).run());
        ComboConfig foxPageConfig = new ComboConfig(cbFoxPage, btbPullFoxPage, "enableFoxPagePackaged", e -> new FoxPageCommand(project, terminal).run());
        ComboConfig rnConfig = new ComboConfig(cbRn, btnPullRN, "alwaysDownHybrid", e -> new RNPackageDownload(project, terminal).run());
        List<ComboConfig> configList = Arrays.asList(sharkConfig, cargoConfig, foxPageConfig, rnConfig);
        IBUToolKitUtil.setUpAutoDownload(project, configList);
        btnSaveSettings.addActionListener(e -> IBUToolKitUtil.saveSettings(project, configList));
        btnSaveSettingAndSync.addActionListener(e -> {
            IBUToolKitUtil.saveSettings(project, configList);
            IBUToolKitUtil.sync(anActionEvent);
            IBUToolKitUtil.close(jFrame);
        });
        IBUToolKitUtil.getIbuCliVersion(project, new IBUToolKitUtil.OnVersionListener() {
            @Override
            public void onVersionGet(String version) {
                lbVersionNumber.setText(version);
            }

            @Override
            public void hasNewVersion(boolean has) {
                lbHasNew.setVisible(has);
            }
        });
        btnUpgrade.addActionListener(e -> new IbuCliUpgradeCommand(project, terminal, lbVersionNumber, lbHasNew).run());
        btnClean.addActionListener(e -> {
            CleanProjectCommand cleanProjectCommand = new CleanProjectCommand(project, terminal);
            cleanProjectCommand.run();
        });
        lbPrjVersion.setText(IBUToolKitUtil.getPluginVersionStr());
        lbPrjHasNew.setVisible(IBUToolKitUtil.getPluginVersion() < IBUToolKitUtil.getPluginLatestVersion(project));
        lbPrjNewTip.setVisible(IBUToolKitUtil.getPluginVersion() < IBUToolKitUtil.getPluginLatestVersion(project));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel getBundleConfigPanel() {
        JPanel bundleConfigPanel = new JPanel();
        bundleConfigPanel.setLayout(new GridLayoutManager(2, 1, JBUI.insets(8), -1, -1, false, false));
        bundleConfigPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JBScrollPane comp2 = new JBScrollPane();
        comp2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        bundleConfigPanel.add(comp2, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));

        JPanel viewportView = new JPanel();
        viewportView.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, true));
        viewportView.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        // 不参与编译
        JBScrollPane unCompiledPane = new JBScrollPane();
        unCompiledPane.setBorder(BorderFactory.createTitledBorder(null, "不参与编译", 0, 0, null, null));
        unCompiledPane.setViewportView(excludeList = new JList());
        viewportView.add(unCompiledPane, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 7, null, null, null));
        // AAR编译
        JBScrollPane aarCompilePane = new JBScrollPane();
        aarCompilePane.setBorder(BorderFactory.createTitledBorder(null, "AAR编译", 0, 0, null, null));
        aarCompilePane.setViewportView(aarList = new JList());
        viewportView.add(aarCompilePane, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 7, null, null, null));
        // 源码编译
        JBScrollPane sourceCompile = new JBScrollPane();
        sourceCompile.setBorder(BorderFactory.createTitledBorder(null, "源码编译", 0, 0, null, null));
        sourceCompile.setViewportView(sourceList = new JList());
        viewportView.add(sourceCompile, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 7, null, null, null));

        comp2.setViewportView(viewportView);
        JPanel savePane = new JPanel();
        savePane.setLayout(new FlowLayout(2, 5, 5));
        bundleConfigPanel.add(savePane, new GridConstraints(1, 0, 1, 1, 2, 1, 3, 3, null, null, null));
        btnSave = new JButton();
        btnSave.setText("Save");
        savePane.add(btnSave);
        btnSaveAndSync = new JButton();
        btnSaveAndSync.setText("Save and Sync");
        savePane.add(btnSaveAndSync);
        return bundleConfigPanel;
    }

    private JPanel getToolsPanel() {
        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new GridLayoutManager(4, 1, JBUI.insets(8), -1, -1, false, false));
        toolsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));

        JPanel autoDownloadPane = new JPanel();
        autoDownloadPane.setLayout(new GridLayoutManager(3, 2, JBUI.insets(8), -1, -1, false, false));
        autoDownloadPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Auto Download (Disable to speed up compilation)", 0, 0, null, null));
        JPanel sharkDBPanel = new JPanel();
        sharkDBPanel.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, true));
        // label
        JLabel lbShark = new JLabel();
        lbShark.setHorizontalAlignment(SwingConstants.LEFT);
        lbShark.setHorizontalTextPosition(SwingConstants.LEFT);
        lbShark.setText("Shark DB:");
        lbShark.setToolTipText("");
        lbShark.setVerticalAlignment(SwingConstants.CENTER);
        lbShark.setVerticalTextPosition(SwingConstants.CENTER);
        sharkDBPanel.add(lbShark, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        // comboBox
        cbShark = new ComboBox();
        cbShark.setMaximumSize(new Dimension(32767, 32767));
        cbShark.setPreferredSize(new Dimension(80, 30));
        sharkDBPanel.add(cbShark, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        // btn
        btnPullShark = new JButton();
        btnPullShark.setText("Fetch");
        btnPullShark.setToolTipText("");
        sharkDBPanel.add(btnPullShark, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        autoDownloadPane.add(sharkDBPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));

        JPanel cargoPanel = new JPanel();
        cargoPanel.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        // label
        JLabel lbCargo = new JLabel();
        lbCargo.setText("Cargo DB:");
        lbCargo.setToolTipText("");
        cargoPanel.add(lbCargo, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        // comboBox
        cbCargo = new ComboBox();
        cbCargo.setPreferredSize(new Dimension(80, 30));
        cargoPanel.add(cbCargo, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        // btn
        btnPullCargo = new JButton();
        btnPullCargo.setText("Fetch");
        btnPullCargo.setToolTipText("");
        cargoPanel.add(btnPullCargo, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        autoDownloadPane.add(cargoPanel, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null));

        JPanel foxPagePanel = new JPanel();
        foxPagePanel.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        autoDownloadPane.add(foxPagePanel, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // label
        JLabel lbFoxPage = new JLabel();
        lbFoxPage.setText("FoxPage Package:");
        foxPagePanel.add(lbFoxPage, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        // comboBox
        cbFoxPage = new ComboBox();
        cbFoxPage.setPreferredSize(new Dimension(80, 30));
        foxPagePanel.add(cbFoxPage, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        // btn
        btbPullFoxPage = new JButton();
        btbPullFoxPage.setText("Fetch");
        foxPagePanel.add(btbPullFoxPage, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));

        JPanel rnPackagePanel = new JPanel();
        rnPackagePanel.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        autoDownloadPane.add(rnPackagePanel, new GridConstraints(1, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        // label
        JLabel lbRn = new JLabel();
        lbRn.setText("RN Package:");
        rnPackagePanel.add(lbRn, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        // comboBox
        cbRn = new ComboBox();
        cbRn.setPreferredSize(new Dimension(80, 30));
        rnPackagePanel.add(cbRn, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        // btn
        btnPullRN = new JButton();
        btnPullRN.setText("Fetch");
        rnPackagePanel.add(btnPullRN, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        toolsPanel.add(autoDownloadPane, new GridConstraints(0, 0, 1, 1, 9, 0, 3, 3, null, null, null));
        // save
        btnSaveSettings = new JButton();
        btnSaveSettings.setText("Save");
        autoDownloadPane.add(btnSaveSettings, new GridConstraints(2, 0, 1, 1, 0, 1, 3, 0, null, null, null));
        // Save and Sync
        btnSaveSettingAndSync = new JButton();
        btnSaveSettingAndSync.setHorizontalTextPosition(0);
        btnSaveSettingAndSync.setOpaque(true);
        btnSaveSettingAndSync.setText("Save and Sync");
        autoDownloadPane.add(btnSaveSettingAndSync, new GridConstraints(2, 1, 1, 1, 0, 3, 3, 3, null, null, null));

        JPanel cliPanel = new JPanel();
        cliPanel.setLayout(new GridLayoutManager(2, 1, JBUI.emptyInsets(), -1, -1, false, false));
        cliPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "IBU-CLI", 0, 0, null, null));
        JPanel comp17 = new JPanel();
        comp17.setLayout(new FlowLayout(0, 5, 5));
        comp17.setAlignmentX(0.5f);
        comp17.setAutoscrolls(false);
        cliPanel.add(comp17, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        toolsPanel.add(cliPanel, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // version
        JLabel lbVersion = new JLabel();
        lbVersion.setText("Version:");
        comp17.add(lbVersion);
        lbVersionNumber = new JLabel();
        lbVersionNumber.setText("loading...");
        comp17.add(lbVersionNumber);
        lbHasNew = new JLabel();
        Font font = getFont(null, -1, 10, lbHasNew.getFont());
        if (font != null) {
            lbHasNew.setFont(font);
        }
        lbHasNew.setForeground(new Color(-393216));
        lbHasNew.setHorizontalAlignment(SwingConstants.LEFT);
        lbHasNew.setHorizontalTextPosition(SwingConstants.LEFT);
        lbHasNew.setText("new");
        lbHasNew.setVerticalAlignment(SwingConstants.BOTTOM);
        lbHasNew.setVerticalTextPosition(SwingConstants.BOTTOM);
        lbHasNew.setVisible(false);
        comp17.add(lbHasNew);
        JPanel comp19 = new JPanel();
        comp19.setLayout(new FlowLayout(0, 0, 0));
        cliPanel.add(comp19, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        btnUpgrade = new JButton();
        btnUpgrade.setLabel("Upgrade");
        btnUpgrade.setText("Upgrade");
        comp19.add(btnUpgrade);
        JPanel comp20 = new JPanel();
        comp20.setLayout(new GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1, false, false));
        toolsPanel.add(comp20, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        comp20.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Other Tools", 0, 0, null, null));
        btnClean = new JButton();
        btnClean.setMargin(JBUI.emptyInsets());
        btnClean.setText("Clean Build Directories");
        btnClean.setToolTipText("Clean All build directories");
        comp20.add(btnClean, new GridConstraints(0, 0, 1, 1, 9, 0, 3, 0, null, null, null));
        JBScrollPane comp21 = new JBScrollPane();
        toolsPanel.add(comp21, new GridConstraints(3, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        terminalPane = new JPanel();
        terminalPane.setLayout(new BorderLayout(0, 0));
        terminalPane.setMinimumSize(new Dimension(-1, 300));
        terminalPane.setPreferredSize(new Dimension(0, 300));
        comp21.setViewportView(terminalPane);
        return toolsPanel;
    }

    private JPanel getVersionInfo() {
        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new FlowLayout(2, 5, 5));
        JLabel lbVersionTitle = new JLabel();
        lbVersionTitle.setText("Version:");
        versionPanel.add(lbVersionTitle);

        lbPrjVersion = new JLabel();
        lbPrjVersion.setText("loading...");
        versionPanel.add(lbPrjVersion);

        lbPrjHasNew = new JLabel();
        Font font = getFont(null, -1, 10, lbPrjHasNew.getFont());
        if (font != null) {
            lbPrjHasNew.setFont(font);
        }
        lbPrjHasNew.setForeground(new Color(-393216));
        lbPrjHasNew.setText("new");
        lbPrjHasNew.setVisible(false);
        versionPanel.add(lbPrjHasNew);

        lbPrjNewTip = new JLabel();
        lbPrjNewTip.setText("(可直接安装tools/IBUToolKit.jar)");
        versionPanel.add(lbPrjNewTip);
        return versionPanel;
    }

    private void setupUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, JBUI.insets(8), -1, -1, false, false));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        tabbedPane.addTab("Bundle Config", null, getBundleConfigPanel(), null);
        tabbedPane.addTab("Tools", null, getToolsPanel(), null);
        mainPanel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        mainPanel.add(getVersionInfo(), new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
    }

    private Font getFont(String name, int n, int n2, Font font) {
        if (font == null) {
            return null;
        }
        String name2;
        if (name == null) {
            name2 = font.getName();
        } else {
            Font font2 = new Font(name, 0, 10);
            if (font2.canDisplay('a') && font2.canDisplay('1')) {
                name2 = name;
            } else {
                name2 = font.getName();
            }
        }
        return new Font(name2, (n >= 0) ? n : font.getStyle(), (n2 >= 0) ? n2 : font.getSize());
    }
}
