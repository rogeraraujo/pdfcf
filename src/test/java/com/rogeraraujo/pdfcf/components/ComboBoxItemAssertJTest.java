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

import com.rogeraraujo.pdfcf.gui.AssertJSwingTestCaseJUnit5;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class to exercise the functionality of ComboBoxItem in an actual
 * Swing execution context.
 */
@Slf4j
public class ComboBoxItemAssertJTest extends AssertJSwingTestCaseJUnit5 {
    @Override
    protected void onSetUp() {
        // Nothing to do
    }

    @Test
    public void setSelectedItemTest() {
        if (GraphicsEnvironment.isHeadless()) {
            log.warn("Headless system, cannot test");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            List<String> strList = Arrays.asList("One", "Two", "Three");
            DefaultComboBoxModel<ComboBoxItem<String>> cbModel =
                ComboBoxItem.createComboBoxModel(strList);

            JComboBox<ComboBoxItem<String>> comboBox = new JComboBox<>(cbModel);

            String itemToSelect = strList.get(1);
            ComboBoxItem.setSelectedItem(comboBox, itemToSelect);
            @SuppressWarnings("unchecked")
            ComboBoxItem<String> selItem = (ComboBoxItem<String>)
                comboBox.getSelectedItem();

            assertNotNull(selItem);
            assertEquals(selItem.getItem(), itemToSelect);
        });
    }
}
