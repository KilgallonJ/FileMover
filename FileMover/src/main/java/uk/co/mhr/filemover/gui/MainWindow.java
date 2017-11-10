/**
 * (c) Midland Software Limited 2017
 * Name     : MainWindow.java
 * Author   : kilgallonj
 * Date     : 10 Nov 2017
 */
package uk.co.mhr.filemover.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JFrame;

import uk.co.mhr.filemover.app.FileTransferRule;
import uk.co.mhr.filemover.config.AppConfiguration;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JRadioButton;
import javax.swing.JList;

/**
 * The primary interface for the File Mover app.
 */
public class MainWindow {

    private JFrame frame;

    private JButton copyButton;

    private Map<String, FileTransferRule> ruleMap;
    private List<JRadioButton> radioButtons;

    private JList<String> list;

    public MainWindow(final AppConfiguration config, final List<FileTransferRule> rules) {
        frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JSplitPane splitPane = new JSplitPane();
        frame.getContentPane().add(splitPane);

        final JPanel panel = new JPanel();
        splitPane.setRightComponent(panel);

        copyButton = new JButton("COPY");
        copyButton.setVerticalAlignment(SwingConstants.BOTTOM);
        copyButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyButton.addActionListener((a) -> {
            /*
             * We need to: - Find out which radio button was selected when COPY was clicked - Find out which files are selected in
             * the left-hand pane - Tell the appropriate rule to copy those files, or copy all of them if none are selected
             */
            final JRadioButton btn = radioButtons.stream().filter(JRadioButton::isSelected).findFirst()
                    .orElseThrow(() -> new RuntimeException());
            final FileTransferRule rule = ruleMap.get(btn.getText());

            final List<String> selectedFiles = list.getSelectedValuesList();

            final List<Path> fileNames = selectedFiles.stream().map(sf -> Paths.get(sf))
                    .collect(Collectors.toList());

            rule.apply(fileNames);
        });

        panel.setLayout(new GridLayout(0, rules.size() + 1, 0, 0));

        ruleMap = new HashMap<>();

        for (final FileTransferRule rule : rules) {
            final JRadioButton ruleRadioButton = new JRadioButton(rule.getDisplayName());
            panel.add(ruleRadioButton);
            radioButtons.add(ruleRadioButton);
            ruleMap.put(rule.getDisplayName(), rule);

            ruleRadioButton.addActionListener((a) -> {
                populateJList(rule);
            });
        }

        panel.add(copyButton);

        list = new JList<>();
        list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        splitPane.setLeftComponent(list);

        frame.setTitle(config.getTitle());
    }

    private void populateJList(final FileTransferRule rule) {
        final DefaultListModel<String> model = new DefaultListModel<>();
        try {
            Files.list(rule.getSource()).map(Path::getFileName).map(Path::toString).forEach(model::addElement);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        list.setModel(model);
    }
}
