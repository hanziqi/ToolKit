package com.trip.ibu;

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
import com.trip.ibu.commands.CargoDBCommand;
import com.trip.ibu.commands.CleanProjectCommand;
import com.trip.ibu.commands.FoxPageCommand;
import com.trip.ibu.commands.IbuCliUpgradeCommand;
import com.trip.ibu.commands.RNPackageDownload;
import com.trip.ibu.utils.BundleListCellRenderer;
import com.trip.ibu.utils.IBUToolKitUtil;

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

    public IBUToolKitForm(AnActionEvent anActionEvent, Project project, JFrame jFrame) {
        setupUI();
        ListTransferHandler listTransferHandler = new ListTransferHandler();
        IBUToolKitUtil.Bundles bundles = IBUToolKitUtil.getAllBundles(project);
        Set<String> excludeBundles = bundles.exclude;
        Set<String> aarBundles = bundles.aar;
        Set<String> sourceBundles = bundles.source;
        DefaultListModel<String> excludeModel = new DefaultListModel<>();
        BundleListCellRenderer excludeRender = new BundleListCellRenderer();
        BundleListCellRenderer aarRender = new BundleListCellRenderer();
        BundleListCellRenderer sourceRender = new BundleListCellRenderer();
        for (String s : excludeBundles) {
            excludeModel.addElement(s);
        }
        excludeList.setModel(excludeModel);
        excludeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        excludeList.setDropMode(DropMode.INSERT);
        excludeList.setDragEnabled(true);
        excludeList.setCellRenderer(excludeRender);
        excludeList.setTransferHandler(listTransferHandler);
        DefaultListModel<String> aarModel = new DefaultListModel<>();
        for (String s2 : aarBundles) {
            aarModel.addElement(s2);
        }
        aarList.setModel(aarModel);
        aarList.setSelectionMode(2);
        aarList.setDropMode(DropMode.INSERT);
        aarList.setDragEnabled(true);
        aarList.setCellRenderer(aarRender);
        aarList.setTransferHandler(listTransferHandler);
        DefaultListModel<String> sourceModel = new DefaultListModel<>();
        for (String s3 : sourceBundles) {
            sourceModel.addElement(s3);
        }
        sourceList.setModel(sourceModel);
        sourceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sourceList.setDropMode(DropMode.INSERT);
        sourceList.setDragEnabled(true);
        sourceList.setCellRenderer(sourceRender);
        sourceList.setTransferHandler(listTransferHandler);
        btnSave.addActionListener(e -> IBUToolKitUtil.saveBundles(project, excludeList.getModel(), sourceList.getModel()));
        btnSaveAndSync.addActionListener(e -> {
            IBUToolKitUtil.saveBundles(project, excludeList.getModel(), sourceList.getModel());
            IBUToolKitUtil.sync(anActionEvent);
            IBUToolKitUtil.close(jFrame);
            return;
        });
        terminal = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        terminal.getComponent().setSize(terminalPane.getMinimumSize().width, 300);
        terminal.getComponent().setVisible(true);
        terminalPane.add(terminal.getComponent());
        ComboConfig sharkConfig = new ComboConfig(cbShark, btnPullShark, "enableSharkDBAutoDownload", e -> {
            IBUToolKitUtil.saveSharkSettings(project, (String) cbShark.getSelectedItem());
            IBUToolKitUtil.sync(anActionEvent);
            return;
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
            return;
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
            return;
        });
        lbPrjVersion.setText(IBUToolKitUtil.getPluginVersionStr());
        lbPrjHasNew.setVisible(IBUToolKitUtil.getPluginVersion() < IBUToolKitUtil.getPluginLatestVersion(project));
        lbPrjNewTip.setVisible(IBUToolKitUtil.getPluginVersion() < IBUToolKitUtil.getPluginLatestVersion(project));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void setupUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, JBUI.insets(8), -1, -1, false, false));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JBTabbedPane tabbedPane = new JBTabbedPane();
        mainPanel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        tabbedPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JPanel component = new JPanel();
        component.setLayout(new GridLayoutManager(2, 1, JBUI.insets(8), -1, -1, false, false));
        tabbedPane.addTab("Bundle Config", null, component, null);
        component.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JBScrollPane comp2 = new JBScrollPane();
        component.add(comp2, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        comp2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JPanel viewportView = new JPanel();
        viewportView.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, true));
        comp2.setViewportView(viewportView);
        viewportView.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JBScrollPane comp3 = new JBScrollPane();
        viewportView.add(comp3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 7, null, null, null));
        comp3.setBorder(BorderFactory.createTitledBorder(null, "不参与编译", 0, 0, null, null));
        comp3.setViewportView(excludeList = new JList());
        JBScrollPane comp4 = new JBScrollPane();
        viewportView.add(comp4, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 7, null, null, null));
        comp4.setBorder(BorderFactory.createTitledBorder(null, "AAR编译", 0, 0, null, null));
        comp4.setViewportView(aarList = new JList());
        JBScrollPane comp5 = new JBScrollPane();
        viewportView.add(comp5, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 7, null, null, null));
        comp5.setBorder(BorderFactory.createTitledBorder(null, "源码编译", 0, 0, null, null));
        comp5.setViewportView(sourceList = new JList());
        JPanel comp6 = new JPanel();
        comp6.setLayout(new FlowLayout(2, 5, 5));
        component.add(comp6, new GridConstraints(1, 0, 1, 1, 2, 1, 3, 3, null, null, null));
        JButton button = new JButton();
        (btnSave = button).setText("Save");
        comp6.add(button);
        JButton button2 = new JButton();
        (btnSaveAndSync = button2).setText("Save and Sync");
        comp6.add(button2);
        JPanel component2 = new JPanel();
        component2.setLayout(new GridLayoutManager(4, 1, JBUI.insets(8), -1, -1, false, false));
        tabbedPane.addTab("Tools", null, component2, null);
        component2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, 0, 0, null, null));
        JPanel comp7 = new JPanel();
        comp7.setLayout(new GridLayoutManager(3, 2, JBUI.insets(8), -1, -1, false, false));
        component2.add(comp7, new GridConstraints(0, 0, 1, 1, 9, 0, 3, 3, null, null, null));
        comp7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Auto Download (Disable to speed up compilation)", 0, 0, null, null));
        JPanel comp8 = new JPanel();
        comp8.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, true));
        comp7.add(comp8, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel comp9 = new JLabel();
        comp9.setHorizontalAlignment(SwingConstants.LEFT);
        comp9.setHorizontalTextPosition(SwingConstants.LEFT);
        comp9.setText("Shark DB:");
        comp9.setToolTipText("");
        comp9.setVerticalAlignment(SwingConstants.CENTER);
        comp9.setVerticalTextPosition(SwingConstants.CENTER);
        comp8.add(comp9, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        cbShark = new ComboBox();
        cbShark.setMaximumSize(new Dimension(32767, 32767));
        cbShark.setPreferredSize(new Dimension(80, 30));
        comp8.add(cbShark, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        btnPullShark = new JButton();
        btnPullShark.setText("Fetch");
        btnPullShark.setToolTipText("");
        comp8.add(btnPullShark, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        JPanel comp10 = new JPanel();
        comp10.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        comp7.add(comp10, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel comp11 = new JLabel();
        comp11.setText("Cargo DB:");
        comp11.setToolTipText("");
        comp10.add(comp11, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        ComboBox comboBox2 = new ComboBox();
        (cbCargo = comboBox2).setPreferredSize(new Dimension(80, 30));
        comp10.add(comboBox2, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        JButton button4 = new JButton();
        (btnPullCargo = button4).setText("Fetch");
        button4.setToolTipText("");
        comp10.add(button4, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        JPanel comp12 = new JPanel();
        comp12.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        comp7.add(comp12, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel comp13 = new JLabel();
        comp13.setText("FoxPage Package:");
        comp12.add(comp13, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        ComboBox comboBox3 = new ComboBox();
        (cbFoxPage = comboBox3).setPreferredSize(new Dimension(80, 30));
        comp12.add(comboBox3, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        JButton button5 = new JButton();
        (btbPullFoxPage = button5).setText("Fetch");
        comp12.add(button5, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        JPanel comp14 = new JPanel();
        comp14.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, true, false));
        comp7.add(comp14, new GridConstraints(1, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel comp15 = new JLabel();
        comp15.setText("RN Package:");
        comp14.add(comp15, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        ComboBox comboBox4 = new ComboBox();
        (cbRn = comboBox4).setPreferredSize(new Dimension(80, 30));
        comp14.add(comboBox4, new GridConstraints(0, 1, 1, 1, 0, 0, 3, 3, null, null, null));
        JButton button6 = new JButton();
        (btnPullRN = button6).setText("Fetch");
        comp14.add(button6, new GridConstraints(0, 2, 1, 1, 0, 0, 3, 3, null, null, null));
        JButton button7 = new JButton();
        (btnSaveSettings = button7).setText("Save");
        comp7.add(button7, new GridConstraints(2, 0, 1, 1, 0, 1, 3, 0, null, null, null));
        JButton button8 = new JButton();
        (btnSaveSettingAndSync = button8).setHorizontalTextPosition(0);
        button8.setOpaque(true);
        button8.setText("Save and Sync");
        comp7.add(button8, new GridConstraints(2, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        JPanel comp16 = new JPanel();
        comp16.setLayout(new GridLayoutManager(2, 1, JBUI.emptyInsets(), -1, -1, false, false));
        component2.add(comp16, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        comp16.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "IBU-CLI", 0, 0, null, null));
        JPanel comp17 = new JPanel();
        comp17.setLayout(new FlowLayout(0, 5, 5));
        comp17.setAlignmentX(0.5f);
        comp17.setAutoscrolls(false);
        comp16.add(comp17, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        JLabel comp18 = new JLabel();
        comp18.setText("Version:");
        comp17.add(comp18);
        JLabel label = new JLabel();
        (lbVersionNumber = label).setText("loading...");
        comp17.add(label);
        JLabel label2 = new JLabel();
        lbHasNew = label2;
        Font $$$getFont$$$ = getFont(null, -1, 10, label2.getFont());
        if ($$$getFont$$$ != null) {
            label2.setFont($$$getFont$$$);
        }
        label2.setForeground(new Color(-393216));
        label2.setHorizontalAlignment(SwingConstants.LEFT);
        label2.setHorizontalTextPosition(SwingConstants.LEFT);
        label2.setText("new");
        label2.setVerticalAlignment(SwingConstants.BOTTOM);
        label2.setVerticalTextPosition(SwingConstants.BOTTOM);
        label2.setVisible(false);
        comp17.add(label2);
        JPanel comp19 = new JPanel();
        comp19.setLayout(new FlowLayout(0, 0, 0));
        comp16.add(comp19, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JButton button9 = new JButton();
        (btnUpgrade = button9).setLabel("Upgrade");
        button9.setText("Upgrade");
        comp19.add(button9);
        JPanel comp20 = new JPanel();
        comp20.setLayout(new GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1, false, false));
        component2.add(comp20, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        comp20.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Other Tools", 0, 0, null, null));
        JButton button10 = new JButton();
        (btnClean = button10).setMargin(JBUI.emptyInsets());
        button10.setText("Clean Build Directories");
        button10.setToolTipText("Clean All build directories");
        comp20.add(button10, new GridConstraints(0, 0, 1, 1, 9, 0, 3, 0, null, null, null));
        JBScrollPane comp21 = new JBScrollPane();
        component2.add(comp21, new GridConstraints(3, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        terminalPane = new JPanel();
        terminalPane.setLayout(new BorderLayout(0, 0));
        terminalPane.setMinimumSize(new Dimension(-1, 300));
        terminalPane.setPreferredSize(new Dimension(0, 300));
        comp21.setViewportView(terminalPane);
        JPanel comp22 = new JPanel();
        comp22.setLayout(new FlowLayout(2, 5, 5));
        mainPanel.add(comp22, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel comp23 = new JLabel();
        comp23.setText("Version:");
        comp22.add(comp23);
        JLabel label3 = new JLabel();
        (lbPrjVersion = label3).setText("loading...");
        comp22.add(label3);
        JLabel label4 = new JLabel();
        lbPrjHasNew = label4;
        Font font = getFont(null, -1, 10, label4.getFont());
        if (font != null) {
            label4.setFont(font);
        }
        label4.setForeground(new Color(-393216));
        label4.setText("new");
        label4.setVisible(false);
        comp22.add(label4);
        lbPrjNewTip = new JLabel();
        lbPrjNewTip.setText("(可直接安装tools/IBUToolKit.jar)");
        comp22.add(lbPrjNewTip);
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
