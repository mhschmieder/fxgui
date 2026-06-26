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
package com.mhschmieder.fxgui.concurrent.task;

import com.mhschmieder.jcommons.io.IoUtilities;
import com.mhschmieder.jcommons.net.DataRequestParameters;
import com.mhschmieder.jcommons.net.DataServerResponse;
import com.mhschmieder.jcommons.net.HttpServletRequestProperties;
import com.mhschmieder.jcommons.net.NetworkUtilities;
import com.mhschmieder.jcommons.security.LoginCredentials;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.DataUpdateType;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.net.HttpURLConnection;

/**
 * Base class for task commonality between server data requests.
 */
public class DataRequestTask extends Task< DataServerResponse > {

    /**
     * Cache the Server Request Properties (Build ID, Request Type, etc.).
     */
    protected final HttpServletRequestProperties httpServletRequestProperties;
    
    /**
     * Cache the Data Request Parameters (Login Credentials, Data Type, etc.).
     */
    protected final DataRequestParameters dataRequestParameters;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public final ClientProperties clientProperties;

    public DataRequestTask( final HttpServletRequestProperties pServerRequestProperties,
                            final DataRequestParameters pDataRequestParameters,
                            final ClientProperties pClientProperties ) {
        // Always call the super-constructor first!
        super();

        httpServletRequestProperties = pServerRequestProperties;
        dataRequestParameters = pDataRequestParameters;
        clientProperties = pClientProperties;
    }

