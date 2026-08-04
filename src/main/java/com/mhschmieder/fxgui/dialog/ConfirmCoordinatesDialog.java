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
 * You should have received a copy of the MIT License along with the
 * FxfxguiPhysics
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgui
 */
package com.mhschmieder.fxgui.dialog;

import com.mhschmieder.fxgui.layout.CartesianPositionPane;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.measure.DistanceUnit;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class ConfirmCoordinatesDialog extends XDialog {

    public CartesianPositionPane _positionPane;
    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;
    protected Point2D _coordinatesCandidate;

    public ConfirmCoordinatesDialog( final String title,
                                     final String masthead,
                                     final ClientProperties pClientProperties,
                                     final Point2D coordinatesCandidate ) {
        // Always call the superclass constructor first!
        super( title, masthead ); // , pClientProperties.systemType );

        clientProperties = pClientProperties;
        _coordinatesCandidate = coordinatesCandidate;

        try {
            initDialog();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initDialog() {
        _positionPane = new CartesianPositionPane( clientProperties );

        final Node positionBorderNode = GuiUtilities.getTitledBorderWrappedNode(
                _positionPane,
                "Reference Point Position" ); //$NON-NLS-1$

        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter( positionBorderNode );

        borderPane.setPadding( new Insets( 16d ) );

        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContent( borderPane );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        // Filter for the ENTER key so we can use it to trigger the OK Button.
        // TODO: Find a better way to filter for this so that we wait for focus
        // lost on the editing controls, but this is probably not possible until
        // the next revision of the Dialog API, at which point we probably have
        // direct control of ENTER anyway by assigning the Default Button.
        borderPane.addEventFilter( KeyEvent.KEY_RELEASED, keyEvent -> {
            final KeyCombination keyCombo
                    = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Emulate the OK Button being pressed.
                setResult( ButtonType.OK );

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );
    }

    public final Point2D getCoordinatesCandidate() {
        return _coordinatesCandidate;
    }

    public final void setDistanceUnit( final DistanceUnit distanceUnit ) {
        _positionPane.updateDistanceUnit( distanceUnit );
    }

    @Override
    public void updateView() {
        _positionPane.setCartesianPosition2D( _coordinatesCandidate );
    }

    @Override
    public void updateModel() {
        _coordinatesCandidate = _positionPane.getCartesianPosition2D();
    }
}// class ConfirmCoordinatesDialog
