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
import com.rogeraraujo.pdfcf.components.*;
import com.rogeraraujo.pdfcf.gs.ConversionQuality;
import com.rogeraraujo.pdfcf.gs.GsUtils;
import com.rogeraraujo.pdfcf.gs.PdfCompatibilityLevel;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.validator.routines.UrlValidator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This is the main window of PDF Compression Frontend.
 */
@Slf4j
public class MainWindow extends JFrame {
    private static final int EXCEPTION_DLG_BOX_WIDTH = 600;
    private static final int EXCEPTION_DLG_BOX_HEIGHT = 500;

    private static final String DEFAULT_CONVERSION_QUALITY_ID =
        ConversionQuality.EBOOK.getId();
    private static final String DEFAULT_PDF_COMPATIBILITY_LEVEL =
        PdfCompatibilityLevel.DEFAULT.getId();
    private static final String DEFAULT_GS_ADDITIONAL_PARAMETERS = "";

    private JTextField jtfGsExecutablePath;

    private JComboBox<ComboBoxItem<ConversionQuality>> cboConversionQuality;

    private JComboBox<ComboBoxItem<PdfCompatibilityLevel>>
        cboPdfCompatibilityLevel;

    private JTextField jtfAdditionalGsParameters;

    private JTextField jtfInputFile;

    private JTextField jtfOutputFile;

    private JTextArea jtaCompressionLog;

    private String gsUsageHelpUrl = "";

    private String defaultInputFileDirPath = "";

    private String defaultOutputFileDirPath = "";

    private JFileChooser executableFileChooser;

    private JFileChooser pdfFileChooser;

    public static MainWindow createInstance(Properties config) {
        MainWindow result = new MainWindow();
        result.configure(config);
        SwingUtils.centerWindow(result, null);

        return result;
    }

    private MainWindow() {
        super(SwingUtils.APP_WINDOW_TITLE);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) {
                confirmExit();
            }

