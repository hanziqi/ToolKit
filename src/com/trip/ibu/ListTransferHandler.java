package com.trip.ibu;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.*;

public class ListTransferHandler extends TransferHandler {
    private int[] indices;
    private int addIndex;
    private int addCount;

    public ListTransferHandler() {
        indices = null;
        addIndex = -1;
        addCount = 0;
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        indices = list.getSelectedIndices();
        Object[] values = list.getSelectedValues();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < values.length; ++i) {
            Object val = values[i];
            buff.append((val == null) ? "" : val.toString());
            if (i != values.length - 1) {
                buff.append("\n");
            }
        }
        return new StringSelection(buff.toString());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return 3;
    }

    @Override
    public boolean importData(TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }
        JList list = (JList) info.getComponent();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        int index = dl.getIndex();
        boolean insert = dl.isInsert();
        Transferable t = info.getTransferable();
        String data;
        try {
            data = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return false;
        }
        String[] values = data.split("\n");
        addIndex = index;
        addCount = values.length;
        for (int i = 0; i < values.length; ++i) {
            if (insert) {
                if (listModel.contains(values[i])) {
                    return false;
                }
                listModel.add(index++, values[i]);
            } else if (index < listModel.getSize()) {
                if (listModel.contains(values[i])) {
                    return false;
                }
                listModel.set(index++, values[i]);
            } else {
                if (listModel.contains(values[i])) {
                    return false;
                }
                listModel.add(index++, values[i]);
            }
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        JList source = (JList) c;
        DefaultListModel listModel = (DefaultListModel) source.getModel();
        if (action == 2) {
            for (int i = indices.length - 1; i >= 0; --i) {
                listModel.remove(indices[i]);
            }
        }
        indices = null;
        addCount = 0;
        addIndex = -1;
    }
}