    @Override
    protected DataServerResponse call() throws InterruptedException {
        // Set the task title.
        final String taskTitle = getTaskTitle();
        updateTitle( taskTitle );

        // Create an empty server response, even if it never gets set.
        DataServerResponse dataServerResponse = new DataServerResponse();
        String serverStatusMessage = null;

        // Declare the number of sub-tasks involved.
        final double numberOfSubTasks = 6.0d;

        try {
            // Default to an initial placeholder status message.
            updateMessage( "Searching for Data Service" );
            updateProgress( 0.0d, numberOfSubTasks );

            // Open a connection to the data servlet.
            // TODO: Make an HttpServerRequest class that holds the reused
            //  fields and only resets the ones that change per data request?
            // TODO: Throw exceptions with these messages instead, so we can
            //  consolidate the handling to the failure callback?
            final HttpURLConnection httpURLConnection = NetworkUtilities
                    .getHttpURLConnection( httpServletRequestProperties.httpServletUrl );
            if ( httpURLConnection == null ) {
                serverStatusMessage = "Server Connection Error: Data Service Not Found"; 
                dataServerResponse.setServerStatusMessage( serverStatusMessage );
                return dataServerResponse;
            }
            updateMessage( "Data Service Found" );
            updateProgress( 1.0d, numberOfSubTasks );

            // Get the user's screen size, for Full Screen Mode and user statistics.
            // TODO: Also get and cache the minimum point, which may not be zero.
            // NOTE: This query is done on-the-fly as the user may switch screens
            //  between server calls.
            final Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            final double screenWidth = visualBounds.getWidth();
            final double screenHeight = visualBounds.getHeight();

            // Add the HTTP request properties for the Data Servlet.
            updateMessage( "Generating Data Request" );
            updateMessage( "Preparing Data Request" );
            NetworkUtilities.addServerRequestProperties( httpURLConnection,
                                                         getDataRequestType(),
                                                         getLoginCredentials(),
                                                         httpServletRequestProperties,
                                                         clientProperties,
                                                         screenWidth,
                                                         screenHeight );

            // Add optional HTTP properties specific to this Data Request Type.
            addDataRequestProperties( httpURLConnection );

            // Request a data update from the server.
            updateMessage( "Connecting to Server" ); //$NON-NLS-1$
            serverStatusMessage = NetworkUtilities.connectToServlet( httpURLConnection,
                                                                     "data update" );
            if ( serverStatusMessage != null ) {
                dataServerResponse.setServerStatusMessage( serverStatusMessage );
                return dataServerResponse;
            }
            updateMessage( "Server Connection Established" );
            updateMessage( "Logging into Server and Sending Data Request" );
            updateProgress( 2.0d, numberOfSubTasks );

            // Send the optional data request input parameters to the server.
            serverStatusMessage = sendDataRequestInputParameters( httpURLConnection );
            if ( serverStatusMessage != null ) {
                dataServerResponse.setServerStatusMessage( serverStatusMessage );
                return dataServerResponse;
            }

            // Handle the data request servlet's HTTP status, and echo the
            // formatted error response to the user if an HTTP error code is
            // detected and/or an error message is returned.
            // NOTE: We set to indeterminate at this point, so the user doesn't
            // think the application is stuck/frozen.
            updateMessage( "Waiting for Data Response from Server" );
            // updateProgress( 3.0d, numberOfSubTasks );
            updateProgress( -1d, numberOfSubTasks );
            dataServerResponse = NetworkUtilities
                    .getDataServerResponse( httpURLConnection );
            if ( ( dataServerResponse == null )
                    || ( dataServerResponse.getServerStatusMessage() != null )
                    || ( dataServerResponse.getServletErrorMessage() != null ) ) {
                return dataServerResponse;
            }
            updateMessage( "Data Response Received" );

            // Load the data response files from the returned servlet stream.
            updateMessage( "Loading Data Response from Server" );
            updateProgress( 4.0d, numberOfSubTasks );
            final StringBuilder messageBuilder = new StringBuilder();
            final byte[] serverResponseData = IoUtilities
                    .saveRemoteStreamToByteArray( httpURLConnection, messageBuilder );
            if ( messageBuilder.length() > 0 ) {
                dataServerResponse.setServerStatusMessage( messageBuilder.toString() );
                return dataServerResponse;
            }
            updateMessage( "Data Response Loaded" );
            updateProgress( 5.0d, numberOfSubTasks );

            // Cache the Server Response Data for post-processing.
            dataServerResponse.setServerResponseData( serverResponseData );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
        finally {
            // Indicate that we are done with the task.
            updateProgress( numberOfSubTasks, numberOfSubTasks );
        }

        // If the user cancelled, throw an interrupted exception.
        if ( isCancelled() ) {
            updateMessage( "Data Request Task Cancelled" );
            throw new InterruptedException();
        }

        return dataServerResponse;
    }
   
    /**
     * Returns the text to use for updating the Title of this Task.
     * <p>
     * NOTE: This default implementation is generic and should be overridden.
     * 
     * @return The text to use for updating the Title of this Task
     */
    protected String getTaskTitle() {
        return "Data Update";
    }
  
    /**
     * Returns the Request Type name that this task will pass to the server.
     * 
     * @return The Request Type name that this task will pass to the server
     */
    protected final String getDataRequestType() {
        return dataRequestParameters.getDataRequestType();
    }
    
    public final DataUpdateType getDataUpdateType() {
        return dataRequestParameters.getDataUpdateType();
    }
    
    protected final LoginCredentials getLoginCredentials() {
        return dataRequestParameters.getLoginCredentials();
    }
    
    /**
     * Adds data request properties to the HTTP Request.
     * <p>
     * NOTE: The base class implementation is blank, as most data requests
     *  will use the file-based approach, but some requests are trivial
     *  enough to instead tag a few custom HTTP parameters to the URL.
     * 
     * @param httpURLConnection The HTTP URL Connection for the Request
     */
    public final void addDataRequestProperties( final HttpURLConnection httpURLConnection ) {
        dataRequestParameters.addDataRequestProperties( httpURLConnection );
    }
    
    /**
     * Returns a server status message related to the sending of data request
     * input parameters to the HTTP Request, or null if no message received,
     * such as when this method is not needed due to no input parameters.
     * <p>
     * NOTE: Not all data  requests send additional input parameters.
     * 
     * @param httpURLConnection The HTTP URL Connection for the Request
     * 
     * @return The server status message, or null if no input parameters sent
     */
    public final String sendDataRequestInputParameters( final HttpURLConnection httpURLConnection ) {
        return dataRequestParameters
                .sendDataRequestInputParameters( httpURLConnection );
   }
}
