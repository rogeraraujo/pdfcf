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

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class to exercise the functionality of the ComboBoxItem class.
 */
@Slf4j
class ComboBoxItemTest {
    @Test
    void toStringTest() {
        ComboBoxItem<?> cbItem = new ComboBoxItem<>(null);
        assertNull(cbItem.toString());

        Long longVal = 1L;
        cbItem = new ComboBoxItem<>(longVal);
        assertEquals(cbItem.toString(), longVal.toString());

        String str = "a";
        cbItem = new ComboBoxItem<>(str);
        assertEquals(cbItem.toString(), str);
    }

    @Test
    void createComboBoxModelTest() {
        List<Long> nullList = null;
        DefaultComboBoxModel<ComboBoxItem<Long>> cbModel1 =
            ComboBoxItem.createComboBoxModel(nullList);
        assertEquals(cbModel1.getSize(), 0);

        List<Long> longList = Arrays.asList(1L, 2L, 3L);
        DefaultComboBoxModel<ComboBoxItem<Long>> cbModel2 =
            ComboBoxItem.createComboBoxModel(longList);
        assertEquals(cbModel2.getSize(), longList.size());

        for (int i = 0, len = cbModel2.getSize(); i < len; ++i) {
            assertEquals(cbModel2.getElementAt(i).getItem(), longList.get(i));
        }

        List<String> strList = Arrays.asList("One", "Two", "Three");
        DefaultComboBoxModel<ComboBoxItem<String>> cbModel3 =
            ComboBoxItem.createComboBoxModel(strList);
        assertEquals(cbModel3.getSize(), strList.size());

        for (int i = 0, len = cbModel3.getSize(); i < len; ++i) {
            assertEquals(cbModel3.getElementAt(i).getItem(), strList.get(i));
        }
    }
}
