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

import com.rogeraraujo.pdfcf.components.ProcessExecutionRunnable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;

/**
 * A JDialog capable of executing an external process in a separate thread,
 * therefore keeping the Swing GUI responsive, and also of canceling that
 * external process by clicking a button. The results of executing the external
 * process are exposed through a number of attributes.
 */
@Slf4j
public class ProcessExecutionDialog extends JDialog
        implements ProcessExecutionRunnable.PerListener {
    private ProcessBuilder processBuilder;

    private ProcessExecutionRunnable executionRunnable;

    private Thread executionThread;

    @Getter
    private Exception threadExecutionException;

    @Getter
    private Process process = null;

    @Getter
    private BufferedReader processOutputReader = null;

    @Getter
    private BufferedReader processErrorReader = null;

    @Getter
    private Integer processExitValue = null;

    @Getter
    private Exception processException;

    public static ProcessExecutionDialog createInstance(
            Frame frame, boolean modal, int width, int height,
            ProcessBuilder processBuilder) {
        ProcessExecutionDialog result = new ProcessExecutionDialog(frame, modal);
        result.buildGui(width, height, processBuilder);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    private ProcessExecutionDialog(Frame frame, boolean modal) {
        super(frame, SwingUtils.APP_WINDOW_TITLE, modal);
    }

    private void buildGui(
            int width, int height, ProcessBuilder processBuilder) {
        if (processBuilder == null) {
            throw new IllegalArgumentException("Process builder cannot be null");
        }

        this.processBuilder = processBuilder;

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel(new MigLayout(
            "insets dialog", "[grow, fill]", "[grow, fill] []"));

        setContentPane(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill]", ""));

        JLabel lblMessage = new JLabel("Running process...");

        topPanel.add(lblMessage, "growx");

        mainPanel.add(topPanel, "growx, growy, wrap");

        // Bottom panel
        JPanel bottomPanel = new JPanel(new MigLayout(
            "insets 0", "push [] push", ""));

        JButton btnCancel = SwingUtils.createButton(
            "Cancel", "icons/silk/cancel.png", null, null);
        btnCancel.addActionListener(this::processBtnCancel);

        bottomPanel.add(btnCancel);

        mainPanel.add(bottomPanel);

        if ((width > 0) && (height > 0)) {
            setSize(width, height);
        }
        else {
            pack();
        }

        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent event) {
                startProcess();
            }});
    }

    private void startProcess() {
        if ((executionRunnable != null) ||
            (executionThread != null)) {
            return;
        }

        try {
            executionRunnable = new ProcessExecutionRunnable(processBuilder);
            executionRunnable.addListener(this);

            executionThread = new Thread(executionRunnable);
            executionThread.start();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } catch (Exception ex) {
            threadExecutionException = ex;

            log.error("Error upon attempting to execute process:", ex);
            processBtnCancel(null);
        }
    }

    private void processBtnCancel(ActionEvent event) {
        // Terminates the external process and interrupts the thread that
        // started it if need be
        if (executionRunnable != null) {
            executionRunnable.destroyProcessForcibly();
        }

        if ((executionThread != null) &&
            executionThread.isAlive() &&
            !executionThread.isInterrupted()) {
            executionThread.interrupt();
        }

        setVisible(false);
        dispose();
    }

    @Override
    public void notifyThreadStart(ProcessExecutionRunnable source) {
        // Nothing to do
    }

    @Override
    public void notifyProcessCreation(ProcessExecutionRunnable source) {
        this.process = source.getProcess();
        this.processOutputReader = source.getProcessOutputReader();
        this.processErrorReader = source.getProcessErrorReader();
    }

    @Override
    public void notifyThreadEnd(ProcessExecutionRunnable source) {
        this.processExitValue = source.getProcessExitValue();
        this.processException = source.getProcessException();

        SwingUtilities.invokeLater(() -> processBtnCancel(null));
    }
}
