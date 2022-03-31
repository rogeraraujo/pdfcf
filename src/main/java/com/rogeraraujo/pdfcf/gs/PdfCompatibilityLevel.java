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
 * This enumeration represents the different PDF format compatibility levels
 * provided by Ghostscript.
 */
public enum PdfCompatibilityLevel {
    DEFAULT("default", "Default (determined by Ghostscript)", ""),
    V1_0("1.0", "1.0", "1.0"),
    V1_1("1.1", "1.1", "1.1"),
    V1_2("1.2", "1.2", "1.2"),
    V1_3("1.3", "1.3", "1.3"),
    V1_4("1.4", "1.4", "1.4"),
    V1_5("1.5", "1.5", "1.5"),
    V1_6("1.6", "1.6", "1.6"),
    V1_7("1.7", "1.7", "1.7"),
    V2_0("2.0", "2.0", "2.0");

    @Getter
    private final String id;

    @Getter
    private final String description;

    @Getter
    private final String commandLineArgument;

    PdfCompatibilityLevel(
            String id, String description, String commandLineArgument) {
        this.id = id;
        this.description = description;
        this.commandLineArgument = commandLineArgument;
    }

    @Override
    public String toString() {
        return description;
    }

    public static PdfCompatibilityLevel getInstance(String id) {
        if (id == null) {
            return null;
        }

        for (PdfCompatibilityLevel pcl : PdfCompatibilityLevel.values()) {
            if (id.equals(pcl.id)) {
                return pcl;
            }
        }

        return null;
    }
}
