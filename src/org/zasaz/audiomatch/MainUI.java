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
                int returnVal = fc.showDialog(MainUI.this, "Add");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    progressBar.setIndeterminate(true);
                    SwingWorker worker = new SwingWorker<Integer, Void>() {
                        @Override
                        public Integer doInBackground() {
                            try {
                                AudioLibrary.addToLibrary(file);
                            } catch (Exception ee) {

                            }
                            return 0;
                        }

                        @Override
                        public void done() {
                            listModel.addElement(file.getName());
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
