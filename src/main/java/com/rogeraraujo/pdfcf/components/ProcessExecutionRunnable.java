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
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Runnable capable of executing an external process,
 * and also of notifying listeners about the execution of said process. The
 * results of executing the external process are exposed through a number of
 * attributes.
 */
@Slf4j
public class ProcessExecutionRunnable implements Runnable {
    // Thread listeners need to implement this interface
    public interface PerListener {
        void notifyThreadStart(ProcessExecutionRunnable source);

        void notifyProcessCreation(ProcessExecutionRunnable source);

        void notifyThreadEnd(ProcessExecutionRunnable source);
    }

    private ProcessBuilder processBuilder;

    @Getter
    private Process process;

    @Getter
    private BufferedReader processOutputReader;

    @Getter
    private BufferedReader processErrorReader;

    @Getter
    private Integer processExitValue;

    @Getter
    private Exception processException;

    @Getter
    private List<PerListener> listeners = new ArrayList<>();

    public ProcessExecutionRunnable(ProcessBuilder processBuilder) {
        if (processBuilder == null) {
            throw new IllegalArgumentException("Process builder cannot be null");
        }

        this.processBuilder = processBuilder;
    }

    public void addListener(PerListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        listeners.add(listener);
    }

    public void removeListener(PerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        // Notifies thread start
        for (PerListener listener : listeners) {
            listener.notifyThreadStart(this);
        }

        try {
            // Creates the process and notifies listeners
            process = processBuilder.start();
            processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            processErrorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));

            for (PerListener listener : listeners) {
                listener.notifyProcessCreation(this);
            }

            // Waits for the process to end
            process.waitFor();

            processExitValue = process.exitValue();
        } catch (Exception ex) {
            processException = ex;
        }

        // Notifies thread end
        for (PerListener listener : listeners) {
            listener.notifyThreadEnd(this);
        }
    }

    /**
     * Terminates the external process started by this thread. If there is
     * no process to terminate, or if a process exists but it has already
     * finished executing, no action is taken.
     */
    public void destroyProcess() {
        if ((process != null) && process.isAlive()) {
            process.destroy();
        }
    }

    /**
     * Forcibly terminates the external process started by this thread.
     * If there is no process to terminate, or if a process exists but
     * it has already finished executing, no action is taken.
     */
    public void destroyProcessForcibly() {
        if ((process != null) && process.isAlive()) {
            process.destroyForcibly();
        }
    }
}