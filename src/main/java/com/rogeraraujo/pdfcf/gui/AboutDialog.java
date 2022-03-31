/*
 * Copyright (c) 2022, Roger Araújo, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.rogeraraujo.pdfcf.gui;

import com.rogeraraujo.pdfcf.Utils;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * This is the "About" dialog box.
 */
@Slf4j
public class AboutDialog extends JDialog {
    public static AboutDialog createInstance(
            Frame frame, boolean modal, int width, int height) {
        AboutDialog result = new AboutDialog(frame, modal);
        result.buildGui(width, height);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    public static AboutDialog createInstance(
            Dialog dialog, boolean modal, int width, int height) {
        AboutDialog result = new AboutDialog(dialog, modal);
        result.buildGui(width, height);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    private AboutDialog(Frame frame, boolean modal) {
        super(frame, "About " + SwingUtils.APP_WINDOW_TITLE, modal);
    }

    private AboutDialog(Dialog dialog, boolean modal) {
        super(dialog, "About " + SwingUtils.APP_WINDOW_TITLE, modal);
    }

    private void buildGui(int width, int height) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new MigLayout(
            "insets dialog", "[grow, fill]", "[grow, fill] []"));

        setContentPane(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new MigLayout(
            "insets 0", "[] [grow, fill]",
            "[grow, fill] [] [] [] [] [] [] [] []"));

        JPanel summaryPanel = new JPanel(new MigLayout(
            "", "[grow, fill]", "[grow, fill]"));
        JTextArea jtaAbout = SwingUtils.createTextArea(
            null, "", 1, 1, false, true, true, true);

        try {
            jtaAbout.setText(Utils.readTextFromResource(
                "text/about.txt", StandardCharsets.UTF_8));
            jtaAbout.setCaretPosition(0);
        } catch (Exception ex) {
            log.error("Error reading summary:", ex);
            jtaAbout.setText("Error reading summary");
        }

        summaryPanel.add(new JScrollPane(jtaAbout), "growx, growy");

        JPanel licensePanel = new JPanel(new MigLayout(
            "", "[grow, fill]", "[grow, fill]"));
        JTextArea jtaLicense = SwingUtils.createTextArea(
            null, "", 1, 1, false, true, true, true);

        try {
            jtaLicense.setText(Utils.readTextFromResource(
                "text/license.txt", StandardCharsets.UTF_8));
            jtaLicense.setCaretPosition(0);
        } catch (Exception ex) {
            log.error("Error reading license:", ex);
            jtaLicense.setText("Error reading license");
        }

        licensePanel.add(new JScrollPane(jtaLicense), "growx, growy");

        JTabbedPane tabPane = new JTabbedPane(
            JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.addTab("Summary", summaryPanel);
        tabPane.addTab("License", licensePanel);

        topPanel.add(tabPane, "span, growx, growy, wrap");

        Properties sysProps = System.getProperties();

        topPanel.add(new JLabel("Java Runtime Name:"));
        topPanel.add(new JLabel(sysProps.getProperty("java.runtime.name")),
            "growx, wrap");
        topPanel.add(new JLabel("Java Runtime Version:"));
        topPanel.add(new JLabel(sysProps.getProperty("java.runtime.version")),
            "growx, wrap");

        topPanel.add(new JLabel("Java VM Name:"));
        topPanel.add(new JLabel(sysProps.getProperty("java.vm.name")),
            "growx, wrap");
        topPanel.add(new JLabel("Java VM Version:"));
        topPanel.add(new JLabel(sysProps.getProperty("java.vm.version")), "growx");

        mainPanel.add(topPanel, "growx, growy, wrap");

        // Bottom panel
        JPanel bottomPanel = new JPanel(new MigLayout(
            "insets 0", "push []", ""));

        JButton btnClose = SwingUtils.createButton(
            "Close", "icons/silk/door_in.png", null, null);
        btnClose.addActionListener(this::processBtnClose);

        bottomPanel.add(btnClose);

        getRootPane().setDefaultButton(btnClose);

        mainPanel.add(bottomPanel);

        if ((width > 0) && (height > 0)) {
            setSize(width, height);
        }
        else {
            pack();
        }
    }

    private void processBtnClose(ActionEvent event) {
        setVisible(false);
        dispose();
    }
}
