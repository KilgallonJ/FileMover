package uk.co.mhr.filemover.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import uk.co.mhr.filemover.app.FileTransferRule;
import uk.co.mhr.filemover.config.AppConfiguration;
import uk.co.mhr.filemover.config.AppConfigurationLoader;

/**
 * The primary interface for the File Mover app.
 */
public class MainWindow {

    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;
    private static final int MIN_CELL_WIDTH = MIN_WIDTH / 2;

    private JFrame frame;
    private JPanel panel;
    private JMenuBar menu;
    private JSplitPane splitPane;

    private JButton copyButton;

    private Map<String, FileTransferRule> ruleMap = new HashMap<>();
    private List<JRadioButton> radioButtons = new ArrayList<>();

    private JList<String> list;

    public MainWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialise(AppConfigurationLoader.loadAppConfiguration(), AppConfigurationLoader.getTransferRules());
    }

    private void initialise(final AppConfiguration config, final List<FileTransferRule> rules) {
        frame.getContentPane().removeAll();

        frame.setTitle(config.getTitle());
        frame.setSize(MIN_WIDTH, MIN_HEIGHT);

        menu = new JMenuBar();
        getMenu().forEach(menu::add);

        frame.setJMenuBar(menu);

        initContentPane(rules);

        final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setLocation((screenDimensions.width / 2) - (MIN_WIDTH / 2), (screenDimensions.height / 2) - (MIN_HEIGHT / 2));

        frame.revalidate();
        frame.setVisible(true);
    }

    private void initContentPane(final List<FileTransferRule> rules) {
        splitPane = new JSplitPane();
        splitPane.setDividerLocation(frame.getWidth() / 2);
        splitPane.setResizeWeight(0.5);
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

    private List<JMenu> getMenu() {
        final List<JMenu> menu = new ArrayList<>();

        menu.add(getOptionsMenu());
        menu.add(getHelpMenu());

        return menu;
    }

    private JMenu getOptionsMenu() {
        final JMenu options = new JMenu("Options");
        options.setMnemonic('O');

        options.add(getReloadConfigurationMenuItem());
        options.addSeparator();
        options.add(getExitMenuItem());

        return options;
    }

    private JMenu getHelpMenu() {
        final JMenu help = new JMenu("Help");

        help.setMnemonic('H');

        help.add(getAboutMenuItem());

        return help;
    }

    private JMenuItem getReloadConfigurationMenuItem() {
        final JMenuItem reloadConfiguration = new JMenuItem("Reload Configuration");
        reloadConfiguration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true));

        reloadConfiguration.addActionListener((e) -> {

            initialise(AppConfigurationLoader.reloadConfigurationFromFile(), AppConfigurationLoader.getTransferRules());
        });

        return reloadConfiguration;
    }

    private JMenuItem getExitMenuItem() {
        final JMenuItem exit = new JMenuItem("Exit");

        exit.setMnemonic('x');
        exit.addActionListener((e) -> {
            // This is done in a separate thread to give any currently executing worker threads time to finish.
            // This should avoid terminating the JVM while one of the worker threads is in the middle of copying
            // a set of files to a directory.
            new Thread(() -> {
                System.exit(0);
            }, "Shutdown Handler Thread").start();
        });

        return exit;
    }

    private JMenuItem getAboutMenuItem() {
        final JMenuItem about = new JMenuItem("About");

        about.setMnemonic('A');

        return about;
    }
}
