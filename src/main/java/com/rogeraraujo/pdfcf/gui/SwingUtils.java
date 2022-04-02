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
import com.rogeraraujo.pdfcf.components.CustomListCellRenderer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class that provides Swing-related methods.
 */
public class SwingUtils {
    public static final String APP_WINDOW_TITLE = "PDF Compression Frontend";

    public static final String OPTION_PANE_ERROR_ICON_KEY =
        "OptionPane.errorIcon";

    // Private constructor to prevent instantiation
    private SwingUtils() { }

    /**
     * Centers a window on a specific screen. If the index of the screen
     * provided by the caller is null, this method tries to center the window
     * in the screen that it is currently located on.
     *
     * @param window Window to center
     * @param screen Index of the screen that the window should be centered in
     */
    public static void centerWindow(Window window, Integer screen) {
        // See: https://stackoverflow.com/a/19746437
        GraphicsConfiguration gfxConfig = null;

        if (screen == null) {
            gfxConfig = window.getGraphicsConfiguration();
        }
        else {
            GraphicsDevice[] screenDevices = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();

            if ((screenDevices != null) &&
                (screenDevices.length > 0) &&
                (screen >= 0) &&
                (screen < screenDevices.length)) {
                gfxConfig = screenDevices[screen].getDefaultConfiguration();
            }
        }

        if (gfxConfig == null) {
            GraphicsDevice defaultScreenDev = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();
            gfxConfig = defaultScreenDev.getDefaultConfiguration();
        }

        if (gfxConfig == null) {
            window.setLocationRelativeTo(null);
            return;
        }

        Rectangle gfxBounds = gfxConfig.getBounds();
        int winX = ((gfxBounds.width - window.getWidth()) / 2) + gfxBounds.x;
        int winY = ((gfxBounds.height - window.getHeight()) / 2) + gfxBounds.y;

        window.setLocation(winX, winY);
    }

    /**
     * Creates a JTextField instance.
     *
     * @param document Document to apply to the created instance; can be null
     * @param text Text to apply to the created instance; can be null
     * @param columns Number of columns to apply to the created instance; only
     *                used if >= 0
     * @param editable Flag indicating whether the created instance should be
     *                 editable
     *
     * @return A newly-created JTextField instance
     */
    public static JTextField createTextField(
            Document document, String text, int columns, boolean editable) {
        JTextField result = (columns >= 0) ?
            new JTextField(columns) : new JTextField();

        if (document != null) {
            result.setDocument(document);
        }

        if (text != null) {
            result.setText(text);
        }

        result.setEditable(editable);

        return result;
    }

    /**
     * Loads an icon from the classpath, returning null if the icon cannot be
     * found.
     *
     * @param iconResourceName Resource name of the icon to load; can be null
     *
     * @return A newly-created Icon instance
     */
    public static Icon loadIcon(String iconResourceName) {
        if (Utils.stringIsEmpty(iconResourceName)) {
            return null;
        }

        URL url = SwingUtils.class.getClassLoader()
            .getResource(iconResourceName);

        return (url != null) ?
            new ImageIcon(url) : null;
    }

    /**
     * Creates a JButton instance.
     *
     * @param text Text to apply to the created instance; can be null
     * @param iconResourceName Resource name of the icon to apply to the created
     *                         instance; can be null
     * @param action Action to bind to the created instance; can be null
     * @param toolTipText Tool tip text to apply to the created instance; can be
     *                    null
     *
     * @return A newly-created JButton instance
     */
    public static JButton createButton(
            String text, String iconResourceName, Action action,
            String toolTipText) {
        JButton result = (text != null) ? new JButton(text) : new JButton();

        if (iconResourceName != null) {
            Icon icon = loadIcon(iconResourceName);

            if (icon != null) {
                result.setIcon(icon);
            }
        }

        if (action != null) {
            result.setAction(action);
        }

        if (toolTipText != null) {
            result.setToolTipText(toolTipText);
        }

        return result;
    }

    /**
     * Creates a JComboBox instance. The main motivation of this method is the
     * [componentTransformer] argument, which allows the caller to fully
     * customize the components used to render the cells of the JComboBox
     * (please see the CustomListCellRenderer class for further details).
     *
     * @param model Model to apply to the created instance; can be null
     * @param componentTransformer Component transformer to apply to the
     *                             created instance; can be null
     * @param selectedIndex Index of the item to initially select in the
     *                      created instance; can be null
     * @param <T> Type of the items contained in the combo box
     *
     * @return A newly-created JComboBox instance
     */
    @SuppressWarnings("unchecked")
    public static <T> JComboBox<T> createComboBox(
            ComboBoxModel<T> model,
            CustomListCellRenderer.ComponentTransformer componentTransformer,
            Integer selectedIndex) {
        JComboBox<T> result = (model != null) ?
            new JComboBox<>(model) : new JComboBox<>();

        if (componentTransformer != null) {
            result.setRenderer(new CustomListCellRenderer(
                (ListCellRenderer<Object>) result.getRenderer(),
                componentTransformer));
        }

        if (selectedIndex != null) {
            result.setSelectedIndex(selectedIndex);
        }

        return result;
    }

