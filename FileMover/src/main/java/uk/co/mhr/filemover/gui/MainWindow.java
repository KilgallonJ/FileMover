/**
 * (c) Midland Software Limited 2017
 * Name     : MainWindow.java
 * Author   : kilgallonj
 * Date     : 10 Nov 2017
 */
package uk.co.mhr.filemover.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;

import uk.co.mhr.filemover.app.FileTransferRule;
import uk.co.mhr.filemover.config.AppConfiguration;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JRadioButton;
import javax.swing.JList;

/**
 * The primary interface for the File Mover app.
 */
public class MainWindow {

    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 400;
    private static final int MIN_CELL_WIDTH = MIN_WIDTH / 2;

    private JFrame frame;
    private JPanel panel;
    private JSplitPane splitPane;

    private JButton copyButton;

    private Map<String, FileTransferRule> ruleMap = new HashMap<>();
    private List<JRadioButton> radioButtons = new ArrayList<>();

    private JList<String> list;

    public MainWindow(final AppConfiguration config, final List<FileTransferRule> rules) {
        frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        splitPane = new JSplitPane();
        frame.getContentPane().add(splitPane);

        panel = new JPanel();
        splitPane.setRightComponent(panel);

        list = new JList<>();
        list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        list.setFixedCellWidth(MIN_CELL_WIDTH);
        splitPane.setLeftComponent(list);

        panel.setLayout(new GridLayout(rules.size() + 1, 1, 0, 0));

        setupCopyButton();
        setupRadios(rules);

        panel.add(copyButton);

        frame.setTitle(config.getTitle());
        frame.setSize(MIN_WIDTH, MIN_HEIGHT);

        final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setLocation((screenDimensions.width / 2) - (MIN_WIDTH / 2), (screenDimensions.height / 2) - (MIN_HEIGHT / 2));

        frame.setVisible(true);
    }

    private void setupCopyButton() {
        copyButton = new JButton("COPY");
        copyButton.setVerticalAlignment(SwingConstants.CENTER);
        copyButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyButton.addActionListener((a) -> {
            // We need to:
            // - Find out which radio button was selected when COPY was clicked
            // - Find out which files are selected in the left-hand pane
            // - Tell the appropriate rule to copy those files, or copy all of them if none are selected
            final JRadioButton btn = radioButtons.stream().filter(JRadioButton::isSelected).findFirst()
                    .orElseThrow(() -> new RuntimeException("No rule selected!"));
            final FileTransferRule rule = ruleMap.get(btn.getText());

            final List<String> selectedFiles = list.getSelectedValuesList();

            final List<Path> filesToCopy = selectedFiles.stream().map(sf -> Paths.get(sf)).collect(Collectors.toList());

            if (filesToCopy.isEmpty()) {
                Object[] files = ((DefaultListModel<String>) list.getModel()).toArray();
                Stream.of(files).forEach(s -> filesToCopy.add(Paths.get(String.valueOf(s))));
            }

            new TransferRuleWorker(frame, rule, filesToCopy).execute();
        });
    }

    private void setupRadios(final List<FileTransferRule> rules) {
        final ButtonGroup group = new ButtonGroup();

        for (final FileTransferRule rule : rules) {
            final JRadioButton ruleRadioButton = new JRadioButton(rule.getDisplayName());
            panel.add(ruleRadioButton);
            group.add(ruleRadioButton);
            radioButtons.add(ruleRadioButton);
            ruleMap.put(rule.getDisplayName(), rule);

            ruleRadioButton.addActionListener((a) -> {
                new DirectoryReaderWorker(rule, list).execute();
            });
        }

        if (!radioButtons.isEmpty()) {
            final JRadioButton btn = radioButtons.get(0);
            btn.setSelected(true);
            new DirectoryReaderWorker(ruleMap.get(btn.getText()), list).execute();
        }
    }
}
