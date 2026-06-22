/*
 * MIT License
 *
 * Copyright (c) 2026 Mark Schmieder. All rights reserved.
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
package com.mhschmieder.fxgui.service;

import com.mhschmieder.jcommons.net.HttpServletRequestProperties;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.concurrent.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract base class for service commonality between server requests.
 */
public abstract class ServerRequestService< T > extends Service< T > {

    /**
     * Cache the Server Request Properties (Build ID, Request Type, etc.).
     */
    protected final HttpServletRequestProperties httpServletRequestProperties;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public ServerRequestService( final HttpServletRequestProperties pHttpServletRequestProperties,
                                 final ClientProperties pClientProperties ) {
        httpServletRequestProperties = pHttpServletRequestProperties;
        clientProperties = pClientProperties;

        // Set the Service to use a Cached Thread Pool vs. the default daemon,
        // to protect against run-time cross-threading issues (especially in a
        // hybrid app), suspended threads, and for better performance.
        final ExecutorService executorService = Executors.newCachedThreadPool();
        setExecutor( executorService );
    }
}
