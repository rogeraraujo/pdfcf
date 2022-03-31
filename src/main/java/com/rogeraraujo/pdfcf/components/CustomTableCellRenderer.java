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
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Customizes components used to render cells in JTables. This is done by
 * acquiring a component, which can be come from the base renderer of a JTable
 * or from the default functionality of DefaultTableCellRenderer, followed by
 * customizing said component (or replacing it altogether) through a transformer
 * interface.
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * Transformer interface that allows to customize or replace a component
     * generated by the TableCellRenderer#getTableCellRendererComponent()
     * method.
     */
    public interface ComponentTransformer {
        Component transform(Component baseComponent, JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column);
    }

    @Getter @Setter
    private TableCellRenderer baseRenderer;

    @Getter @Setter
    private ComponentTransformer componentTransformer;

    public CustomTableCellRenderer(
            TableCellRenderer baseRenderer,
            ComponentTransformer componentTransformer) {
        this.baseRenderer = baseRenderer;
        this.componentTransformer = componentTransformer;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component result = (baseRenderer != null) ?
            baseRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column) :
            super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (componentTransformer != null) {
            result = componentTransformer.transform(
                result, table, value, isSelected, hasFocus, row, column);
        }

        return result;
    }

    /**
     * Transformer that applies a "striping" effect by using an alternate color
     * for the background of items displayed in even lines of JTables.
     */
    public static class AlternateRowColorTransformer
            implements ComponentTransformer {
        public static final Color DEFAULT_ALTERNATE_ROW_COLOR =
            new Color(200, 201, 210);

        private static final Border DEFAULT_EMPTY_BORDER =
            BorderFactory.createEmptyBorder(1, 1, 1, 1);

        @Getter
        private final Color alternateRowColor;

        public AlternateRowColorTransformer(Color alternateRowColor) {
            this.alternateRowColor = (alternateRowColor != null) ?
                alternateRowColor : DEFAULT_ALTERNATE_ROW_COLOR;
        }

        @Override
        public Component transform(
                Component baseComponent, JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // This section is heavily based in the DefaultTableCellRenderer
            // source code. Checks for selected cell first
            Color selectedFg = null;
            Color selectedBg = null;
            JTable.DropLocation dropLocation = table.getDropLocation();

            if ((dropLocation != null) &&
                    !dropLocation.isInsertRow() &&
                    !dropLocation.isInsertColumn() &&
                    (dropLocation.getRow() == row) &&
                    (dropLocation.getColumn() == column)) {
                selectedFg = UIManager.getColor("Table.dropCellForeground");
                selectedBg = UIManager.getColor("Table.dropCellBackground");
                isSelected = true;
            }

            if (isSelected) {
                baseComponent.setForeground(selectedFg == null ?
                    table.getSelectionForeground() : selectedFg);
                baseComponent.setBackground(selectedBg == null ?
                    table.getSelectionBackground() : selectedBg);
            }
            else {
                // We only set the alternate row color if the cell is not
                // selected
                Color background = table.getBackground();

                if ((background == null) ||
                    (background instanceof javax.swing.plaf.UIResource)) {
                    Color tempColor = alternateRowColor;

                    if ((tempColor != null) && (row % 2 != 0)) {
                        background = tempColor;
                    }
                }

                baseComponent.setForeground(table.getForeground());
                baseComponent.setBackground(background);
            }

            // Sets the font
            baseComponent.setFont(table.getFont());

            // Checks for focused cell
            JComponent jComponent = null;

            if (baseComponent instanceof JComponent) {
                jComponent = (JComponent) baseComponent;
            }

            if (hasFocus) {
                Border border = null;

                if (isSelected) {
                    border = UIManager.getBorder(
                        "Table.focusSelectedCellHighlightBorder");
                }

                if (border == null) {
                    border = UIManager.getBorder(
                        "Table.focusCellHighlightBorder");
                }

                if (jComponent != null) {
                    jComponent.setBorder(border);
                }

                if (!isSelected && table.isCellEditable(row, column)) {
                    Color tempColor = UIManager.getColor("Table.focusCellForeground");

                    if (tempColor != null) {
                        baseComponent.setForeground(tempColor);
                    }

                    tempColor = UIManager.getColor("Table.focusCellBackground");

                    if (tempColor != null) {
                        baseComponent.setBackground(tempColor);
                    }
                }
            }
            else {
                if (jComponent != null) {
                    Border noFocusBorder = UIManager.getBorder(
                        "Table.cellNoFocusBorder");
                    jComponent.setBorder((noFocusBorder != null) ?
                        noFocusBorder : DEFAULT_EMPTY_BORDER);
                }
            }

            // Sets the cell value
            if (jComponent instanceof JLabel) {
                ((JLabel) jComponent).setText(
                    (value != null) ? value.toString() : "");
            }

            return baseComponent;
        }
    }
}
