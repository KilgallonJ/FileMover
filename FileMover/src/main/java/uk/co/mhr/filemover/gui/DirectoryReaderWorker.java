package uk.co.mhr.filemover.gui;

import java.awt.Cursor;
import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingWorker;

import uk.co.mhr.filemover.app.FileTransferRule;

/**
 * Swing worker to handle the reading of files in a directory (which may be a network location) outside of the EDT.
 */
public class DirectoryReaderWorker extends SwingWorker<DefaultListModel<String>, Void> {

    private FileTransferRule rule;
    private JList<String> list;
    private ButtonGroup group;

    public DirectoryReaderWorker(final FileTransferRule rule, final JList<String> list, final ButtonGroup group) {
        this.rule = rule;
        this.list = list;
        this.group = group;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultListModel<String> doInBackground() throws Exception {
        final Cursor currentCursor = list.getParent().getParent().getCursor();

        try {
            setButtonGroupState(false);
            list.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            final DefaultListModel<String> model = new DefaultListModel<>();
            File[] files = rule.getSource().toFile().listFiles(rule.getFilter());

            if (files == null) {
                files = new File[0];
            }

            Stream.of(files).map(File::getName).forEach(model::addElement);
            return model;
        } finally {
            list.getParent().getParent().setCursor(currentCursor);
            setButtonGroupState(true);
        }
    }

    @Override
    protected void done() {
        try {
            list.setModel(get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error updating list elements: " + e.getMessage(), e);
        }
    }

    private void setButtonGroupState(final boolean isEnabled) {
        final Enumeration<AbstractButton> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            final AbstractButton button = buttons.nextElement();
            button.setEnabled(isEnabled);
        }
    }
}
