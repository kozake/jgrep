package jgrep.ui;

import jgrep.command.event.*;
import jgrep.command.grep.GrepCommand;
import jgrep.command.grep.Hit;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private JButton btnGrep;
    private JTextField txtTargetDirectory;
    private JTextField txtKeyword;
    private JPanel pnlMain;
    private JButton btnDirectoryChoice;
    private JTextArea txaOutput;
    private JProgressBar progressBar;
    private JLabel lblProgress;
    private JFormattedTextField txtThreads;
    private JCheckBox chkRegex;
    private JComboBox cmbCharsetName;
    private JCheckBox chkIgnoreCase;
    private JFormattedTextField txtTargetGlobPattern;
    private SwingWorker grepWorker;

    public MainFrame() {
        $$$setupUI$$$();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(pnlMain);
        setVisible(true);
        pack();
        btnDirectoryChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(MainFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    txtTargetDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        btnGrep.addActionListener(e -> {
            if (grepWorker != null) {
                grepWorker.cancel(true);
                return;
            }
            if (txtTargetDirectory.getText().isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, "対象ディレクトリを選択してください。");
                return;
            }
            if (txtKeyword.getText().isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, "検索文字を選択してください。");
                return;
            }
            btnGrep.setText("cancel");
            grepWorker = new SwingWorker<List<Hit>, List<Hit>>() {
                private long start;

                @Override
                protected List<Hit> doInBackground() throws Exception {
                    start = System.currentTimeMillis();
                    txaOutput.setText("");
                    try {
                        GrepCommand command = new GrepCommand();
                        command.setTargetDirectory(new File(txtTargetDirectory.getText()));
                        command.setTargetGlobPattern(txtTargetGlobPattern.getText());
                        command.setKeyword(txtKeyword.getText());
                        command.setCharsetName(cmbCharsetName.getSelectedItem().toString());
                        command.setRegex(chkRegex.isSelected());
                        command.setIgnoreCase(chkIgnoreCase.isSelected());
                        command.setThreads(Integer.parseInt(txtThreads.getText()));
                        command.addCommandEventListener(event -> {
                            if (isCancelled()) {
                                return;
                            }
                            if (event.getType() == CommandEventType.Process) {
                                progressBar.setValue(event.getProgress());
                                lblProgress.setText(
                                        (event.getTop() == CommandEvent.UNKNOWN_PROGRESS ? "?" : event.getTop())
                                                + "/"
                                                + (event.getBottom() == CommandEvent.UNKNOWN_PROGRESS ? "?" : event.getBottom()));
                                publish(event.getChunks());
                            }
                        });
                        return command.execute();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                @Override
                protected void process(List<List<Hit>> chunks) {
                    try {
                        for (List<Hit> chunk : chunks) {
                            for (Hit hit : chunk) {
                                if (isCancelled()) {
                                    return;
                                }
                                String line = hit.getLine();
                                txaOutput.append(hit.getFile().getAbsolutePath());
//                                txaOutput.append(hit.getFile().getAbsolutePath() + ": " + line.substring(0, Math.min(line.length(), 20)));
                                txaOutput.append(System.lineSeparator());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                protected void done() {
                    if (isCancelled()) {
                        txaOutput.append("キャンセルしました。");
                        txaOutput.append(System.lineSeparator());
                    }
                    super.done();
                    btnGrep.setText("grep");
                    grepWorker = null;
                    long end = System.currentTimeMillis();
                    txaOutput.append(System.lineSeparator());
                    txaOutput.append("実行時間: " + (end - start) + "(ms)");
                    txaOutput.append(System.lineSeparator());

                }
            };
            grepWorker.execute();
        });
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        pnlMain = new JPanel();
        pnlMain.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("対象ディレクトリ");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlMain.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("検索文字");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlMain.add(label2, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(panel1, gbc);
        txtTargetDirectory = new JTextField();
        txtTargetDirectory.setEditable(false);
        txtTargetDirectory.setPreferredSize(new Dimension(200, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(txtTargetDirectory, gbc);
        btnDirectoryChoice = new JButton();
        btnDirectoryChoice.setText("選択");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(btnDirectoryChoice, gbc);
        txtKeyword = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(txtKeyword, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(600, 300));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(scrollPane1, gbc);
        txaOutput = new JTextArea();
        txaOutput.setEditable(true);
        scrollPane1.setViewportView(txaOutput);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(panel2, gbc);
        lblProgress = new JLabel();
        lblProgress.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel2.add(lblProgress, gbc);
        progressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(progressBar, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(panel3, gbc);
        cmbCharsetName = new JComboBox();
        cmbCharsetName.setEditable(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("UTF-8");
        defaultComboBoxModel1.addElement("windows-31j");
        defaultComboBoxModel1.addElement("EUC-JP");
        cmbCharsetName.setModel(defaultComboBoxModel1);
        panel3.add(cmbCharsetName);
        chkRegex = new JCheckBox();
        chkRegex.setText("正規表現");
        panel3.add(chkRegex);
        chkIgnoreCase = new JCheckBox();
        chkIgnoreCase.setText("大文字小文字を区別しない");
        panel3.add(chkIgnoreCase);
        final JLabel label3 = new JLabel();
        label3.setText("スレッド数");
        panel3.add(label3);
        txtThreads.setPreferredSize(new Dimension(60, 30));
        txtThreads.setText("5");
        panel3.add(txtThreads);
        btnGrep = new JButton();
        btnGrep.setText("grep");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        pnlMain.add(btnGrep, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("対象ファイル");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlMain.add(label4, gbc);
        txtTargetGlobPattern = new JFormattedTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlMain.add(txtTargetGlobPattern, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pnlMain;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(99);
        formatter.setAllowsInvalid(true);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        txtThreads = new JFormattedTextField(formatter);
    }
}
