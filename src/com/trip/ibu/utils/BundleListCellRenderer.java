package com.trip.ibu.utils;

import java.awt.*;

import javax.swing.*;

public class BundleListCellRenderer extends DefaultListCellRenderer {
    public BundleListCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        this.setIcon(resize("/icons/aar.png", 12));
        return this;
    }

    public static ImageIcon resize(String url, int size) {
        ImageIcon imageIcon = new ImageIcon(BundleListCellRenderer.class.getResource(url));
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(size, size, 4);
        imageIcon = new ImageIcon(newImage);
        return imageIcon;
    }
}
