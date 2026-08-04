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
package com.mhschmieder.fxgui.dialog;

import com.mhschmieder.fxcontrols.util.MessageFactory;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.io.RasterGraphicsExportOptions;
import com.mhschmieder.fxgraphics.io.VectorGraphicsExportOptions;
import com.mhschmieder.jcommons.lang.CharConstants;
import com.mhschmieder.jcommons.lang.StringUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.measure.DistanceUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * {@code DialogUtilities} is a static utilities class for ensuring a reduction
 * in copy/paste code for similar functionality that should be maintained
 * consistently across all {@code Dialog} derived classes.
 *
 * @author Mark Schmieder
 * @version 1.0
 */
public final class DialogUtilities {

    /**
     * This is meant to be used as the layout pane background in windows that
     * don't support custom colors per user. It is designed to be a fairly close
     * match for Day Mode background, adjusted for ideal contrast.
     */
    public static final Color WINDOW_BACKGROUND_COLOR = Color.GAINSBORO;
    /**
     * This is meant to be used as the layout pane background in windows that
     * don't support custom colors per user. It is designed to be a fairly close
     * match for Day Mode foreground, adjusted for ideal contrast.
     */
    public static final Color WINDOW_FOREGROUND_COLOR = Color.BLACK;

    /**
     * The default constructor is disabled, as this is a static utilities
     * class.
     */
    private DialogUtilities() {
    }

    /**
     * The purpose of this method is to produce a consistent background style
     * for use on all dialog content panes, using a default background color and
     * no corner radii or insets (for a flush fill against the parent).
     *
     * @return A @Background object targeted to setBackground()
     */
    public static Background makeDialogBackground() {
        return makeDialogBackground( Insets.EMPTY );
    }

    /**
     * The purpose of this method is to produce a consistent background style
     * for use on all dialog content panes, using a default background color and
     * no corner radii and user-provided insets (to avoid corner gaps).
     *
     * @param insets The {@link Insets} to use for the background
     * @return A @Background object targeted to setBackground()
     */
    public static Background makeDialogBackground( final Insets insets ) {
        return RegionUtilities.makeRegionBackground( WINDOW_BACKGROUND_COLOR,
                                                     insets );
    }

    public static void showInformationAlert( final String message,
                                             final String masthead,
                                             final String title ) {
        final Alert alert = new Alert( AlertType.INFORMATION, message );
        showAlertDialog( alert, masthead, title );
    }

    /**
     * Blocks on the alert dialog.
     *
     * @param alert    the {@link Alert} to show and wait for.
     * @param masthead if not null, passed into
     *                 {@link Alert#setHeaderText(String)}
     * @param title    passed into {@link Alert#setTitle(String)}
     * @return The response from {@link Alert#showAndWait()}
     */
    private static Optional< ButtonType > showAlertDialog( final Alert alert,
                                                           final String masthead,
                                                           final String title ) {
        if ( masthead != null ) {
            alert.setHeaderText( masthead );
        }
        alert.setTitle( title );
        return alert.showAndWait();
    }

    public static Optional< ButtonType > showFileExitConfirmationAlert( final File file,
                                                                        final String productName ) {
        final String message = MessageFactory.getSaveFileChangesMessage( file );
        final String masthead = MessageFactory.getFileExitMasthead();
        final String title = MessageFactory.getFileExitTitle( productName );
        return showConfirmationAlert( message, masthead, title, true );
    }

    /**
     * Blocks while waiting for confirmation.
     *
     * @param message    The message for the user to confirm
     * @param masthead   The header text
     * @param title      The title of the confirmation dialog
     * @param showCancel Whether the confirmation dialog shows a cancel button
     * @return the result of {@link Alert#showAndWait()}
     */
    public static Optional< ButtonType > showConfirmationAlert( final String message,
                                                                final String masthead,
                                                                final String title,
                                                                final boolean showCancel ) {
        return showConfirmationAlert( message,
                                      masthead,
                                      title,
                                      showCancel,
                                      AlertType.CONFIRMATION );
    }

