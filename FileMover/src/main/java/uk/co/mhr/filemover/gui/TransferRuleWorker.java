/**
 * (c) Midland Software Limited 2017
 * Name     : TransferRuleWorker.java
 * Author   : kilgallonj
 * Date     : 13 Nov 2017
 */
package uk.co.mhr.filemover.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import uk.co.mhr.filemover.app.FileTransferRule;

/**
 * Swing worker to handle the copying of files outside of the event dispatch thread.
 */
public class TransferRuleWorker extends SwingWorker<Void, Void> {

    private Component frame;
    private FileTransferRule rule;
    private List<Path> filesToCopy;

    public TransferRuleWorker(final JFrame frame, final FileTransferRule rule, final List<Path> filesToCopy) {
        this.frame = frame;
        this.rule = rule;
        this.filesToCopy = filesToCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Void doInBackground() throws Exception {
        final Cursor currentCursor = frame.getCursor();

        try {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rule.apply(filesToCopy);
        } finally {
            frame.setCursor(currentCursor);
        }

        return null;
    }

}
