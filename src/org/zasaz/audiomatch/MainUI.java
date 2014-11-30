package org.zasaz.audiomatch;

//Copyright (C) 2014  Yingzhuo Li
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
    private JScrollPane scroll;
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
                            if (files.length > 1) {
                                progressBar.setIndeterminate(false);
                                progressBar.setStringPainted(true);
                            }
                            for (int i = 0; i < files.length; i++) {
                                try {
                                    AudioLibrary.addToLibrary(files[i]);
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                    files[i] = null;
                                }
                                if (files.length > 1) {
                                    progressBar.setValue((int) ((i + 1.0) / files.length * 100));
                                }
                            }

                            return 0;
                        }

                        @Override
                        public void done() {
                            progressBar.setIndeterminate(true);
                            for (int i = 0; i < files.length; i++) {
                                if(files[i]!=null) {
                                    listModel.addElement(files[i].getName());
                                }
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
                fc.setMultiSelectionEnabled(false);
                int returnVal = fc.showDialog(MainUI.this, "Match");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    progressBar.setStringPainted(false);
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
