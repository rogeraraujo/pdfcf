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

import lombok.Getter;

/**
 * This enumeration represents the different types of file conversion quality
 * provided by Ghostscript.
 */
public enum ConversionQuality {
    SCREEN("screen", "Screen Optimized (72 dpi, low resolution)", "/screen"),
    EBOOK("ebook", "eBook (150 dpi, medium resolution)", "/ebook"),
    PRINTER("printer", "Print Optimized (300 dpi, high resolution)", "/printer"),
    PREPRESS("prepress", "Prepress Optimized (300 dpi, high resolution)", "/prepress"),
    DEFAULT("default", "General use (can possibly generate larger output files)", "/default");

    @Getter
    private final String id;

    @Getter
    private final String description;

    @Getter
    private final String commandLineArgument;

    ConversionQuality(
            String id, String description, String commandLineArgument) {
        this.id = id;
        this.description = description;
        this.commandLineArgument = commandLineArgument;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ConversionQuality getInstance(String id) {
        if (id == null) {
            return null;
        }

        for (ConversionQuality cq : ConversionQuality.values()) {
            if (id.equals(cq.id)) {
                return cq;
            }
        }

        return null;
    }
}
