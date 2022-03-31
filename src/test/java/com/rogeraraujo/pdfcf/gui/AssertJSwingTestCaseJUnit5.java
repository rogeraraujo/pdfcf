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

package com.rogeraraujo.pdfcf.gui;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;

/**
 * Base class to run Swing-based tests. It acts as a bridge between the
 * AssertJSwingJUnitTestCase class in AssertJ, which uses JUnit 4, and the
 * JUnit 5 testing infrastructure of this application.
 */
public abstract class AssertJSwingTestCaseJUnit5
        extends AssertJSwingJUnitTestCase {
    protected FrameFixture fixture;

    @BeforeAll
    public static void beforeAll() {
        // We need this method because AssertJSwingJUnitTestCase#setUpOnce()
        // is annotated with @BeforeClass (i.e., JUnit 4 style), and we are
        // using JUnit 5
        AssertJSwingJUnitTestCase.setUpOnce();
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        // We need this method because AssertJSwingJUnitTestCase#setUp()
        // is annotated with @Before (i.e., JUnit 4 style), and we are using
        // JUnit 5
        super.setUp();

        if (!GraphicsEnvironment.isHeadless()) {
            DummyFrame frame = GuiActionRunner.execute(DummyFrame::new);
            fixture = new FrameFixture(robot(), frame);
            fixture.show();
        }
    }

    @AfterEach
    public void afterEach() throws Exception {
        // We need this method because AssertJSwingJUnitTestCase#tearDown()
        // is annotated with @After (i.e., JUnit 4 style), and we are using
        // JUnit 5
        if (fixture != null) {
            fixture.cleanUp();
        }

        super.tearDown();
    }

    @AfterAll
    public static void afterAll() {
        // We need this method because AssertJSwingJUnitTestCase#tearDownOnce()
        // is annotated with @AfterClass (i.e., JUnit 4 style), and we are using
        // JUnit 5
        AssertJSwingJUnitTestCase.tearDownOnce();
    }
}