    /**
     * Creates a JTextArea instance.
     *
     * @param document Document to apply to the created instance; can be null
     * @param text Text to apply to the created instance; can be null
     * @param rows Number of rows to apply to the created instance; only used
     *             used if >= 0
     * @param columns Number of columns to apply to the created instance; only
     *                used if >= 0
     * @param editable Flag indicating whether the created instance should be
     *                 editable
     * @param lineWrap Flag indicating whether to activate line wraps in the
     *                 created instance
     * @param overrideTabKeyStroke Flag indicating whether to make the Tab and
     *                             Shift-Tab keystrokes work as focus control
     *                             keys instead of inserting tab characters
     * @param useTextFieldFont Flag indicating whether to replace the default
     *                         JTextArea font by the font used in JTextField
     *
     * @return A newly-created JTextArea instance
     */
    public static JTextArea createTextArea(
            Document document, String text, int rows, int columns,
            boolean editable, boolean lineWrap, boolean overrideTabKeyStroke,
            boolean useTextFieldFont) {
        JTextArea result = new JTextArea();

        if (document != null) {
            result.setDocument(document);
        }

        if (text != null) {
            result.setText(text);
        }

        if (rows >= 0) {
            result.setRows(rows);
        }

        if (columns >= 0) {
            result.setColumns(columns);
        }

        result.setEditable(editable);

        if (lineWrap) {
            result.setLineWrap(true);
            result.setWrapStyleWord(true);
        }

        if (overrideTabKeyStroke) {
            result.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            result.setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        }

        if (useTextFieldFont) {
            Font font = UIManager.getFont("TextField.font");

            if (font == null) {
                font = new JTextField().getFont();
            }

            if (font != null) {
                result.setFont(font);
            }
        }

        return result;
    }

    /**
     * Copies text to the clipboard.
     *
     * @param text The text to copy
     * @param owner Object that must be notified when another object or
     *              application takes ownership of the clipboard; can be null
     */
    public static void copyTextToClipboard(String text, ClipboardOwner owner) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), owner);
    }

    /**
     * Displays an error message in a modal dialog box.
     *
     * @param parentComponent Parent component of the dialog box; can be null
     * @param message Error message to display
     */
    public static void showErrorMessage(
            Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message,
            SwingUtils.APP_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an information message in a modal dialog box.
     *
     * @param parentComponent Parent component of the dialog box; can be null
     * @param message Error message to display
     */
    public static void showInformationMessage(
            Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message,
            SwingUtils.APP_WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Given a component, binds a keystroke to an action that must be performed
     * when the component is focused and the keystore occurs.
     *
     * @param component Component that, when focused, will respond to the
     *                  keystroke
     * @param keyStroke Keystroke that will perform the supplied action
     * @param actionMapKey Unique key used to register the action in the
     *                     component
     * @param action Action to perform when the supplied keystroke occurs
     */
    public static void bindKeyStrokeToAction(JComponent component,
            KeyStroke keyStroke, String actionMapKey, Action action) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            keyStroke, actionMapKey);
        component.getActionMap().put(actionMapKey, action);
    }

    /**
     * Increases all font sizes in a UIDefaults instance. As the
     * [increaseInPoints] argument can be negative, this method can be used to
     * decrease font sizes as well.
     *
     * @param uiDefaults UIDefault instance whose font sizes will be changed;
     *                   can be null
     * @param increaseInPoints Number of points used to increase font sizes;
     *                         can be negative. Values of zero are ignored
     * @param changedFontKeys Set containing the font keys already changed;
     *                        can be used to avoid changing a font more than
     *                        once
     */
    public static void increaseFontSizes(
            UIDefaults uiDefaults, int increaseInPoints,
            Set<String> changedFontKeys) {
        if ((uiDefaults == null) || (increaseInPoints == 0)) {
            return;
        }

        // We use a List because attempting to directly iterate
        // uiDefaults.keySet() may cause ConcurrentModificationException
        List<Object> keys = new ArrayList<>(uiDefaults.keySet());

        for (Object key : keys) {
            String strKey = key.toString();

            if (changedFontKeys.contains(strKey)) {
                continue;
            }

            Object value = uiDefaults.get(key);
            Font newFont = null;

            if (value instanceof FontUIResource) {
                FontUIResource curFont = (FontUIResource) value;
                newFont = new FontUIResource(
                    curFont.getFamily(), curFont.getStyle(),
                    curFont.getSize() + increaseInPoints);
            }
            else if (value instanceof Font) {
                Font curFont = (Font) value;
                newFont = new Font(
                    curFont.getFamily(), curFont.getStyle(),
                    curFont.getSize() + increaseInPoints);
            }

            if (newFont != null) {
                uiDefaults.put(key, newFont);
                changedFontKeys.add(strKey);
            }
        }
    }
}
