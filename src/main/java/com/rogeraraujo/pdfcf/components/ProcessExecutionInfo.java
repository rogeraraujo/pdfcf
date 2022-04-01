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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the execution of an external process.
 */
@Getter @Setter
public class ProcessExecutionInfo {
    private Process process;

    private List<String> initialInputStreamLines = new ArrayList<>();

    private List<String> initialErrorStreamLines = new ArrayList<>();

    private BufferedReader inputStreamReader;

    private BufferedReader errorStreamReader;

    private Integer exitValue;

    private Exception executionException;

    public ProcessExecutionInfo(Process process) {
        this.process = process;
    }
}
