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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

/**
 * Utility class to provide miscellaneous methods.
 */
public class Utils {
    // Private constructor to prevent instantiation
    private Utils() { }

    public static final long ONE_KILOBYTE = 1024;
    public static final long ONE_MEGABYTE = ONE_KILOBYTE * 1024;
    public static final long ONE_GIGABYTE = ONE_MEGABYTE * 1024;
    public static final long ONE_TERABYTE = ONE_GIGABYTE * 1024;

    public static final String BYTE_SUFFIX = "byte";
    public static final String BYTES_SUFFIX = "bytes";
    public static final String KILOBYTE_SUFFIX = "KB";
    public static final String MEGABYTE_SUFFIX = "MB";
    public static final String GIGABYTE_SUFFIX = "GB";
    public static final String TERABYTE_SUFFIX = "TB";

    public static final int SECONDS_PER_MINUTE = 60;
    public static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
    public static final int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;

    public static final String SECONDS_SUFFIX = "s";
    public static final String MINUTES_SUFFIX = "m";
    public static final String HOURS_SUFFIX = "h";
    public static final String DAYS_SUFFIX = "d";

    /**
     * Checks whether a String is empty. Null Strings are considered empty.
     *
     * @param str String to check; can be null
     *
     * @return Boolean indicating whether the String is empty
     */
    public static boolean stringIsEmpty(String str) {
        return (str == null) || str.isEmpty();
    }

    /**
     * Checks whether a String is empty or blank. Null Strings are considered
     * empty.
     *
     * @param str String to check; can be null
     *
     * @return Boolean indicating whether the String is empty or blank
     */
    public static boolean stringIsEmptyOrBlank(String str) {
        return (str == null) || str.isEmpty() || (str.trim().length() < 1);
    }

    /**
     * Converts an Object to its String representation, allowing the use of a
     * default value in case the Object is null.
     *
     * @param obj Object to convert; can be null
     * @param defaultStr Default value to return in case the Object to convert
     *                   is null
     *
     * @return String representation of the object
     */
    public static String objectToStr(Object obj, String defaultStr) {
        return (obj != null) ? obj.toString() : defaultStr;
    }

    /**
     * Reads and returns the complete contents of a plain text resource file.
     *
     * @param resourceName Name of the resource file; can be null
     * @param charset Character set of the resource file
     *
     * @return Complete contents of the resource file
     *
     * @throws IOException If an error occurs when reading the resource file
     */
    public static String readTextFromResource(
            String resourceName, Charset charset) throws IOException {
        if (resourceName == null) {
            return null;
        }

        InputStream inpStream = null;
        InputStreamReader inpStreamReader = null;
        BufferedReader bufReader = null;
        StringBuilder result = new StringBuilder();

        try {
            // Tries to open the resource
            inpStream = Utils.class.getClassLoader()
                .getResourceAsStream(resourceName);

            if (inpStream == null) {
                return null;
            }

            // Reads the resource line by line
            inpStreamReader = (charset == null) ?
                new InputStreamReader(inpStream) :
                new InputStreamReader(inpStream, charset);
            bufReader = new BufferedReader(inpStreamReader);

            String lineStr = bufReader.readLine();
            int lineNum = 0;

            while (lineStr != null) {
                if (lineNum > 0) {
                    result.append('\n');
                }

                ++lineNum;
                result.append(lineStr);
                lineStr = bufReader.readLine();
            }
        } finally {
            // Frees resources
            if (bufReader != null) {
                try { bufReader.close(); }
                catch (Exception ignored) { }
            }

            if (inpStreamReader != null) {
                try { inpStreamReader.close(); }
                catch (Exception ignored) { }
            }

            if (inpStream != null) {
                try { inpStream.close(); }
                catch (Exception ignored) { }
            }
        }

        return result.toString();
    }

    /**
     * Converts a String to Integer, returning null in case the conversion
     * fails. Blank spaces in the beginning or the end of the String are
     * ignored.
     *
     * @param str String to convert; can be null
     *
     * @return Integer value of the String
     */
    public static Integer stringToInt(String str) {
        if (str == null) {
            return null;
        }

        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Creates a String composed of a specified input String repeated a number
     * of times.
     *
     * @param str Input String to repeat
     * @param count Number of repetitions
     *
     * @return String composed of the specified input String repeated a number
     *         of times
     */
    public static String repeatString(String str, int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                "Count (" + count + ") cannot be negative");
        }

