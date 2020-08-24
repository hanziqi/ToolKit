package com.torlax.ibu;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.annotation.Nonnull;
import javax.swing.JComboBox;

public class ComboConfig {
    @Nonnull
    private final JComboBox jComboBox;
    @Nonnull
    private final String property;
    @Nonnull
    private final JButton jButton;

    public ComboConfig(@Nonnull final JComboBox jComboBox, @Nonnull final JButton jButton, @Nonnull final String property, @Nonnull final ActionListener actionListener) {
        this.jComboBox = jComboBox;
        this.property = property;
        (this.jButton = jButton).addActionListener(actionListener);
    }

    @Nonnull
    public JComboBox getComboBox() {
        return this.jComboBox;
    }

    @Nonnull
    public String getProperty() {
        return this.property;
    }
}