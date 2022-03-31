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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to exercise the functionality of the Utils class.
 */
@Slf4j
class UtilsTest {
    @Test
    void stringIsEmptyTest() {
        assertTrue(Utils.stringIsEmpty(null));
        assertTrue(Utils.stringIsEmpty(""));
        assertFalse(Utils.stringIsEmpty(" "));
        assertFalse(Utils.stringIsEmpty("a"));
    }

    @Test
    void stringIsEmptyOrBlankTest() {
        assertTrue(Utils.stringIsEmptyOrBlank(null));
        assertTrue(Utils.stringIsEmptyOrBlank(""));
        assertTrue(Utils.stringIsEmptyOrBlank(" "));
        assertTrue(Utils.stringIsEmptyOrBlank("  "));
        assertFalse(Utils.stringIsEmptyOrBlank("a"));
    }

    @Test
    void objectToStrTest() {
        assertNull(Utils.objectToStr(null, null));
        assertEquals(Utils.objectToStr(null, ""), "");
        assertEquals(Utils.objectToStr(null, "a"), "a");
        assertEquals(Utils.objectToStr(1L, null), "1");
    }

    @Test
    void readTextFromResourceTest() throws IOException {
        Charset charset = StandardCharsets.UTF_8;

        assertNull(Utils.readTextFromResource(
            null, charset));
        assertEquals(Utils.readTextFromResource(
            "text/empty.txt", charset), "");
        assertEquals(Utils.readTextFromResource(
            "text/one-line.txt", charset), "Line 1");
        assertEquals(Utils.readTextFromResource(
            "text/two-lines.txt", charset), "\nLine 2");
        assertEquals(Utils.readTextFromResource(
            "text/three-lines.txt", charset), "\n\nLine 3");
        assertEquals(Utils.readTextFromResource(
                "text/last-line-empty.txt", charset),
            "The next (and last) line is empty\n");
    }

    @Test
    void stringToIntTest() {
        assertNull(Utils.stringToInt(null));
        assertNull(Utils.stringToInt(""));
        assertNull(Utils.stringToInt(" "));
        assertNull(Utils.stringToInt("a"));
        assertNull(Utils.stringToInt("1 -1"));
        assertNull(Utils.stringToInt(" 1 -1 "));
        assertNull(Utils.stringToInt("+1 -1"));
        assertNull(Utils.stringToInt(" +1 -1 "));

        assertEquals(Utils.stringToInt("1"), 1);
        assertEquals(Utils.stringToInt(" 1 "), 1);
        assertEquals(Utils.stringToInt("+1"), +1);
        assertEquals(Utils.stringToInt(" +1 "), +1);
        assertEquals(Utils.stringToInt("-1"), -1);
        assertEquals(Utils.stringToInt(" -1 "), -1);
    }

    @Test
    void repeatStringTest() {
        assertThrows(IllegalArgumentException.class,
            () -> Utils.repeatString(null, -1));
        assertNull(Utils.repeatString(null, 0));
        assertNull(Utils.repeatString(null, 1));

        assertThrows(IllegalArgumentException.class,
            () -> Utils.repeatString("", -1));
        assertEquals(Utils.repeatString("", 0), "");
        assertEquals(Utils.repeatString("", 1), "");
        assertEquals(Utils.repeatString("", 2), "");

        assertThrows(IllegalArgumentException.class,
            () -> Utils.repeatString("xy", -1));
        assertEquals(Utils.repeatString("xy", 0), "");
        assertEquals(Utils.repeatString("xy", 1), "xy");
        assertEquals(Utils.repeatString("xy", 2), "xyxy");
    }

    @Test
    void formatFileSizeTest() {
        assertEquals(Utils.formatFileSize(0, null), "0 bytes");
        assertEquals(Utils.formatFileSize(1, null), "1 byte");
        assertEquals(Utils.formatFileSize(-1, null), "-1 byte");
        assertEquals(Utils.formatFileSize(512, null), "512 bytes");
        assertEquals(Utils.formatFileSize(-512, null), "-512 bytes");

        DecimalFormat decFormat = new DecimalFormat("0.##");

        assertEquals(Utils.formatFileSize(Utils.ONE_KILOBYTE, null),
            "1 " + Utils.KILOBYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_KILOBYTE, null),
            "-1 " + Utils.KILOBYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(Utils.ONE_KILOBYTE * 3 / 2, decFormat),
            decFormat.format(1.5d) + " " + Utils.KILOBYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_KILOBYTE * 3 / 2, decFormat),
            "-" + decFormat.format(1.5d) + " " + Utils.KILOBYTE_SUFFIX);

        assertEquals(Utils.formatFileSize(Utils.ONE_MEGABYTE, null),
            "1 " + Utils.MEGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_MEGABYTE, null),
            "-1 " + Utils.MEGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(Utils.ONE_MEGABYTE * 3 / 2, decFormat),
            decFormat.format(1.5d) + " " + Utils.MEGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_MEGABYTE * 3 / 2, decFormat),
            "-" + decFormat.format(1.5d) + " " + Utils.MEGABYTE_SUFFIX);

        assertEquals(Utils.formatFileSize(Utils.ONE_GIGABYTE, null),
            "1 " + Utils.GIGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_GIGABYTE, null),
            "-1 " + Utils.GIGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(Utils.ONE_GIGABYTE * 3 / 2, decFormat),
            decFormat.format(1.5d) + " " + Utils.GIGABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_GIGABYTE * 3 / 2, decFormat),
            "-" + decFormat.format(1.5d) + " " + Utils.GIGABYTE_SUFFIX);

        assertEquals(Utils.formatFileSize(Utils.ONE_TERABYTE, null),
            "1 " + Utils.TERABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_TERABYTE, null),
            "-1 " + Utils.TERABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(Utils.ONE_TERABYTE * 3 / 2, decFormat),
            decFormat.format(1.5d) + " " + Utils.TERABYTE_SUFFIX);
        assertEquals(Utils.formatFileSize(-Utils.ONE_TERABYTE * 3 / 2, decFormat),
            "-" + decFormat.format(1.5d) + " " + Utils.TERABYTE_SUFFIX);
    }
}
