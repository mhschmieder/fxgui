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
package com.mhschmieder.fxgui.task;

import com.mhschmieder.jcommons.net.AuthorizationServerResponse;
import com.mhschmieder.jcommons.net.HttpServletRequestProperties;
import com.mhschmieder.jcommons.net.NetworkUtilities;
import com.mhschmieder.jcommons.security.LoginCredentials;
import com.mhschmieder.jcommons.security.ServerLoginCredentials;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.net.HttpURLConnection;

public class AuthorizationRequestTask extends Task< AuthorizationServerResponse > {

    /** The Request Type name that this task will pass to the server. */
    @SuppressWarnings("nls") public static String AUTHORIZATION_REQUEST_TYPE = "Authorize User";

    /** Cache the Login Credentials to use for authorizing the request. */
    protected LoginCredentials                    loginCredentials;

    /**
     * Cache the Server Request Properties (Build ID, Client Type, etc.).
     */
    public HttpServletRequestProperties                httpServletRequestProperties;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties                       clientProperties;

    public AuthorizationRequestTask( final LoginCredentials pLoginCredentials,
                                     final HttpServletRequestProperties pServerRequestProperties,
                                     final ClientProperties pClientProperties ) {
        // Always call the super-constructor first!
        super();

        // TODO: Wrap the Login Credentials in an AuthorizationRequestContext
        // class, for future-proof API expansion later on?
        loginCredentials = pLoginCredentials;
        httpServletRequestProperties = pServerRequestProperties;
        clientProperties = pClientProperties;
    }

    @Override
    protected AuthorizationServerResponse call() throws InterruptedException {
        // Open a connection to the Authorization Servlet.
        final HttpURLConnection httpURLConnection = NetworkUtilities
                .getHttpURLConnection( httpServletRequestProperties.httpServletUrl );
        if ( httpURLConnection == null ) {
            final String urlConnectionStatus =
                                             "Server Connection Error: Authorization Service Not Found"; //$NON-NLS-1$
            final AuthorizationServerResponse authorizationServerResponse =
                                                                          new AuthorizationServerResponse( urlConnectionStatus,
                                                                                                           null,
                                                                                                           true,
                                                                                                           null,
                                                                                                           ServerLoginCredentials.EXPIRATION_DATE_DEFAULT,
                                                                                                           null,
                                                                                                           HttpURLConnection.HTTP_UNAVAILABLE );
            return authorizationServerResponse;
        }

        // Get the user's screen size, for Full Screen Mode and user statistics.
        // TODO: Also get and cache the minimum point, which may not be zero.
        // NOTE: This query is done on-the-fly as the user may switch screens
        // between server calls.
        final Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        final double screenWidth = visualBounds.getWidth();
        final double screenHeight = visualBounds.getHeight();

        // Add the HTTP request properties for the Authorization Servlet.
        NetworkUtilities.addServerRequestProperties( httpURLConnection,
                                                     AUTHORIZATION_REQUEST_TYPE,
                                                     loginCredentials,
                                                     httpServletRequestProperties,
                                                     clientProperties,
                                                     screenWidth,
                                                     screenHeight );

        // Request a user authorization based on Login Credentials.
        final String servletErrorMessage = NetworkUtilities.connectToServlet( httpURLConnection,
                                                                              "authorization" ); //$NON-NLS-1$
        if ( servletErrorMessage != null ) {
            final AuthorizationServerResponse authorizationServerResponse =
                                                                          new AuthorizationServerResponse( null,
                                                                                                           servletErrorMessage,
                                                                                                           true,
                                                                                                           null,
                                                                                                           ServerLoginCredentials.EXPIRATION_DATE_DEFAULT,
                                                                                                           null,
                                                                                                           HttpURLConnection.HTTP_UNAVAILABLE );
            return authorizationServerResponse;
        }

        // Handle the authorization servlet's HTTP status, and echo the
        // formatted error response to the user if an HTTP error code is
        // detected and/or the authorization failed.
        final AuthorizationServerResponse authorizationServerResponse = NetworkUtilities
                .getAuthorizationServerResponse( httpURLConnection );
        final String serverStatusMessage = authorizationServerResponse.getServerStatusMessage();
        if ( serverStatusMessage != null ) {
            authorizationServerResponse.setServerStatusMessage( serverStatusMessage );
            authorizationServerResponse
                    .setHttpResponseCode( authorizationServerResponse.getHttpResponseCode() );
        }

        return authorizationServerResponse;
    }
}