        if (count == 0) {
            return (str != null) ? "" : null;
        }

        if ((str == null) || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder(str.length() * count);

        while (count > 0) {
            result.append(str);
            --count;
        }

        return result.toString();
    }

    /**
     * Formats a file size according to byte units -- bytes, kilobytes,
     * megabytes, gigabytes, and terabytes.
     *
     * @param sizeInBytes File size to format
     * @param decFormat DecimalFormat instance used to format fractional
     *                  values. If the caller supplies a null value, this
     *                  method uses a default format
     *
     * @return The formatted file size
     */
    public static String formatFileSize(
            long sizeInBytes, DecimalFormat decFormat) {
        String sign = (sizeInBytes >= 0) ? "" : "-";
        long absSizeInBytes = Math.abs(sizeInBytes);

        // Bytes
        if (absSizeInBytes == 0) {
            return "0 " + BYTES_SUFFIX;
        }

        if (absSizeInBytes == 1) {
            return sign + "1 " + BYTE_SUFFIX;
        }

        if (absSizeInBytes < ONE_KILOBYTE) {
            return sizeInBytes + " " + BYTES_SUFFIX;
        }

        // Kilobytes
        if (absSizeInBytes == ONE_KILOBYTE) {
            return sign + "1 " + KILOBYTE_SUFFIX;
        }

        if (decFormat == null) {
            decFormat = new DecimalFormat("0.##");
        }

        if (absSizeInBytes < ONE_MEGABYTE) {
            return decFormat.format(
                sizeInBytes / (double) ONE_KILOBYTE) + " " + KILOBYTE_SUFFIX;
        }

        // Megabytes
        if (absSizeInBytes == ONE_MEGABYTE) {
            return sign + "1 " + MEGABYTE_SUFFIX;
        }

        if (absSizeInBytes < ONE_GIGABYTE) {
            return decFormat.format(
                sizeInBytes / (double) ONE_MEGABYTE) + " " + MEGABYTE_SUFFIX;
        }

        // Gigabytes
        if (absSizeInBytes == ONE_GIGABYTE) {
            return sign + "1 " + GIGABYTE_SUFFIX;
        }

        if (absSizeInBytes < ONE_TERABYTE) {
            return decFormat.format(
                sizeInBytes / (double) ONE_GIGABYTE) + " " + GIGABYTE_SUFFIX;
        }

        // Terabytes and up
        if (absSizeInBytes == ONE_TERABYTE) {
            return sign + "1 " + TERABYTE_SUFFIX;
        }

        return decFormat.format(
            sizeInBytes / (double) ONE_TERABYTE) + " " + TERABYTE_SUFFIX;
    }

    /**
     * Formats an elapsed time according to time units -- seconds, minutes,
     * hours and days.
     *
     * @param timeInSeconds Elapsed time to format
     *
     * @return The formatted elapsed time
     */
    public static String formatElapsedTime(long timeInSeconds) {
        if (timeInSeconds == 0) {
            return 0 + SECONDS_SUFFIX;
        }

        String sign = (timeInSeconds >= 0) ? "" : "-";
        long remainingSeconds = Math.abs(timeInSeconds);

        long days = remainingSeconds / SECONDS_PER_DAY;
        remainingSeconds = remainingSeconds - (days * SECONDS_PER_DAY);

        long hours = remainingSeconds / SECONDS_PER_HOUR;
        remainingSeconds = remainingSeconds - (hours * SECONDS_PER_HOUR);

        long minutes = remainingSeconds / SECONDS_PER_MINUTE;
        remainingSeconds = remainingSeconds - (minutes * SECONDS_PER_MINUTE);

        StringBuilder result = new StringBuilder(sign);

        if (days > 0) {
            result.append(days).append(DAYS_SUFFIX);
        }

        if (hours > 0) {
            result.append(hours).append(HOURS_SUFFIX);
        }

        if (minutes > 0) {
            result.append(minutes).append(MINUTES_SUFFIX);
        }

        if (remainingSeconds > 0) {
            result.append(remainingSeconds).append(SECONDS_SUFFIX);
        }

        return result.toString();
    }
}
