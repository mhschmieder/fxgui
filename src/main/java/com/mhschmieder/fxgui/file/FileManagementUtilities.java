/*
 * MIT License
 *
 * Copyright (c) 2022, 2026 Mark Schmieder. All rights reserved.
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
 * This file is part of the jgui Library
 *
 * You should have received a copy of the MIT License along with the jgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/jgui
 */
package com.mhschmieder.fxgui.file;

import com.mhschmieder.fxgui.dialog.DialogUtilities;
import com.mhschmieder.fxgui.stage.XStage;
import com.mhschmieder.jcommons.io.FileDiagnostics;
import com.mhschmieder.jcommons.io.FileUtilities;
import com.mhschmieder.jcommons.lang.StringUtilities;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities related to file functionality, such as making files/directories.
 * <p>
 * NOTE: These utility methods must be run on the JavaFX Application Thread.
 */
public final class FileManagementUtilities {

    private static final Logger LOGGER = System.getLogger(
            FileManagementUtilities.class.getName() );

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private FileManagementUtilities() {
    }

    /**
     * Returns the {@link File} handle for the file requested.
     * <p>
     * This serves as a "safety" wrapper to cover most usage contexts, to avoid
     * copy/paste code and inconsistent or incomplete logic when dealing with
     * various issues that can come up when trying to make a file that may or
     * may not already exist.
     *
     * @param filePathname The pathname of the file to create
     * @param replaceIfExists {@code true} if the file should be overwritten if
     *                               it already exists; {@code false} otherwise
     * @param headlessMode {@code true} if no confirmation dialogs are wanted
     * @return The {@link File} handle for the file requested
     */
    public static File makeFile( final String filePathname,
                                 final boolean replaceIfExists,
                                 final boolean headlessMode ) {
        final File file = new File( filePathname );
        if ( !FileDiagnostics.checkFileExists(
                filePathname, "", true ) ) {
            try {
                final Path filePath = Paths.get( filePathname );
                Files.createFile( filePath );
            }
            catch ( final Exception e ) {
                LOGGER.log( Level.ERROR, e.getMessage(), e );

                if ( !headlessMode ) {
                    final String message = "Could Not Create File: "
                            + StringUtilities.quote( filePathname );
                    final String masthead = "File Not Created";
                    final String title = "File Error";

                    DialogUtilities.showWarningAlert( message, masthead, title );
                }

                return null;
            }
        }
        else {
            if ( replaceIfExists ) {
                if ( !headlessMode && !DialogUtilities
                        .checkOverwriteExistingFile( file ) ) {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        return file;
    }

    /**
     * Returns the {@link File} handle for the directory requested.
     * <p>
     * This serves as a "safety" wrapper to cover most usage contexts, to avoid
     * copy/paste code and inconsistent or incomplete logic when dealing with
     * various issues that can come up when trying to make a directory that may
     * or may not already exist and that may or may not already have contents.
     *
     * @param directoryPathname The pathname of the directory to create
     * @param replaceIfExists {@code true} if the directory and its contents
     *                               should be deleted if already exists vs.
     *                               retained and returned for additions;
     *                               {@code false} to leave the directory as-is
     * @param headlessMode {@code true} if no confirmation dialogs are wanted
     * @return The {@link File} handle for the directory requested
     */
    public static File makeDirectory( final String directoryPathname,
                                      final boolean replaceIfExists,
                                      final boolean headlessMode ) {
        return makeDirectory(
                new File( directoryPathname ),
                replaceIfExists,
                headlessMode );
    }

    /**
     * Returns the {@link File} handle for the directory requested.
     * <p>
     * This serves as a "safety" wrapper to cover most usage contexts, to avoid
     * copy/paste code and inconsistent or incomplete logic when dealing with
     * various issues that can come up when trying to make a directory that may
     * or may not already exist and that may or may not already have contents.
     *
     * @param directoryFile The directory to create
     * @param replaceIfExists {@code true} if the directory and its contents
     *                               should be deleted if already exists vs.
     *                               retained and returned for additions;
     *                               {@code false} to leave the directory as-is
     * @param headlessMode {@code true} if no confirmation dialogs are wanted
     * @return The {@link File} handle for the directory requested
     */
    public static File makeDirectory( final File directoryFile,
                                      final boolean replaceIfExists,
                                      final boolean headlessMode ) {
        try {
            return makeDirectory(
                    Path.of( directoryFile.getPath() ),
                    directoryFile,
                    replaceIfExists,
                    headlessMode );
        }
        catch ( final Exception e ) {
            LOGGER.log( Level.ERROR, e.getMessage(), e );
            return null;
        }
    }

    /**
     * Returns the {@link File} handle for the directory requested.
     * <p>
     * This serves as a "safety" wrapper to cover most usage contexts, to avoid
     * copy/paste code and inconsistent or incomplete logic when dealing with
     * various issues that can come up when trying to make a directory that may
     * or may not already exist and that may or may not already have contents.
     *
     * @param directoryPath The {@link Path} encapsulation of the directory
     * @param replaceIfExists {@code true} if the directory and its contents
     *                               should be deleted if already exists vs.
     *                               retained and returned for additions;
     *                               {@code false} to leave the directory as-is
     * @param headlessMode {@code true} if no confirmation dialogs are wanted
     * @return The {@link File} handle for the directory requested
     */
    public static File makeDirectory( final Path directoryPath,
                                      final boolean replaceIfExists,
                                      final boolean headlessMode ) {
        try {
            return makeDirectory(
                    directoryPath,
                    directoryPath.toFile(),
                    replaceIfExists,
                    headlessMode );
        }
        catch ( final Exception e ) {
            LOGGER.log( Level.ERROR, e.getMessage(), e );
            return null;
        }
    }

    /**
     * Returns the {@link File} handle for the directory requested.
     * <p>
     * This serves as a "safety" wrapper to cover most usage contexts, to avoid
     * copy/paste code and inconsistent or incomplete logic when dealing with
     * various issues that can come up when trying to make a directory that may
     * or may not already exist and that may or may not already have contents.
     *
     * @param directoryPath The {@link Path} encapsulation of the directory
     * @param directoryFile The directory to create
     * @param replaceIfExists {@code true} if the directory and its contents
     *                               should be deleted if already exists vs.
     *                               retained and returned for additions;
     *                               {@code false} to leave the directory as-is
     * @param headlessMode {@code true} if no confirmation dialogs are wanted
     * @return The {@link File} handle for the directory requested
     */
    public static File makeDirectory( final Path directoryPath,
                                      final File directoryFile,
                                      final boolean replaceIfExists,
                                      final boolean headlessMode ) {
        if ( !FileDiagnostics.checkFileExists(
                directoryFile.getPath(), "", true ) ) {
            try {
                FileUtils.forceMkdir( directoryFile );
            }
            catch ( final Exception e ) {
                LOGGER.log( Level.ERROR, e.getMessage(), e );

                if ( !headlessMode ) {
                    final String message = "Could Not Create Folder: "
                            + StringUtilities.quote( directoryFile.getPath() );
                    final String masthead = "Folder Not Created";
                    final String title = "File Error";

                    DialogUtilities.showWarningAlert( message, masthead, title );
                }

                return null;
            }
        }
        else {
            if ( !FileDiagnostics.isDirectoryEmpty( directoryFile )
                    && replaceIfExists ) {
                if ( !headlessMode ) {
                    if ( !DialogUtilities.checkRemoveDirectoryContents(
                            directoryFile.getPath() ) ) {
                        return null;
                    }
                }

                // Delete the current directory contents but preserve structure.
                if ( !FileUtilities.deleteDirectoryContents( directoryFile ) ) {
                    if ( !headlessMode ) {
                        final String message = "Could Not Delete Folder: "
                                + StringUtilities.quote(
                                directoryFile.getPath() );
                        final String masthead = "Folder Not Deleted";
                        final String title = "File Error";

                        DialogUtilities.showWarningAlert(
                                message, masthead, title );
                    }
                }
            }
        }

        return directoryFile;
    }

    public static void toggleStageVisible( final XStage stage ) {
        // NOTE: If the stage visibility is set to false while iconified, then
        //  the stage enters a bad state that cannot be shown when visibility is
        //  iet to true. Setting iconified to false prior to visibility to false
        //  prevents this issue from occurring.
        if ( stage.isShowing() ) {
            stage.setIconified( false );
        }
        stage.setVisible( !stage.isShowing(), true );
    }
}
