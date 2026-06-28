/*
 * MIT License
 *
 * Copyright (c) 2020, 2026 Mark Schmieder. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the fxgui Library
 *
 * You should have received a copy of the MIT License along with the fxgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgui
 */
module fxgui {
    exports com.mhschmieder.fxgui.application;
    exports com.mhschmieder.fxgui.concurrent.service;
    exports com.mhschmieder.fxgui.concurrent.task;
    exports com.mhschmieder.fxgui.demo;
    exports com.mhschmieder.fxgui.dialog;
    exports com.mhschmieder.fxgui.event;
    exports com.mhschmieder.fxgui.file;
    exports com.mhschmieder.fxgui.input;
    exports com.mhschmieder.fxgui.layout;
    exports com.mhschmieder.fxgui.print;
    exports com.mhschmieder.fxgui.stage;
    exports com.mhschmieder.fxgui.swing;
    exports com.mhschmieder.fxgui.util;
    requires commons.math3;
    requires fxcontrols;
    requires fxdxfimport;
    requires fxgraphics;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;
    requires jcommons;
    requires jcontrols;
    requires jgraphics;
    requires jgui;
    requires jmath;
    requires jphysics;
    requires jvectorexport;
    requires org.apache.commons.io;
    requires org.apache.commons.rng.api;
    requires org.apache.commons.rng.core;
    requires org.controlsfx.controls;
    requires org.jsoup;
}