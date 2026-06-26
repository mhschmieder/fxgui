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
 * This file is part of the FxAcoustics Library
 *
 * You should have received a copy of the MIT License along with the
 * FxAcoustics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxacoustics
 */
package com.mhschmieder.fxgui.layout;

import com.mhschmieder.fxacousticscontrols.control.AcousticsControlFactory;
import com.mhschmieder.fxacousticscontrols.control.AcousticsLabeledControlFactory;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SplRangePane extends VBox {

    public static final boolean AUTO_RANGE_DEFAULT   = true;

    // Declare JavaFX controls for SPL Range related actions.
    public CheckBox             _autoRangeSplCheckBox;
    public Label                _splRangeLabel;
    public Spinner< Integer >   _splRangeSpinner;

    // Declare a cache of the current SPL Range in dB.
    protected double _splRangeDb = AcousticsControlFactory.SPL_RANGE_DB_DEFAULT;

    public SplRangePane( final ClientProperties clientProperties, final boolean useExtendedRange ) {
        // Always call the superclass constructor first!
        super();

        initPane( clientProperties, useExtendedRange );
    }

    public final void initPane( final ClientProperties clientProperties,
                                final boolean useExtendedRange ) {
        _autoRangeSplCheckBox = AcousticsLabeledControlFactory
                .getAutoRangeSplCheckBox( clientProperties );
        _splRangeLabel = AcousticsLabeledControlFactory.getSplRangeLabel( clientProperties );
        _splRangeSpinner = AcousticsControlFactory
                .getSplRangeSpinnerInstance( clientProperties, false, useExtendedRange );

        // Disable the SPL Range spinner until Auto-Range SPL is turned off.
        _autoRangeSplCheckBox.setDisable( false );
        _splRangeSpinner.setDisable( true );

        // Create a horizontal box to host the SPL Range controls.
        final HBox hbox = new HBox();
        hbox.getChildren().addAll( _splRangeLabel, _splRangeSpinner );
        hbox.setAlignment( Pos.CENTER_LEFT );
        hbox.setPadding( new Insets( 12d ) );
        hbox.setSpacing( 12d );

        getChildren().addAll( _autoRangeSplCheckBox, hbox );

        setAlignment( Pos.CENTER );
        setSpacing( 16d );
        setPadding( new Insets( 16d ) );

        // Load the event handler for the Auto-Range SPL Check Box.
        _autoRangeSplCheckBox.selectedProperty()
                .addListener( ( observable, oldValue, newValue ) -> {
                    // Update the Auto-Range SPL value.
                    processAutoRangeSplChangedNotification( newValue );
                } );
    }

    public final int getSplRangeDb() {
        if ( _autoRangeSplCheckBox.isSelected() ) {
            return AcousticsControlFactory.SPL_RANGE_DB_DEFAULT;
        }

        final Integer splRangeDb = _splRangeSpinner.getValue();
        return splRangeDb.intValue();
    }

    public final boolean isAutoRangeSpl() {
        return _autoRangeSplCheckBox.isSelected();
    }

    // Selectively enable or disable the manual SPL Range spinner, and
    // conditionally reset the cached SPL Range.
    protected final void processAutoRangeSplChangedNotification( final boolean autoRangeSpl ) {
        // If we are now in Auto-Range Mode, disable the manual SPL Range
        // spinner.
        setSplRangeEnabled( !autoRangeSpl );

        // If we are now in Auto-Range Mode, reset to the default SPL Range.
        if ( autoRangeSpl ) {
            setSplRangeDb( AcousticsControlFactory.SPL_RANGE_DB_DEFAULT );
        }
    }

    protected final void setAutoRangeSpl( final boolean autoRangeSpl ) {
        _autoRangeSplCheckBox.setSelected( autoRangeSpl );
    }

    protected final void setSplRangeDb( final int splRangeDb ) {
        _splRangeSpinner.getValueFactory().setValue( splRangeDb );
    }

    protected final void setSplRangeEnabled( final boolean splRangeEnabled ) {
        _splRangeSpinner.setDisable( !splRangeEnabled );
    }

    public final void updateSplRange( final boolean autoRangeSpl, final int splRangeDb ) {
        // Set to the cached Auto-Range Mode.
        setAutoRangeSpl( autoRangeSpl );

        // If we are no longer in Auto-Range Mode, enable the manual SPL Range
        // spinner.
        setSplRangeEnabled( !autoRangeSpl );

        // If we are no longer in Auto-Range Mode, set to the cached manual SPL
        // Range.
        if ( !autoRangeSpl ) {
            setSplRangeDb( splRangeDb );
        }
    }

}
