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
package com.mhschmieder.fxgui.layout;

import com.mhschmieder.fxcontrols.control.ControlFactory;
import com.mhschmieder.fxcontrols.control.LabeledControlFactory;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DitheringPane extends VBox {

    public static final boolean USE_DITHERING_DEFAULT    = true;

    // Declare JavaFX controls for Dithering related actions.
    public CheckBox             _useDitheringCheckBox;
    public Label                _ditheringAmountLabel;
    public Spinner< Double >    _ditheringAmountSpinner;

    // Declare a cache of the current Dithering Amount in percentiles.
    protected double _splRangeDb = ControlFactory.DITHERING_AMOUNT_DEFAULT;

    public DitheringPane( final ClientProperties clientProperties,
                          final boolean initialDisableDithering ) {
        // Always call the superclass constructor first!
        super();

        initPane( clientProperties, initialDisableDithering );
    }

    public final void initPane( final ClientProperties clientProperties,
                                final boolean initialDisableDithering ) {
        // Make the JavaFX Nodes and add them to the Scene.
        _useDitheringCheckBox = LabeledControlFactory
                .getUseDitheringCheckBox( clientProperties );

        _ditheringAmountLabel = LabeledControlFactory.getDitheringLabel(
                clientProperties );

        _ditheringAmountSpinner = ControlFactory
                .getDitheringAmountSpinnerInstance(
                        clientProperties, false );

        // Disable the Dithering Amount spinner until Use Dithering is turned
        // on.
        _useDitheringCheckBox.setDisable( false );
        _ditheringAmountSpinner.setDisable( false );

        // Create a horizontal box to host the Dithering controls.
        final HBox hbox = new HBox();
        hbox.getChildren().addAll( _ditheringAmountLabel, _ditheringAmountSpinner );
        hbox.setAlignment( Pos.CENTER_LEFT );
        hbox.setPadding( new Insets( 12d ) );
        hbox.setSpacing( 12d );

        getChildren().addAll( _useDitheringCheckBox, hbox );

        setAlignment( Pos.CENTER );
        setSpacing( 16d );
        setPadding( new Insets( 16d ) );

        if ( initialDisableDithering ) {
            _useDitheringCheckBox.setSelected( false );
        }
        else {
            _useDitheringCheckBox.setSelected( USE_DITHERING_DEFAULT );
        }

        // Load the event handler for the Use Dithering Check Box.
        _useDitheringCheckBox.selectedProperty()
                .addListener( ( observable, oldValue, newValue ) -> {
                    // Update the Dithering Amount value.
                    processUseDitheringChangedNotification( newValue );
                } );
    }

    public final double getDitheringAmount() {
        final Double ditheringAmount = _ditheringAmountSpinner.getValue();
        return ditheringAmount.doubleValue();
    }

    public final boolean isUseDithering() {
        return _useDitheringCheckBox.isSelected();
    }

    // Selectively enable or disable the manual Dithering Amount spinner, and
    // conditionally reset the cached Dithering Amount.
    protected final void processUseDitheringChangedNotification( final boolean useDithering ) {
        // If we are now in Use Dithering Mode, enable the manual Dithering
        // Amount spinner.
        setDitheringAmountEnabled( useDithering );

        // If we are not in Use Dithering Mode, reset to the default Dithering
        // Amount.
        if ( !useDithering ) {
            setDitheringAmount( ControlFactory.DITHERING_AMOUNT_DEFAULT );
        }
    }

    protected final void setDitheringAmount( final double ditheringAmount ) {
        _ditheringAmountSpinner.getValueFactory().setValue( ditheringAmount );
    }

    protected final void setDitheringAmountEnabled( final boolean ditheringAmountEnabled ) {
        _ditheringAmountSpinner.setDisable( !ditheringAmountEnabled );
    }

    protected final void setUseDithering( final boolean useDithering ) {
        _useDitheringCheckBox.setSelected( useDithering );
    }

    // NOTE: This is the method to use when updating from Preferences.
    public final void updateDithering( final boolean useDithering, final double ditheringAmount ) {
        // TODO: Review whether we need runLater() here or not.
        Platform.runLater( () -> {
            // Set to the cached Use Dithering Mode.
            setUseDithering( useDithering );

            // If we are now in Use Dithering Mode, enable the manual Dithering
            // Amount spinner.
            // NOTE: We do this manually because the JavaFX callback may not
            // have been registered at the time this method is invoked from
            // the Preferences loader.
            setDitheringAmountEnabled( useDithering );

            // If we are no longer in Use Dithering Mode, set to the cached
            // manual Dithering Amount.
            if ( useDithering ) {
                setDitheringAmount( ditheringAmount );
            }
        } );
    }
}
