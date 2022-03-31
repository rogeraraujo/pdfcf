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
import lombok.Setter;

import javax.swing.*;
import java.util.Collection;

/**
 * A wrapper for items displayed in JComboBoxes. The main motivation for this
 * class is that, by default, a JComboBox displays items based on the value of
 * their toString() method, and this class makes it easy to customize the
 * toString() method if need be.
 *
 * @param <T> The type of the wrapped items
 */
public class ComboBoxItem<T> {
    @Getter @Setter
    private T item;

    public ComboBoxItem(T item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return (item != null) ? item.toString() : null;
    }

    /**
     * Creates a new instance of this class based on a collection of items.
     *
     * @param items Collection of items to use; can be null
     * @param <T> The type of the wrapped items
     *
     * @return A new instance of this class
     */
    public static <T> DefaultComboBoxModel<ComboBoxItem<T>>
            createComboBoxModel(Collection<T> items) {
        DefaultComboBoxModel<ComboBoxItem<T>> result =
            new DefaultComboBoxModel<>();

        if ((items != null) && (items.size() > 0)) {
            for (T item : items) {
                result.addElement(new ComboBoxItem<>(item));
            }
        }

        return result;
    }

    /**
     * Sets the selected item of a combo box, where the selected item can be
     * either directly stored in the combo box model, or wrapped inside an
     * instance of ComboBoxItem.
     *
     * @param comboBox Combo box whose selected item will be changed
     * @param newItem The item to select; use null to clear the selection
     */
    public static void setSelectedItem(JComboBox<?> comboBox, Object newItem) {
        ComboBoxModel<?> model = comboBox.getModel();
        int len = model.getSize();

        for (int i = 0; i < len; ++i) {
            Object elem = model.getElementAt(i);

            if (((newItem == null) && (elem == null)) ||
                ((newItem != null) && (elem != null) &&
                    ((newItem == elem) || newItem.equals(elem)))) {
                comboBox.setSelectedIndex(i);
                return;
            }

            if (elem instanceof ComboBoxItem) {
                Object cbItem = ((ComboBoxItem<?>) elem).getItem();

                if (((newItem == null) && (cbItem == null)) ||
                    ((newItem != null) && (cbItem != null) &&
                        ((newItem == cbItem) || newItem.equals(cbItem)) )) {
                    comboBox.setSelectedIndex(i);
                    return;
                }
            }
        }

        if (newItem == null) {
            comboBox.setSelectedItem(null);
        }
    }
}
