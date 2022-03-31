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

import lombok.Getter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A plain, non-formatted Document that limits the maximum number of characters
 * that it can store. Meant to be used with instances of JTextField and
 * JTextArea.
 */
public class LimitedDocument extends PlainDocument {
    @Getter
    private final int maxSize;

    public LimitedDocument(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attrSet)
            throws BadLocationException {
        if ((maxSize < 1) || (str == null)) {
            super.insertString(offset, str, attrSet);
            return;
        }

        int strLen = str.length();
        int curLen = getLength();

        if (strLen + curLen <= maxSize) {
            super.insertString(offset, str, attrSet);
        }
        else {
            super.insertString(offset, str.substring(0, maxSize - curLen), attrSet);
        }
    }
}
