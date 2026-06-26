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
package com.mhschmieder.fxgui.concurrent.service;

import com.mhschmieder.fxgui.dialog.LoginDialogUtilities;
import com.mhschmieder.fxgui.concurrent.task.AuthorizationRequestTask;
import com.mhschmieder.jcommons.net.AuthorizationServerResponse;
import com.mhschmieder.jcommons.net.HttpServletRequestProperties;
import com.mhschmieder.jcommons.security.LoginCredentials;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.scene.control.Dialog;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Implementation class for specifics of authorization server requests.
 */
public class AuthorizationRequestService extends ServerRequestService< AuthorizationServerResponse > {

    /** Cache the Login Credentials to use for authorizing the request. */
    protected LoginCredentials                 loginCredentials;

    /** Reference to a Login Dialog that instigates the authorization. */
    protected Dialog< Pair< String, String > > loginDialog;

    public AuthorizationRequestService( final HttpServletRequestProperties pServerRequestProperties,
                                        final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pServerRequestProperties,
               pClientProperties );

        loginCredentials = new LoginCredentials();

        // Not all authorization requests are launched from Login Dialogs.
        loginDialog = null;
        
        // Add callbacks for the Service API status tracking.
        addCallbacks();
    }

    @Override
    protected AuthorizationRequestTask createTask() {
        // Create a new Authorization Request Task.
        final AuthorizationRequestTask authorizationrequestTask =
                                                                new AuthorizationRequestTask( loginCredentials,
                                                                                              httpServletRequestProperties,
                                                                                              clientProperties );

        return authorizationrequestTask;
    }

    public void requestUserAuthorization( final LoginCredentials pLoginCredentials ) {
        // Disregard empty, null, or invalid Login Credentials.
        if ( !pLoginCredentials.isValid() ) {
            return;
        }

        // Make sure the authorization parameter sources are up to date.
        setLoginCredentials( pLoginCredentials );

        // Restart the Service as this also cancels old tasks and then resets.
        restart();
    }

    public LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }
    
    public void setLoginCredentials( final LoginCredentials pLoginCredentials ) {
        loginCredentials = pLoginCredentials;
    }

    public Dialog< Pair< String, String > > getLoginDialog() {
        return loginDialog;
    }

    public void setLoginDialog( final Dialog< Pair< String, String > > pLoginDialog ) {
        loginDialog = pLoginDialog;
    }
    
    public Callback< Pair< String, String >, Void > makeAuthenticator() {
        final Callback< Pair< String, String >, Void > authenticator = userInfo -> {
            // Contact the server's authorization service, if available, to
            // authorize the user.
            final LoginCredentials loginCredentialsCandidate = new LoginCredentials( userInfo.getKey(),
                                                                                     userInfo.getValue() );
            requestUserAuthorization( loginCredentialsCandidate );
            return null;
        };
        
        return authenticator;
    }
    
    /**
     * Add callbacks for the Service API status tracking, to handle the most
     * useful status values such as: scheduled, cancelled, failed, succeeded.
     * <p>
     * NOTE: This is meant to cover boilerplate behavior to avoid copy/paste
     *  in downstream clients, especially regarding the Server Login Dialog.
     */
    protected void addCallbacks() {
        // TODO: Tie this into the Login Dialog as a text field validator?
        setOnFailed( t -> {
            // Forward any service exceptions to the established logger.
            final Throwable exception = getException();
            exception.printStackTrace();

            // Alert the user that there were problems with the service.
            final String statusMessage = exception.toString();
            LoginDialogUtilities.showLoginWarningDialog( statusMessage );
        } );
    }
}
