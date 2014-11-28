package org.zasaz.audiomatch;

import org.zasaz.audiomatch.util.AudioLibrary;
import org.zasaz.audiomatch.util.MP3Filter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Mike on 11/27/2014.
 */
public class MainUI extends JFrame {
    private JList audioList;
    private JButton addButton;
    private JPanel rootPanel;
    private JProgressBar progressBar;
    private JButton matchButton;
    private DefaultListModel listModel;
    private final JFileChooser fc = new JFileChooser();
    public static String matched = "";

    public MainUI() {
        super();
        try {
            UIManager.setLookAndFeel((LookAndFeel) Class.forName(UIManager.getSystemLookAndFeelClassName()).newInstance());
        } catch (Exception e) {
        }
        setContentPane(rootPanel);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        listModel = new DefaultListModel();
        audioList.setModel(listModel);
        fc.addChoosableFileFilter(new MP3Filter());
        fc.setAcceptAllFileFilterUsed(false);
        this.setTitle("Audio Match");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setMultiSelectionEnabled(true);
                int returnVal = fc.showDialog(MainUI.this, "Add");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    progressBar.setIndeterminate(true);
                    SwingWorker worker = new SwingWorker<Integer, Void>() {
                        @Override
                        public Integer doInBackground() {
                            try {
                                if (files.length > 1) {
                                    progressBar.setIndeterminate(false);
                                    progressBar.setStringPainted(true);
                                }
                                for (int i = 0; i < files.length; i++) {
                                    AudioLibrary.addToLibrary(files[i]);
                                    if (files.length > 1) {
                                        progressBar.setValue((int)((i + 1.0) / files.length * 100));
                                    }
                                }
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                            return 0;
                        }

                        @Override
                        public void done() {
                            progressBar.setIndeterminate(true);
                            for (int i = 0; i < files.length; i++) {
                                listModel.addElement(files[i].getName());
                            }
                            progressBar.setIndeterminate(false);
                        }
                    };
                    worker.execute();
                } else {
                }
            }
        });
        matchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showDialog(MainUI.this, "Match");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    progressBar.setIndeterminate(true);
                    SwingWorker worker = new SwingWorker<String, Void>() {
                        @Override
                        public String doInBackground() {
                            String name = "Error";
                            try {
                                name = AudioLibrary.tryToMatch(file);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                            MainUI.matched = name;
                            return name;
                        }

                        @Override
                        public void done() {
                            progressBar.setIndeterminate(false);
                            JOptionPane.showMessageDialog(MainUI.this, MainUI.matched);
                        }
                    };
                    worker.execute();
                } else {
                }
            }
        });
        setVisible(true);
    }
}
