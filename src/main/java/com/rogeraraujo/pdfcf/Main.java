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

package com.rogeraraujo.pdfcf;

import com.rogeraraujo.pdfcf.components.CustomTableCellRenderer;
import com.rogeraraujo.pdfcf.gui.MainWindow;
import com.rogeraraujo.pdfcf.gui.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileReader;
import java.util.*;

/**
 * The main entry point of this application.
 */
public class Main {
    static {
        // Configures Logback. See:
        //   https://stackoverflow.com/a/21886071
        // This property-based approach works in desktop apps, but in web apps
        // you might need to configure Logback differently. See:
        //   https://stackoverflow.com/a/21886322
        System.setProperty("logback.configurationFile", "./config/logback.xml");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> initializeGui(args));
    }

    public static Logger getLogger() {
        // We expose this Logger through a static method instead of a Lombok
        // annotation because we want to make sure that the Logback
        // configuration in the static{} section of this class gets executed
        // before anything else (including Lombok)
        return LoggerFactory.getLogger(Main.class);
    }

    private static void initializeGui(String[] args) {
        // Reads configuration file
        Properties config = new Properties();
        FileReader reader = null;

        try {
            reader = new FileReader("./config/pdfcf.properties");
            config.load(reader);
        } catch (Exception ex) {
            getLogger().error("Error reading configuration file:", ex);
        } finally {
            if (reader != null) {
                try { reader.close(); }
                catch (Exception ignored) { }
            }
        }

        // Lists available Look-and-Feels
        for (String arg : args) {
            if ("-listLafs".equalsIgnoreCase(arg)) {
                listAvailableLookAndFeels();
                break;
            }
        }

        // Configures the Look-and-Feel
        try {
            String className = config.getProperty("swing-look-and-feel-class-name");
            UIManager.setLookAndFeel(Utils.stringIsEmptyOrBlank(className) ?
                UIManager.getSystemLookAndFeelClassName() : className);
        } catch (Exception ex) {
            getLogger().error("Error setting Swing Look-and-Feel:", ex);
        }

        // Sets the alternate row for JTable
        UIDefaults[] arrDefaults = {
            UIManager.getDefaults(), UIManager.getLookAndFeelDefaults() };

        for (UIDefaults defaults : arrDefaults) {
            if (defaults != null) {
                defaults.putIfAbsent("Table.alternateRowColor",
                    CustomTableCellRenderer.AlternateRowColorTransformer
                        .DEFAULT_ALTERNATE_ROW_COLOR);
            }
        }

        // Increases Swing font sizes
        Integer increase = Utils.stringToInt(
            config.getProperty("font-size-increase-in-points", "0"));

        if ((increase != null) && (increase != 0)) {
            Set<String> changedKeys = new HashSet<>();

            for (UIDefaults defaults : arrDefaults) {
                if (defaults != null) {
                    try {
                        SwingUtils.increaseFontSizes(
                            defaults, increase, changedKeys);
                    } catch (Exception ex) {
                        getLogger().trace("Error increasing font sizes:", ex);
                    }
                }
            }
        }

        // Creates and displays the main window
        MainWindow mainWin = MainWindow.createInstance(config);
        mainWin.setVisible(true);
    }

    private static void listAvailableLookAndFeels() {
        UIManager.LookAndFeelInfo[] installedLafs =
                UIManager.getInstalledLookAndFeels();

        if (installedLafs.length > 0) {
            System.out.println("Listing Look-and-Feels...");
            Arrays.sort(installedLafs, Comparator.comparing(
                UIManager.LookAndFeelInfo::getClassName));

            for (UIManager.LookAndFeelInfo installedLaf : installedLafs) {
                System.out.println(installedLaf.getClassName());
            }

            System.out.println("Done.");
        }
        else {
            System.out.println("Could not obtain installed Look-and-Feels");
        }
    }
}