    /**
     * Blocks while waiting for confirmation.
     *
     * @param message    The message for the user to confirm
     * @param masthead   The header text
     * @param title      The title of the confirmation dialog
     * @param showCancel Whether the confirmation dialog shows a cancel button
     * @param alertType  The type of alert to indicate with the banner icon
     * @return the result of {@link Alert#showAndWait()}
     */
    public static Optional< ButtonType > showConfirmationAlert( final String message,
                                                                final String masthead,
                                                                final String title,
                                                                final boolean showCancel,
                                                                final AlertType alertType ) {
        // Most confirmation dialogs do not need a Cancel button.
        final List< ButtonType > buttonTypes = new ArrayList<>();
        buttonTypes.add( ButtonType.YES );
        buttonTypes.add( ButtonType.NO );
        if ( showCancel ) {
            buttonTypes.add( ButtonType.CANCEL );
        }
        final ButtonType[] buttons = buttonTypes.toArray( new ButtonType[ 0 ] );
        final Alert alert = new Alert( alertType, message, buttons );
        return showAlertDialog( alert, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message The message to display for File Open error alerts
     */
    public static void showFileOpenErrorAlert( final String message ) {
        final String masthead = MessageFactory.getFileNotOpenedMasthead();
        showFileOpenErrorAlert( message, masthead );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message  error message
     * @param masthead header text
     */
    public static void showFileOpenErrorAlert( final String message,
                                               final String masthead ) {
        final String title = MessageFactory.getFileOpenErrorTitle();
        showErrorAlert( message, masthead, title );
    }

    /**
     * Creates a new error {@link Alert} and shows it, blocking until it
     * returns.
     *
     * @param message  the error to display
     * @param masthead header text
     * @param title    the title of the alert
     */
    public static void showErrorAlert( final String message,
                                       final String masthead,
                                       final String title ) {
        final Alert alert = new Alert( AlertType.ERROR, message );
        showAlertDialog( alert, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message The message to display for File Read error alerts
     */
    public static void showFileReadErrorAlert( final String message ) {
        final String title = MessageFactory.getFileReadErrorTitle();
        showErrorAlert( message, null, title );
    }

    public static Optional< ButtonType > showFileSaveConfirmationAlert( final File file,
                                                                        final String title ) {
        final String message = MessageFactory.getSaveFileChangesMessage( file );
        final String masthead = MessageFactory.getSaveFileChangesMasthead();
        return showConfirmationAlert( message, masthead, title, true );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message The message to display for File Save error alerts
     */
    public static void showFileSaveErrorAlert( final String message ) {
        final String masthead = MessageFactory.getFileNotSavedMasthead();
        final String title = MessageFactory.getFileSaveErrorTitle();
        showErrorAlert( message, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message The message to display for File Save warning alerts
     */
    public static void showFileSaveWarningAlert( final String message ) {
        final String masthead = MessageFactory.getFilePartiallySavedMasthead();
        final String title = MessageFactory.getFileSaveErrorTitle();
        showWarningAlert( message, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message  The message to display in the alert
     * @param masthead header text
     * @param title    title of the alert
     */
    public static void showWarningAlert( final String message,
                                         final String masthead,
                                         final String title ) {
        final Alert alert = new Alert( AlertType.WARNING, message );
        showAlertDialog( alert, masthead, title );
    }

    public static boolean showRasterGraphicsExportOptions( final ClientProperties clientProperties,
                                                           final RasterGraphicsExportOptions rasterGraphicsExportOptions,
                                                           final boolean hasChart,
                                                           final boolean hasAuxiliary,
                                                           final String graphicsExportAllLabel,
                                                           final String graphicsExportChartLabel,
                                                           final String graphicsExportAuxiliaryLabel ) {
        // Clone the Raster Graphics Export Options to serve as the candidate.
        final RasterGraphicsExportOptions rasterGraphicsExportOptionsCandidate
                =
                new RasterGraphicsExportOptions( rasterGraphicsExportOptions );

        final String masthead
                = MessageFactory.getRasterGraphicsExportOptionsMasthead();
        final String title = MessageFactory.getFileSaveOptionsTitle();
        final RasterGraphicsExportOptionsDialog
                rasterGraphicsExportOptionsDialog
                = new RasterGraphicsExportOptionsDialog( title,
                                                         masthead,
                                                         clientProperties,
                                                         rasterGraphicsExportOptionsCandidate,
                                                         hasChart,
                                                         hasAuxiliary,
                                                         graphicsExportAllLabel,
                                                         graphicsExportChartLabel,
                                                         graphicsExportAuxiliaryLabel );
        final Optional< ButtonType > response
                = rasterGraphicsExportOptionsDialog.showModalDialog();

        // Cache the new Raster Graphics Export Options unless the user
        // canceled.
        final boolean rasterGraphicsExportOptionsCaptured = response.isPresent()
                                                            && response.get()
                                                                       .equals(
                                                                               rasterGraphicsExportOptionsDialog._exportButton );
        if ( rasterGraphicsExportOptionsCaptured ) {
            // Sync the data model to final edits before caching the result.
            rasterGraphicsExportOptionsDialog.updateModel();
            rasterGraphicsExportOptions.setRasterGraphicsExportOptions(
                    rasterGraphicsExportOptionsCandidate );
        }

        return rasterGraphicsExportOptionsCaptured;
    }

    public static boolean showVectorGraphicsExportOptions( final ClientProperties clientProperties,
                                                           final VectorGraphicsExportOptions vectorGraphicsExportOptions,
                                                           final boolean hasTitle,
                                                           final boolean hasChart,
                                                           final boolean hasAuxiliary,
                                                           final String graphicsExportAllLabel,
                                                           final String graphicsExportChartLabel,
                                                           final String graphicsExportAuxiliaryLabel ) {
        // Clone the Vector Graphics Export Options to serve as the candidate.
        final VectorGraphicsExportOptions vectorGraphicsExportOptionsCandidate
                =
                new VectorGraphicsExportOptions( vectorGraphicsExportOptions );

        final String masthead
                = MessageFactory.getVectorGraphicsExportOptionsMasthead();
        final String title = MessageFactory.getFileSaveOptionsTitle();
        final VectorGraphicsExportOptionsDialog
                vectorGraphicsExportOptionsDialog
                = new VectorGraphicsExportOptionsDialog( title,
                                                         masthead,
                                                         clientProperties,
                                                         vectorGraphicsExportOptionsCandidate,
                                                         hasTitle,
                                                         hasChart,
                                                         hasAuxiliary,
                                                         graphicsExportAllLabel,
                                                         graphicsExportChartLabel,
                                                         graphicsExportAuxiliaryLabel );
        final Optional< ButtonType > response
                = vectorGraphicsExportOptionsDialog.showModalDialog();

        // Cache the new Vector Graphics Export Options unless the user
        // canceled.
        final boolean vectorGraphicsExportOptionsCaptured = response.isPresent()
                                                            && response.get()
                                                                       .equals(
                                                                               vectorGraphicsExportOptionsDialog._exportButton );
        if ( vectorGraphicsExportOptionsCaptured ) {
            // Sync the data model to final edits before caching the result.
            vectorGraphicsExportOptionsDialog.updateModel();
            vectorGraphicsExportOptions.setVectorGraphicsExportOptions(
                    vectorGraphicsExportOptionsCandidate );
        }

        return vectorGraphicsExportOptionsCaptured;
    }

    public static void showIncompatibileClientAlert( final String productName,
                                                     final Hyperlink checkForUpdatesHyperlink ) {
        final String message = MessageFactory.getIncompatibleClientMessage(
                productName );
        final String masthead = MessageFactory.getIncompatibleClientMasthead(
                productName );
        final String title = MessageFactory.getClientServerProtocolErrorTitle();
        final String checkForUpdatesPreamble
                = MessageFactory.getCheckForUpdatesPreamble();
        final TextFlow textFlow = new TextFlow( new Text(
                checkForUpdatesPreamble ), checkForUpdatesHyperlink );
        showTextFlowAlert( message,
                           masthead,
                           title,
                           textFlow,
                           AlertType.ERROR );
    }

    /**
     * Blocks while waiting for confirmation, but provides a hyperlink for
     * follow-up action, hosted in a Text Flow control.
     *
     * @param message   The message for the user to confirm
     * @param masthead  The header text
     * @param title     The title of the confirmation dialog
     * @param textFlow  The Text Flow to use for user follow-up action
     * @param alertType The type of alert to indicate with the banner icon
     */
    public static void showTextFlowAlert( final String message,
                                          final String masthead,
                                          final String title,
                                          final TextFlow textFlow,
                                          final AlertType alertType ) {
        final TextFlowAlert alert = new TextFlowAlert( alertType,
                                                       message,
                                                       textFlow );
        if ( masthead != null ) {
            alert.setHeaderText( masthead );
        }
        alert.setTitle( title );
        alert.showAndWait();
    }

    public static void showInvalidUserAccountAlert( final String message,
                                                    final Hyperlink accountManagementHyperlink ) {
        final String masthead = MessageFactory.getInvalidUserAccountMasthead();
        final String title = MessageFactory.getUserAuthorizationErrorTitle();
        final String accountManagementPreamble
                = MessageFactory.getAccountManagementPreamble();
        final TextFlow textFlow = new TextFlow( new Text(
                accountManagementPreamble ), accountManagementHyperlink );
        showTextFlowAlert( message,
                           masthead,
                           title,
                           textFlow,
                           AlertType.ERROR );
    }

    /**
     * Returns {@code true} if specified file exists and the user chooses to
     * overwrite; returns {@code false} if specified file doesn't exist, or it
     * exists and the user chooses not to overwrite.
     *
     * @param filename The name of the file to be checked for overwrite
     * @return {@code true} if file exists and should be overwritten;
     *         {@code false} otherwise (for all other cases and user responses)
     */
    public static boolean checkOverwriteExistingFile( final String filename ) {
        final File possibleFile = new File( filename );
        return checkOverwriteExistingFile( possibleFile );
    }

    /**
     * Returns {@code true} if specified file exists and the user chooses to
     * overwrite; returns {@code false} if specified file doesn't exist, or it
     * exists and the user chooses not to overwrite.
     *
     * @param file The file to be checked for overwrite
     * @return {@code true} if file exists and should be overwritten;
     *         {@code false} otherwise (for all other cases and user responses)
     */
    public static boolean checkOverwriteExistingFile( final File file ) {
        // No need to specify the full file path for the confirmation dialog.
        final String filename = file.getName();
        final String message = "Overwrite Existing File: "
                               + StringUtilities.quote( filename ) + " ?";
        final String masthead = "File Will Be Overwritten";
        final String title = "File Already Exists";

        final Optional< ButtonType > response = showConfirmationAlert( message,
                                                                       masthead,
                                                                       title,
                                                                       false );
        return response.isPresent() && !response.get().equals( ButtonType.NO );
    }

    /**
     * Returns {@code true} if specified file exists and the user chooses to
     * overwrite; returns {@code false} if specified file doesn't exist, or it
     * exists and the user chooses not to overwrite.
     *
     * @param directoryPathname The path of the directory to be checked for
     *                          content removal
     * @return {@code true} if file exists and should be overwritten;
     *         {@code false} otherwise (for all other cases and user responses)
     */
    public static boolean checkRemoveDirectoryContents( final String directoryPathname ) {
        final String message = "Remove Files and Subfolders from Folder: "
                               + StringUtilities.quote( directoryPathname )
                               + " ?";
        final String masthead = "Folder Contains Files or Subfolders";
        final String title = "Folder Not Empty";

        final Optional< ButtonType > response = showConfirmationAlert( message,
                                                                       masthead,
                                                                       title,
                                                                       false );
        return response.isPresent() && !response.get().equals( ButtonType.NO );
    }

    // NOTE: No longer used, but keep it at hand for upcoming deployments.
    public static void showInstallationDirectoryError() {
        final String message =
                "The installation directory contains a space in its name."
                + CharConstants.LF
                + "Application components cannot run from directories with "
                + "spaces in" + CharConstants.LF
                + "their name. Please rename or move the installation "
                + "directory.";
        final String masthead = "Space in Directory Name";
        final String title = "Space in Directory Name Warning";

        showWarningAlert( message, masthead, title );
    }

    // NOTE: Point2D is immutable, so this method must return the confirmed
    //  coordinates vs. a status. This isn't a problem though, as the default
    //  choice is passed in, so a Cancel action simply returns the initial
    //  coordinates unchanged.
    public static Point2D showConfirmCoordinatesDialog( final String title,
                                                        final ClientProperties clientProperties,
                                                        final Point2D coordinatesCandidate,
                                                        final DistanceUnit distanceUnit ) {
        final String masthead = MessageFactory.getConfirmCoordinatesMasthead();
        final ConfirmCoordinatesDialog confirmCoordinatesDialog
                = new ConfirmCoordinatesDialog( title,
                                                masthead,
                                                clientProperties,
                                                coordinatesCandidate );
        confirmCoordinatesDialog.setDistanceUnit( distanceUnit );
        final Optional< ButtonType > response
                = confirmCoordinatesDialog.showModalDialog();

        // Get the Button Type that was pressed. Use object-oriented comparisons
        // as Lambda Expressions do not give us the flexibility of exiting this
        // method directly or of sharing common code after initial special-case
        // handling of the three user options ("Yes", "No", and "Cancel") and
        // their variants. If no button was pressed, default to Cancel.
        final ButtonType buttonType = response.orElse( ButtonType.CANCEL );

        // Handle the full enumeration of potential responses.
        Point2D coordinates = coordinatesCandidate;

        switch ( buttonType.getButtonData() ) {
            case HELP:
            case HELP_2:
            case BACK_PREVIOUS:
            case CANCEL_CLOSE:
                // These options equate to cancellation. If the user cancels,
                // return the initial coordinates.
                break;
            case NO:
                // This case is currently unsupported but would act like Cancel.
                break;
            case APPLY:
            case FINISH:
            case NEXT_FORWARD:
            case OK_DONE:
            case YES:
                // Sync the data model to the final edits before querying them.
                confirmCoordinatesDialog.updateModel();

                // Return the newly confirmed coordinates.
                coordinates
                        = confirmCoordinatesDialog.getCoordinatesCandidate();

                break;
            case BIG_GAP:
            case SMALL_GAP:
            case LEFT:
            case RIGHT:
            case OTHER:
                // It is unlikely that these cases will ever be called, but it
                // is safest to treat them like a Cancel.
                break;
            default:
                break;
        }

        return coordinates;
    }
}
