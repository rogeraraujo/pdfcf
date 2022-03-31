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

package com.rogeraraujo.pdfcf.gs;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Utility class to provide Ghostscript-related methods.
 */
public class GsUtils {
    // Private constructor to prevent instantiation
    private GsUtils() { }

    public static final String DEFAULT_GS_USAGE_HELP_URL =
        "https://ghostscript.com/doc/current/Use.htm";

    private static final String WIN_PROGRAM_FILES_DIR_NAME =
        "Program Files";
    private static final String WIN_PROGRAM_FILES_X86_DIR_NAME =
        "Program Files (x86)";
    private static final String GS_BASE_DIR_NAME = "gs";
    private static final String GS_BIN_DIR_NAME = "bin";
    public static final String GS_WIN64_EXEC_FILE_NAME = "gswin64c.exe";
    public static final String GS_WIN32_EXEC_FILE_NAME = "gswin32c.exe";

    public static final String GS_DEBIAN_LINUX_EXEC_FILE_PATH = "/usr/bin/gs";
    private static final String GS_UNIX_EXEC_FILE_NAME = "gs";

    /**
     * Implementation of FilenameFilter that accepts any directory and
     * rejects regular files.
     */
    public static class AcceptDirectoriesFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            return file.isDirectory();
        }
    }

    /**
     * Seeks and returns the path of the Ghostscript executable file in the
     * Windows platform, or returns an empty String in case said path can not
     * be found. This simple implementation only examines a fixed set of
     * directories and file names in the C: and D: drives. See:
     *
     *   https://ghostscript.com/doc/current/Use.htm
     */
    public static String seekGsExecutablePathWindows() {
        String[] driveLetters = { "C", "D" };
        String[] programFilesDirs = {
            WIN_PROGRAM_FILES_DIR_NAME, WIN_PROGRAM_FILES_X86_DIR_NAME };
        AcceptDirectoriesFilter acceptDirsFilter = new AcceptDirectoriesFilter();

        for (String driveLetter : driveLetters) {
            for (String programFilesDir : programFilesDirs) {
                File baseDir = new File(driveLetter + ":/" +
                    programFilesDir + "/" + GS_BASE_DIR_NAME);

                if (!(baseDir.exists() && baseDir.isDirectory())) {
                    continue;
                }

                File[] subDirs = baseDir.listFiles(acceptDirsFilter);

                if ((subDirs == null) || (subDirs.length < 1)) {
                    continue;
                }

                for (File gsVersionDir : subDirs) {
                    File binDir = new File(gsVersionDir, GS_BIN_DIR_NAME);
                    File execFile = new File(binDir, GS_WIN64_EXEC_FILE_NAME);

                    if (execFile.exists() && execFile.isFile()) {
                        return execFile.getPath();
                    }

                    execFile = new File(binDir, GS_WIN32_EXEC_FILE_NAME);

                    if (execFile.exists() && execFile.isFile()) {
                        return execFile.getPath();
                    }
                }
            }
        }

        return "";
    }

    /**
     * Seeks and returns the path of the Ghostscript executable file in the
     * Linux platform, and returns a default value in case said path can not
     * be found. This simple implementation only examines a fixed file name
     * expected in Debian, Ubuntu, Fedora, Suse and Arch Linux systems. In
     * case this file does not exist, the "gs" value is returned, which assumes
     * the Ghostscript executable is accessible from the system path. See:
     *
     *   https://ghostscript.com/doc/current/Use.htm
     */
    public static String seekGsExecutablePathLinux() {
        File execFile = new File(GS_DEBIAN_LINUX_EXEC_FILE_PATH);

        if (execFile.exists() && execFile.isFile()) {
            return execFile.getPath();
        }

        return GS_UNIX_EXEC_FILE_NAME;
    }
}
