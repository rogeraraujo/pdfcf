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

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This dialog box displays information about Exceptions.
 */
public class ExceptionDialog extends JDialog {
    private JTextArea jtaStackTrace = null;

    public static ExceptionDialog createInstance(
            Frame frame, boolean modal, int width, int height,
            String errorMessage, Exception exception) {
        ExceptionDialog result = new ExceptionDialog(frame, modal);
        result.buildGui(width, height, errorMessage, exception);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    public static ExceptionDialog createInstance(
            Dialog dialog, boolean modal, int width, int height,
            String errorMessage, Exception exception) {
        ExceptionDialog result = new ExceptionDialog(dialog, modal);
        result.buildGui(width, height, errorMessage, exception);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    private ExceptionDialog(Frame frame, boolean modal) {
        super(frame, SwingUtils.APP_WINDOW_TITLE, modal);
    }

    private ExceptionDialog(Dialog dialog, boolean modal) {
        super(dialog, SwingUtils.APP_WINDOW_TITLE, modal);
    }

    private void buildGui(
            int width, int height, String errorMessage, Exception exception) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new MigLayout(
            "insets dialog", "[grow, fill]", "[grow, fill] []"));

        setContentPane(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new MigLayout(
            "insets 0", "[] [grow, fill]", "[grow, fill]"));

        JLabel lblIcon = new JLabel(UIManager.getIcon(
            SwingUtils.OPTION_PANE_ERROR_ICON_KEY));
        //lblIcon.setVerticalAlignment(JLabel.TOP);

        topPanel.add(lblIcon);
        topPanel.add(createErrorPanel(errorMessage, exception), "growx, growy");

        mainPanel.add(topPanel, "growx, growy, wrap");

        // Bottom panel
        JPanel bottomPanel;

        if (jtaStackTrace == null) {
            bottomPanel = new JPanel(new MigLayout(
                "insets 0", "push []", "[]"));
        }
        else {
            bottomPanel = new JPanel(new MigLayout(
                "insets 0", "push [] []", "[]"));

            JButton btnCopyStackTrace = SwingUtils.createButton(
                "Copy Stack Trace", "icons/silk/page_copy.png", null, null);
            btnCopyStackTrace.addActionListener(this::processBtnCopyStackTrace);

            bottomPanel.add(btnCopyStackTrace);
        }

        JButton btnClose = SwingUtils.createButton(
            "Close", "icons/silk/door_in.png", null, null);
        btnClose.addActionListener(this::processBtnClose);

        bottomPanel.add(btnClose);

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

    private void processBtnCopyStackTrace(ActionEvent event) {
        SwingUtils.copyTextToClipboard(jtaStackTrace.getText(), null);
    }

    private JPanel createErrorPanel(String errorMessage, Exception exception) {
        JPanel result;

        // Simpler case
        if (exception == null) {
            result = new JPanel(new MigLayout(
                "insets 0", "[grow, fill]", "[grow, fill]"));

            JTextField jtfErrorMessage = new JTextField(errorMessage);
            jtfErrorMessage.setEditable(false);

            result.add(jtfErrorMessage, "growx, growy");

            return result;
        }

        // Shows more information
        result = new JPanel(new MigLayout(
            "insets 0", "[grow, fill]",
            "[] paragraph [] [] paragraph [] [grow, fill]"));

        JTextField jtfErrorMessage = new JTextField(errorMessage);
        jtfErrorMessage.setEditable(false);

        result.add(jtfErrorMessage, "growx, wrap");

        result.add(new JLabel("Error Details:"), "wrap");

        JTextArea jtaErrorDetails = SwingUtils.createTextArea(
            null, exception.getMessage(), 5, 10, false, true, true, true);
        JScrollPane scpErrorDetails = new JScrollPane(jtaErrorDetails);
        jtaErrorDetails.setCaretPosition(0);

        result.add(scpErrorDetails, "growx, wrap");

        result.add(new JLabel("Stack Trace:"), "wrap");

        StringWriter strWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(strWriter));

        jtaStackTrace = SwingUtils.createTextArea(
            null, strWriter.toString(), 10, 10, false, false, true, true);
        JScrollPane scpStackTrace = new JScrollPane(jtaStackTrace);
        jtaStackTrace.setCaretPosition(0);

        result.add(scpStackTrace, "growx, growy");

        return result;
    }
}