            @Override
            public void windowOpened(WindowEvent event) {
                processWindowOpened();
            }
        });

        setLayout(new MigLayout(
            "", "[grow, fill]", "[grow, fill]"));

        add(createMainPanel(), "growx, growy");

        setSize(650, 600);
    }

    private void confirmExit() {
        int answer = JOptionPane.showConfirmDialog(
            this, "Close PDF Compression Frontend?",
            SwingUtils.APP_WINDOW_TITLE, JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            setVisible(false);
            dispose();
        }
    }

    private void processWindowOpened() {
        if (Utils.stringIsEmptyOrBlank(jtfGsExecutablePath.getText())) {
            String message =
                "The path of the Ghostscript executable file is not specified in the\n" +
                "configuration file, and it was not possible to locate it automatically.\n\n" +
                "To avoid the need to specify this path manually whenever you execute\n" +
                "this program, please verify the location of the Ghostscript executable\n" +
                "file and update the configuration file accordingly.";

            SwingUtils.showInformationMessage(this, message);
        }
        else {
            jtfInputFile.requestFocusInWindow();
        }
    }

    private void configure(Properties config) {
        // Sets window size
        Integer width = Utils.stringToInt(
            config.getProperty("main-window-width", "-1"));
        Integer height = Utils.stringToInt(
            config.getProperty("main-window-height", "-1"));

        if ((width  != null) && (width  > 0) &&
            (height != null) && (height > 0)) {
            setSize(width, height);
        }

        // Sets Ghostscript configuration values
        String gsExecutablePath = config.getProperty(
            "ghostscript-executable-path", "");

        if (Utils.stringIsEmptyOrBlank(gsExecutablePath)) {
            gsExecutablePath = GsUtils.seekGsExecutablePathWindows();
        }

        if (Utils.stringIsEmptyOrBlank(gsExecutablePath)) {
            gsExecutablePath = GsUtils.seekGsExecutablePathLinux();
        }

        jtfGsExecutablePath.setText(gsExecutablePath);

        ComboBoxItem.setSelectedItem(cboConversionQuality,
            ConversionQuality.getInstance(config.getProperty(
                "conversion-quality", DEFAULT_CONVERSION_QUALITY_ID).trim()));
        ComboBoxItem.setSelectedItem(cboPdfCompatibilityLevel,
            PdfCompatibilityLevel.getInstance(config.getProperty(
                "pdf-compatibility-level", DEFAULT_PDF_COMPATIBILITY_LEVEL).trim()));
        jtfAdditionalGsParameters.setText(config.getProperty(
            "ghostscript-additional-parameters", DEFAULT_GS_ADDITIONAL_PARAMETERS));

        gsUsageHelpUrl = config.getProperty(
            "ghostscript-usage-help-url", GsUtils.DEFAULT_GS_USAGE_HELP_URL);
        defaultInputFileDirPath = config.getProperty(
            "default-input-file-folder", "");
        defaultOutputFileDirPath = config.getProperty(
            "default-output-file-folder", "");
    }

    private JPanel createMainPanel() {
        JPanel result = new JPanel(new MigLayout(
            "", "[] [grow, fill]",
            Utils.repeatString("[] ", 12) + "[grow, fill] [] []"));

        // Ghostscript executable file
        result.add(new JLabel("Ghostscript Executable File:"),
            "span, growx, wrap");

        JPanel executablePanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill] []", ""));

        jtfGsExecutablePath = SwingUtils.createTextField(
            new LimitedDocument(5000), "", 0, true);

        executablePanel.add(jtfGsExecutablePath, "growx");

        JButton btnBrowseGsExecutablePath = SwingUtils.createButton(
            null, "icons/silk/folder_table.png", null, "Browse...");
        btnBrowseGsExecutablePath.addActionListener(
            this::processBtnBrowseGsExecutablePath);

        executablePanel.add(btnBrowseGsExecutablePath, "");

        result.add(executablePanel, "span, growx, wrap");

        // Conversion quality
        result.add(new JLabel("Conversion Quality"));

        ComboBoxModel<ComboBoxItem<ConversionQuality>>
            conversionQualityModel = createConversionQualityModel();
        cboConversionQuality = SwingUtils.createComboBox(
            conversionQualityModel,
            new CustomListCellRenderer.AlternateRowColorTransformer(null), null);

        result.add(cboConversionQuality, "growx, wrap");

        // PDF compatibility level
        result.add(new JLabel("PDF Compatibility Level"));

        DefaultComboBoxModel<ComboBoxItem<PdfCompatibilityLevel>>
            pdfCompatibilityLevelModel = createPdfCompatibilityLevelModel();
        cboPdfCompatibilityLevel = SwingUtils.createComboBox(
            pdfCompatibilityLevelModel,
            new CustomListCellRenderer.AlternateRowColorTransformer(null), null);
        result.add(cboPdfCompatibilityLevel, "growx, wrap");

        // Additional parameters for Ghostscript
        result.add(new JLabel("Additional Parameters for Ghostscript:"),
            "span, growx, wrap");

        JPanel usageHelpPanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill] [] ", ""));

        jtfAdditionalGsParameters = SwingUtils.createTextField(
            new LimitedDocument(5000), "", 0, true);

        JButton btnViewGsUsageHelp = SwingUtils.createButton(
            null, "icons/silk/book_open.png", null,
            "<html>" +
            "View Ghostscript usage help online.<br>" +
            "Clicking this button will open a web<br>" +
            "browser window." +
            "</html>");
        btnViewGsUsageHelp.addActionListener(this::processBtnViewGsUsageHelp);

        usageHelpPanel.add(jtfAdditionalGsParameters, "growx");
        usageHelpPanel.add(btnViewGsUsageHelp);

        result.add(usageHelpPanel, "span, growx, wrap");

        // Input file
        result.add(new JLabel("Input File:"), "span, growx, wrap");

        JPanel inputFilePanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill] [] []", ""));

        jtfInputFile = SwingUtils.createTextField(
            new LimitedDocument(5000), "", 0, true);

        JButton btnBrowseInputFile = SwingUtils.createButton(
            "[F4]", "icons/silk/folder_table.png", null, "Browse...");
        AbstractAction browseInputFileAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                processBtnBrowseInputFile(event);
            }};

        btnBrowseInputFile.addActionListener(browseInputFileAction);
        SwingUtils.bindKeyStrokeToAction(btnBrowseInputFile,
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
            "browseAction", browseInputFileAction);

        JButton btnViewInputFile = SwingUtils.createButton(
            "[F5]", "icons/silk/page_white_acrobat.png", null,
            "<html>View file using<br>external PDF viewer</html>");
        AbstractAction viewInputFileAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                processBtnViewInputFile(event);
            }};

        btnViewInputFile.addActionListener(viewInputFileAction);
        SwingUtils.bindKeyStrokeToAction(btnViewInputFile,
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            "viewAction", viewInputFileAction);

        inputFilePanel.add(jtfInputFile, "growx");
        inputFilePanel.add(btnBrowseInputFile);
        inputFilePanel.add(btnViewInputFile);

        result.add(inputFilePanel, "span, growx, wrap");

        // Output file
        result.add(new JLabel("Output File:"), "span, growx, wrap");

        JPanel outputFilePanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill] [] []", ""));

        jtfOutputFile = SwingUtils.createTextField(
            new LimitedDocument(5000), "", 0, true);

        JButton btnBrowseOutputFile = SwingUtils.createButton(
            "[F6]", "icons/silk/folder_table.png", null, "Browse...");
        AbstractAction browseOutputFileAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                processBtnBrowseOutputFile(event);
            }};

        btnBrowseOutputFile.addActionListener(browseOutputFileAction);
        SwingUtils.bindKeyStrokeToAction(btnBrowseOutputFile,
            KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
            "browseAction", browseOutputFileAction);

        JButton btnViewOutputFile = SwingUtils.createButton(
            "[F7]", "icons/silk/page_white_acrobat.png", null,
            "<html>View file using<br>external PDF viewer</html>");
        AbstractAction viewOutputFileAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                processBtnViewOutputFile(event);
            }};

        btnViewOutputFile.addActionListener(viewOutputFileAction);
        SwingUtils.bindKeyStrokeToAction(btnViewOutputFile,
            KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
            "viewAction", viewOutputFileAction);

        outputFilePanel.add(jtfOutputFile, "growx");
        outputFilePanel.add(btnBrowseOutputFile);
        outputFilePanel.add(btnViewOutputFile);

        result.add(outputFilePanel, "span, growx, wrap");

        // Compress button
        JButton btnCompress = SwingUtils.createButton(
            "Compress [F8]", "icons/silk/compress.png", null, null);
        AbstractAction compressAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                processBtnCompress(event);
            }};

        btnCompress.addActionListener(compressAction);
        SwingUtils.bindKeyStrokeToAction(btnCompress,
            KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0),
            "compressAction", compressAction);

        result.add(btnCompress, "span, wrap");

        // Compression log
        result.add(new JLabel("Compression Log:"), "span, growx, wrap");

        JPanel logPanel = new JPanel(new MigLayout(
            "insets 0", "[grow, fill] []", "[grow, fill]"));

        jtaCompressionLog = SwingUtils.createTextArea(
            new LimitedDocument(100000), "", 5, 10, false, true, true, true);
        JScrollPane scpCompressionLog = new JScrollPane(jtaCompressionLog);

        logPanel.add(scpCompressionLog, "growx, growy");

        JPanel logButtonsPanel = new JPanel(new MigLayout(
            "insets 0", "[]", "[] []"));

        JButton btnCopyLog = SwingUtils.createButton(
            null, "icons/silk/page_copy.png", null, "Copy log");
        btnCopyLog.addActionListener(this::processBtnCopyLog);

        logButtonsPanel.add(btnCopyLog, "sizegroup buttons, wrap");

        JButton btnClearLog = SwingUtils.createButton(
            null, "icons/silk/textfield.png", null, "Clear log");
        btnClearLog.addActionListener(this::processBtnClearLog);

        logButtonsPanel.add(btnClearLog, "sizegroup buttons");

        logPanel.add(logButtonsPanel);

        result.add(logPanel, "span, growx, growy, wrap");

        result.add(new JSeparator(JSeparator.HORIZONTAL), "span, growx, wrap");

        // Bottom panel
        JPanel bottomPanel = new JPanel(new MigLayout(
            "insets 0", "[] push []", ""));

        JButton btnAbout = SwingUtils.createButton(
            "About...", "icons/silk/information.png", null, null);
        btnAbout.addActionListener(this::processBtnAbout);

        bottomPanel.add(btnAbout, "sizegroup buttons");

        JButton btnClose = SwingUtils.createButton(
            "Close", "icons/silk/door_in.png", null, null);
        btnClose.addActionListener(this::processBtnClose);

        bottomPanel.add(btnClose, "sizegroup buttons");

        result.add(bottomPanel, "span, growx");

        return result;
    }

    private ComboBoxModel<ComboBoxItem<ConversionQuality>>
            createConversionQualityModel() {
        List<ConversionQuality> comboOptions =
            Arrays.stream(ConversionQuality.values())
                .collect(Collectors.toList());

        return ComboBoxItem.createComboBoxModel(comboOptions);
    }

    private DefaultComboBoxModel<ComboBoxItem<PdfCompatibilityLevel>>
            createPdfCompatibilityLevelModel() {
        List<PdfCompatibilityLevel> comboOptions =
            Arrays.stream(PdfCompatibilityLevel.values())
                .collect(Collectors.toList());

        return ComboBoxItem.createComboBoxModel(comboOptions);
    }

    private void showExceptionDialog(String errorMsg, Exception ex) {
        boolean endsInColonOrPeriod = (errorMsg != null) &&
            (errorMsg.endsWith(":") || errorMsg.endsWith("."));

        log.error(errorMsg + (endsInColonOrPeriod ? "" : ":"), ex);

        ExceptionDialog.createInstance(this, true,
            EXCEPTION_DLG_BOX_WIDTH, EXCEPTION_DLG_BOX_HEIGHT,
            errorMsg + (endsInColonOrPeriod ? "" : "."), ex).setVisible(true);
    }

    private void createExecutableFileChooser() {
        if (executableFileChooser != null) {
            return;
        }

        FileNameExtensionFilter executableFileFilter = new FileNameExtensionFilter(
            "Executable files", "exe", "com", "cmd", "bat", "sh");
        FileFilter gsWin64ExecFileFilter = new FileFilter() {
            @Override public boolean accept(File file) {
                return file.isDirectory() ||
                    GsUtils.GS_WIN64_EXEC_FILE_NAME.equalsIgnoreCase(file.getName());
            }

            @Override public String getDescription() {
                return "Ghostscript 64-bit Windows executable file";
            }
        };
        FileFilter gsWin32ExecFileFilter = new FileFilter() {
            @Override public boolean accept(File file) {
                return file.isDirectory() ||
                    GsUtils.GS_WIN32_EXEC_FILE_NAME.equalsIgnoreCase(file.getName());
            }

            @Override public String getDescription() {
                return "Ghostscript 32-bit Windows executable file";
            }
        };
        AllFilesFilter allFilesFilter = new AllFilesFilter();

        executableFileChooser = new JFileChooser();
        executableFileChooser.setAcceptAllFileFilterUsed(false);
        executableFileChooser.addChoosableFileFilter(executableFileFilter);
        executableFileChooser.addChoosableFileFilter(gsWin64ExecFileFilter);
        executableFileChooser.addChoosableFileFilter(gsWin32ExecFileFilter);
        executableFileChooser.addChoosableFileFilter(allFilesFilter);
        executableFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        executableFileChooser.setMultiSelectionEnabled(false);
        executableFileChooser.setDialogTitle("Choose executable file");

        executableFileChooser.setFileFilter(executableFileFilter);
    }

    private void processBtnBrowseGsExecutablePath(ActionEvent event) {
        createExecutableFileChooser();

        String gsExecutablePath = jtfGsExecutablePath.getText();

        if (!Utils.stringIsEmptyOrBlank(gsExecutablePath)) {
            File gsExecutable = new File(gsExecutablePath);
            executableFileChooser.setSelectedFile(gsExecutable);
        }

        if (executableFileChooser.showOpenDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        jtfGsExecutablePath.setText(
            executableFileChooser.getSelectedFile().getPath());
    }
    private void processBtnViewGsUsageHelp(ActionEvent event) {
        if (Utils.stringIsEmptyOrBlank(gsUsageHelpUrl)) {
            SwingUtils.showErrorMessage(this,
                "The Ghostscript usage help URL is empty.\n" +
                "Please specify a valid URL in the configuration file of this program.");
            return;
        }

        UrlValidator urlValidator = new UrlValidator(
            new String[] { "http", "https" });

        if (!urlValidator.isValid(gsUsageHelpUrl)) {
            SwingUtils.showErrorMessage(this,
                "The Ghostscript usage help URL does not seem to be valid:\n\n" +
                "  " + gsUsageHelpUrl + "\n\n" +
                "Please specify a valid URL in the configuration file of this program.");
            return;
        }

        try {
            Desktop.getDesktop().browse(URI.create(gsUsageHelpUrl));
        } catch (IOException ex) {
            showExceptionDialog("Error browsing to Ghostscript usage help", ex);
        }
    }

    private void createPdfFileChooser() {
        if (pdfFileChooser != null) {
            return;
        }

        FileNameExtensionFilter pdfFileFilter = new FileNameExtensionFilter(
            "PDF files", "pdf");
        AllFilesFilter allFilesFilter = new AllFilesFilter();

        pdfFileChooser = new JFileChooser();
        pdfFileChooser.setAcceptAllFileFilterUsed(false);
        pdfFileChooser.addChoosableFileFilter(pdfFileFilter);
        pdfFileChooser.addChoosableFileFilter(allFilesFilter);
        pdfFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        pdfFileChooser.setMultiSelectionEnabled(false);
        pdfFileChooser.setDialogTitle("Choose PDF file");

        pdfFileChooser.setFileFilter(pdfFileFilter);
    }

    private void processBtnBrowseInputFile(ActionEvent event) {
        createPdfFileChooser();

        String inputFilePath = jtfInputFile.getText();

        if (Utils.stringIsEmptyOrBlank(inputFilePath)) {
            if (!Utils.stringIsEmptyOrBlank(defaultInputFileDirPath)) {
                pdfFileChooser.setCurrentDirectory(
                    new File(defaultInputFileDirPath));
            }
        }
        else {
            File inputFile = new File(inputFilePath);
            pdfFileChooser.setSelectedFile(inputFile);
        }

        if (pdfFileChooser.showOpenDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        jtfInputFile.setText(pdfFileChooser.getSelectedFile().getPath());
    }

    private void processBtnViewInputFile(ActionEvent event) {
        String inputFilePath = jtfInputFile.getText();
        File inputFile = new File(inputFilePath);

        if (!inputFile.exists()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the input file does not exist.");
            jtfInputFile.requestFocusInWindow();
            return;
        }

        if (!inputFile.isFile()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the input file does not refer to " +
                "a file.\n" +
                "It is probably a folder.");
            jtfInputFile.requestFocusInWindow();
            return;
        }

        try {
            Desktop.getDesktop().open(inputFile);
        } catch (IOException ex) {
            showExceptionDialog("Error opening input file", ex);
        }
    }

    private void processBtnBrowseOutputFile(ActionEvent event) {
        createPdfFileChooser();

        String outputFilePath = jtfOutputFile.getText();

        if (Utils.stringIsEmptyOrBlank(outputFilePath)) {
            if (!Utils.stringIsEmptyOrBlank(defaultOutputFileDirPath)) {
                pdfFileChooser.setCurrentDirectory(
                    new File(defaultOutputFileDirPath));
            }
        }
        else {
            File outputFile = new File(outputFilePath);
            pdfFileChooser.setSelectedFile(outputFile);
        }

        if (pdfFileChooser.showOpenDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        jtfOutputFile.setText(pdfFileChooser.getSelectedFile().getPath());
    }

    private void processBtnViewOutputFile(ActionEvent event) {
        String outputFilePath = jtfOutputFile.getText();
        File outputFile = new File(outputFilePath);

        if (!outputFile.exists()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the output file does not exist.");
            jtfOutputFile.requestFocusInWindow();
            return;
        }

        if (!outputFile.isFile()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the output file does not refer to " +
                "a file.\n" +
                "It is probably a folder.");
            jtfOutputFile.requestFocusInWindow();
            return;
        }

        try {
            Desktop.getDesktop().open(outputFile);
        } catch (IOException ex) {
            showExceptionDialog("Error opening output file", ex);
        }
    }

    private void processBtnCompress(ActionEvent event) {
        // Validation.
        // Ghostscript executable. If the executable does not have a parent
        // file, it most likey is in the system path
        String gsExecutablePath = jtfGsExecutablePath.getText();
        File gsExecutable = new File(gsExecutablePath);
        File gsExecutableParent = gsExecutable.getParentFile();

        if (gsExecutableParent != null) {
            if (!gsExecutable.exists()) {
                SwingUtils.showErrorMessage(this,
                    "The path specified for the Ghostscript executable file " +
                    "does not exist.");
                jtfGsExecutablePath.requestFocusInWindow();
                return;
            }

            if (!gsExecutable.isFile()) {
                SwingUtils.showErrorMessage(this,
                    "The path specified for the Ghostscript executable file " +
                    "does not refer to a file.\n" +
                    "It is probably a folder.");
                jtfGsExecutablePath.requestFocusInWindow();
                return;
            }
        }

        // Conversion quality
        Object selItem = cboConversionQuality.getSelectedItem();
        @SuppressWarnings("unchecked")
        ConversionQuality conversionQuality = (selItem != null) ?
            ((ComboBoxItem<ConversionQuality>) selItem).getItem() : null;

        if (conversionQuality == null) {
            SwingUtils.showErrorMessage(this,
                "Please specify a valid value for the conversion quality.");
            cboConversionQuality.requestFocusInWindow();
            cboConversionQuality.setPopupVisible(true);
            return;
        }

        // PDF compatibility level
        selItem = cboPdfCompatibilityLevel.getSelectedItem();
        @SuppressWarnings("unchecked")
        PdfCompatibilityLevel pdfCompatibilityLevel = (selItem != null) ?
            ((ComboBoxItem<PdfCompatibilityLevel>) selItem).getItem() : null;

        if (pdfCompatibilityLevel == null) {
            SwingUtils.showErrorMessage(this,
                "Please specify a valid value for the PDF compatibility level.");
            cboPdfCompatibilityLevel.requestFocusInWindow();
            cboPdfCompatibilityLevel.setPopupVisible(true);
            return;
        }

        // Input file
        String inputFilePath = jtfInputFile.getText();
        File inputFile = new File(inputFilePath);

        if (!inputFile.exists()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the input file does not exist.");
            jtfInputFile.requestFocusInWindow();
            return;
        }

        if (!inputFile.isFile()) {
            SwingUtils.showErrorMessage(this,
                "The path specified for the input file does not refer to " +
                "a file.\n" +
                "It is probably a folder.");
            jtfInputFile.requestFocusInWindow();
            return;
        }

        // Output file
        String outputFilePath = jtfOutputFile.getText();

        if (Utils.stringIsEmptyOrBlank(outputFilePath)) {
            SwingUtils.showErrorMessage(this,
                "The path of the output file can not be empty.");
            jtfOutputFile.requestFocusInWindow();
            return;
        }

        File outputFile = new File(outputFilePath);
        File outputFileParent = outputFile.getParentFile();

        if ((outputFileParent != null) && !outputFileParent.exists()) {
            SwingUtils.showErrorMessage(this,
                "The folder for the output file does not exist.");
            jtfOutputFile.requestFocusInWindow();
            return;
        }

        if (inputFile.equals(outputFile)) {
            SwingUtils.showErrorMessage(this,
                "The output file can not be the same as the input file.");
            jtfOutputFile.requestFocusInWindow();
            return;
        }

        // Prepares process execution
        String additionalGsParameters = jtfAdditionalGsParameters.getText();

        List<String> commands = new ArrayList<>();
        commands.add(jtfGsExecutablePath.getText());
        commands.add("-sDEVICE=pdfwrite");
        commands.add("-dNOPAUSE");
        //commands.add("-dQUIET");
        commands.add("-dBATCH");
        commands.add("-dPDFSETTINGS=" + conversionQuality.getCommandLineArgument());
        commands.add("-sOutputFile=" + outputFilePath);

        if (!Utils.stringIsEmptyOrBlank(
                pdfCompatibilityLevel.getCommandLineArgument())) {
            commands.add("-dCompatibilityLevel=" +
                pdfCompatibilityLevel.getCommandLineArgument());
        }

        if (!additionalGsParameters.trim().isEmpty()) {
            commands.add(additionalGsParameters);
        }

        commands.add(inputFilePath);

        // Outputs full execution command to compression log
        StringBuilder fullCommand = new StringBuilder();

        for (int i = 0, len = commands.size(); i < len; ++i) {
            if (i > 0) {
                fullCommand.append(' ');
            }

            fullCommand.append(commands.get(i));
        }

        jtaCompressionLog.append(
            "Executing Ghostscript:\n" + fullCommand + "\n\n");

        // Actual process execution
        ProcessExecutionDialog executionDlg = null;
        List<String> initialInputStreamLines = null;
        List<String> initialErrorStreamLines = null;
        BufferedReader inputStreamReader = null;
        BufferedReader errorStreamReader = null;
        Integer exitValue = null;

        try {
            // Sets up the process builder
            ProcessBuilder procBuilder = new ProcessBuilder(commands);
            procBuilder.redirectErrorStream(true);

            if (gsExecutableParent != null) {
                procBuilder.directory(gsExecutable.getParentFile());
            }

            // Creates and shows a modal dialog to execute the process.
            // The process is run when the dialog gets shown, and the dialog
            // closes automatically when the process ends
            executionDlg = ProcessExecutionDialog.createInstance(
                this, true, 0, 0, procBuilder);
            executionDlg.setVisible(true);

            // Reads process execution info
            ProcessExecutionInfo processExecutionInfo =
                executionDlg.getProcessExecutionInfo();

            if (processExecutionInfo != null) {
                initialInputStreamLines =
                    processExecutionInfo.getInitialInputStreamLines();
                initialErrorStreamLines =
                    processExecutionInfo.getInitialErrorStreamLines();
                inputStreamReader = processExecutionInfo.getInputStreamReader();
                errorStreamReader = processExecutionInfo.getErrorStreamReader();
                exitValue = processExecutionInfo.getExitValue();
            }

            // Throws any stored exceptions if need be
            if (executionDlg.getThreadExecutionException() != null) {
                throw executionDlg.getThreadExecutionException();
            }

            if ((processExecutionInfo != null) &&
                (processExecutionInfo.getExecutionException() != null)) {
                throw processExecutionInfo.getExecutionException();
            }
        } catch (Exception ex) {
            showExceptionDialog("Error running Ghostscript", ex);
        } finally {
            // Processes input stream
            boolean emittedLines = Utils.consumeElements(initialInputStreamLines,
                new Utils.AlwaysTrueIntegerBiFunction<>((line, lineNum) ->
                    jtaCompressionLog.append(line + "\n"))) > 0;

            try {
                emittedLines |= Utils.consumeLines(inputStreamReader,
                    new Utils.AlwaysTrueIntegerBiFunction<>((line, lineNum) ->
                        jtaCompressionLog.append(line + "\n"))) > 0;
            } catch (Exception ex) {
                showExceptionDialog("Error reading Ghostscript input stream", ex);
            }

            if (emittedLines) {
                jtaCompressionLog.append("\n");
            }

            // Processes error stream
            emittedLines = Utils.consumeElements(initialErrorStreamLines,
                new Utils.AlwaysTrueIntegerBiFunction<>((line, lineNum) ->
                    jtaCompressionLog.append(line + "\n"))) > 0;

            try {
                emittedLines |= Utils.consumeLines(errorStreamReader,
                    new Utils.AlwaysTrueIntegerBiFunction<>((line, lineNum) ->
                        jtaCompressionLog.append(line + "\n"))) > 0;
            } catch (Exception ex) {
                showExceptionDialog("Error reading Ghostscript error stream", ex);
            }

            if (emittedLines) {
                jtaCompressionLog.append("\n");
            }

            // Frees resources
            if (executionDlg != null) {
                executionDlg.dispose();
            }

            Utils.closeReader(inputStreamReader, true);
            Utils.closeReader(errorStreamReader, true);
        }

        // Processes exit value
        jtaCompressionLog.append("Ghostscript exit value: " +
            ((exitValue != null) ? exitValue.toString() : "(unavailable)") +
            '\n');

        if (exitValue == null) {
            return;
        }

        if (exitValue == 0) {
            long inputFileSize = inputFile.length();
            long outputFileSize = outputFile.length();

            if ((inputFileSize > 0) && (outputFileSize > 0)) {
                double sizeRatio = outputFileSize / (double) inputFileSize;
                DecimalFormat decFormat1d = new DecimalFormat("0.#");
                DecimalFormat decFormat2d = new DecimalFormat("0.##");
                String message;

                if (sizeRatio == 1.0d) {
                    message = "The input and output files have the same size, " +
                        Utils.formatFileSize(outputFileSize, decFormat2d) + ".";
                }
                else {
                    if (sizeRatio < 1.0d) {
                        message = "The output file has " +
                            Utils.formatFileSize(outputFileSize, decFormat2d) +
                            " and is " +
                            decFormat1d.format((1.0d - sizeRatio) * 100.0d) +
                            "% smaller than the input file.";
                    }
                    else {
                        message = "The output file has " +
                            Utils.formatFileSize(outputFileSize, decFormat2d) +
                            " and is unfortunately " +
                            decFormat1d.format((sizeRatio - 1.0d) * 100.0d) +
                            "% larger than the input file.";
                    }
                }

                jtaCompressionLog.append(message + "\n");
            }
        }
        else {
            jtaCompressionLog.append(
                "Please refer to the documentation of Ghostscript to " +
                "check the error for this exit value.\n");
        }

        jtaCompressionLog.append("\n");
    }

    private void processBtnCopyLog(ActionEvent event) {
        SwingUtils.copyTextToClipboard(jtaCompressionLog.getText(), null);
    }

    private void processBtnClearLog(ActionEvent event) {
        jtaCompressionLog.setText("");
    }

    private void processBtnAbout(ActionEvent event) {
        AboutDialog.createInstance(this, true, 600, 500).setVisible(true);
    }

    private void processBtnClose(ActionEvent event) {
        confirmExit();
    }
}
