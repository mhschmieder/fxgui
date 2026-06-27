/*
 * MIT License
 *
 * Copyright (c) 2024, 2026 Mark Schmieder. All rights reserved.
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
package com.mhschmieder.fxgui.dialog;

import com.mhschmieder.fxcontrols.control.TextEditor;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jcommons.util.GlobalUtilities;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.lang.System.Logger;
import java.util.Collections;

public class NameChangeDialog extends XDialog {

    private static final Logger LOGGER = System.getLogger(
            NameChangeDialog.class.getName() );

    public StringProperty name;

    /**
     * Default constructor. This is the preferred constructor for this class.
     * <p>
     * Creates a new {@code XDialog} instance.
     *
     * @param title
     * 		The title bar text to use for the Dialog
     * @param headerText
     * 		The Header Text to use as a simplified description
     * @param objectType The object type of the object whose name will change
     * @param originalName The original name of the object to be renamed
     * @since 1.0
     */
    public NameChangeDialog( final String title,
                             final String headerText,
                             final String objectType,
                             final String originalName ) {
        super( title, headerText );

        try{
            initDialog( objectType, originalName );
        }catch( final Exception e ){
            LOGGER.log( Level.ERROR, e.getMessage(), e);
        }
    }

    private void initDialog( final String objectType,
                             final String originalName ) {
        name = new SimpleStringProperty( originalName );
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().add( ButtonType.OK );
        final TextEditor textEditor = new TextEditor(
                true, GlobalUtilities.makeClientProperties(
                Collections.emptyMap() ) );
        final HBox nameBox = GuiUtilities.getLabeledTextFieldPane(
                objectType + " " + "Name", textEditor );
        final VBox content = new VBox(nameBox);
        content.setPadding( new Insets(
                10.0d, 10.0d, 10.0d, 10.0d ) );
        content.setSpacing( 10.0d );
        dialogPane.setContent( content );
        textEditor.textProperty().bindBidirectional( name );
    }
}
