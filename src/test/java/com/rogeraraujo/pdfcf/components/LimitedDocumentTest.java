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

package com.rogeraraujo.pdfcf.components;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.swing.text.BadLocationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class to exercise the functionality of the LimitedDocument class.
 */
@Slf4j
class LimitedDocumentTest {
    @Test
    public void insertStringTest() throws BadLocationException {
        LimitedDocument doc1 = new LimitedDocument(0);
        doc1.insertString(doc1.getLength(), "xyz", null);
        doc1.insertString(doc1.getLength(), "xyz", null);
        doc1.insertString(doc1.getLength(), "xyz", null);
        assertEquals(doc1.getText(0, doc1.getLength()), "xyzxyzxyz");

        LimitedDocument doc2 = new LimitedDocument(-1);
        doc2.insertString(doc2.getLength(), "xyz", null);
        doc2.insertString(doc2.getLength(), "xyz", null);
        doc2.insertString(doc2.getLength(), "xyz", null);
        assertEquals(doc2.getText(0, doc2.getLength()), "xyzxyzxyz");

        LimitedDocument doc3 = new LimitedDocument(3);
        doc3.insertString(doc3.getLength(), "xy", null);
        doc3.insertString(doc3.getLength(), "xy", null);
        doc3.insertString(doc3.getLength(), "xy", null);
        assertEquals(doc3.getText(0, doc3.getLength()), "xyx");
    }
}
