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

import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jacoustics.FrequencySignalUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.NumberFormat;

public final class FrequencyRangeInformationPane extends VBox {

    // Declare strings for the static part of the settings formatting.
    public static final String  RELATIVE_BANDWIDTH_LABEL_LABEL   = "Relative Bandwidth";         //$NON-NLS-1$
    public static final String  CENTER_FREQUENCY_LABEL_LABEL     = "Center Frequency";           //$NON-NLS-1$
    public static final String  START_FREQUENCY_LABEL_LABEL      = "Start Frequency";            //$NON-NLS-1$
    public static final String  STOP_FREQUENCY_LABEL_LABEL       = "Stop Frequency";             //$NON-NLS-1$

    private static final String BANDWIDTH_UNITS                  = " octave";                    //$NON-NLS-1$

    // Declare default formatted data for each label.
    private static final String RELATIVE_BANDWIDTH_LABEL_DEFAULT = RELATIVE_BANDWIDTH_LABEL_LABEL
            + " Not Available";                                                                  //$NON-NLS-1$
    private static final String CENTER_FREQUENCY_LABEL_DEFAULT   = CENTER_FREQUENCY_LABEL_LABEL
            + " Not Available";                                                                  //$NON-NLS-1$
    private static final String START_FREQUENCY_LABEL_DEFAULT    = START_FREQUENCY_LABEL_LABEL
            + " Not Available";                                                                  //$NON-NLS-1$
    private static final String STOP_FREQUENCY_LABEL_DEFAULT     = STOP_FREQUENCY_LABEL_LABEL
            + " Not Available";                                                                  //$NON-NLS-1$

    public Label                _relativeBandwidthLabel;
    public Label                _centerFrequencyLabel;
    public Label                _startFrequencyLabel;
    public Label                _stopFrequencyLabel;

    // Cache the Client Properties for System Type, Locale etc.
    public ClientProperties     _clientProperties;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat      _numberFormat;

    public FrequencyRangeInformationPane( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        try {
            initPane();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane() {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( _clientProperties.locale );

        _relativeBandwidthLabel = GuiUtilities.getStatusLabel(
                RELATIVE_BANDWIDTH_LABEL_DEFAULT );
        _centerFrequencyLabel = GuiUtilities.getStatusLabel(
                CENTER_FREQUENCY_LABEL_DEFAULT );
        _startFrequencyLabel = GuiUtilities.getStatusLabel(
                START_FREQUENCY_LABEL_DEFAULT );
        _stopFrequencyLabel = GuiUtilities.getStatusLabel(
                STOP_FREQUENCY_LABEL_DEFAULT );

        getChildren().addAll( _relativeBandwidthLabel,
                              _centerFrequencyLabel,
                              _startFrequencyLabel,
                              _stopFrequencyLabel );
        setAlignment( Pos.CENTER_LEFT );

        setPadding( new Insets( 6.0d ) );
    }

    public void reset() {
        _relativeBandwidthLabel.setText( RELATIVE_BANDWIDTH_LABEL_DEFAULT );
        _centerFrequencyLabel.setText( CENTER_FREQUENCY_LABEL_DEFAULT );
        _startFrequencyLabel.setText( START_FREQUENCY_LABEL_DEFAULT );
        _stopFrequencyLabel.setText( STOP_FREQUENCY_LABEL_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _relativeBandwidthLabel.setTextFill( foregroundColor );
        _centerFrequencyLabel.setTextFill( foregroundColor );
        _startFrequencyLabel.setTextFill( foregroundColor );
        _stopFrequencyLabel.setTextFill( foregroundColor );
    }

    // Update the cached Frequency Range.
    // NOTE: This method is generally called from an acoustic response context.
    @SuppressWarnings("nls")
    public void setFrequencyRange( final double startFrequency,
                                   final double stopFrequency,
                                   final String relativeBandwidth,
                                   final double centerFrequency ) {
        // Store the start, stop and center frequencies with a maximum three
        // digits of precision (to cover some tightly spaced low frequencies).
        _numberFormat.setMinimumFractionDigits( 0 );
        _numberFormat.setMaximumFractionDigits( 3 );
        final String sStartFrequency = FrequencySignalUtilities
                .getFormattedFrequency( startFrequency, _numberFormat );
        final String sStopFrequency = FrequencySignalUtilities
                .getFormattedFrequency( stopFrequency, _numberFormat );
        final String sCenterFrequency = FrequencySignalUtilities
                .getFormattedFrequency( centerFrequency, _numberFormat );

        final String relativeBandwidthLabel = RELATIVE_BANDWIDTH_LABEL_LABEL + " = "
                + relativeBandwidth + BANDWIDTH_UNITS;
        final String centerFrequencyLabel = CENTER_FREQUENCY_LABEL_LABEL + " = " + sCenterFrequency;
        final String startFrequencyLabel = START_FREQUENCY_LABEL_LABEL + " = " + sStartFrequency;
        final String stopFrequencyLabel = STOP_FREQUENCY_LABEL_LABEL + " = " + sStopFrequency;

        // Update the associated labels in the information pane.
        _relativeBandwidthLabel.setText( relativeBandwidthLabel );
        _centerFrequencyLabel.setText( centerFrequencyLabel );
        _startFrequencyLabel.setText( startFrequencyLabel );
        _stopFrequencyLabel.setText( stopFrequencyLabel );
    }

    public String[] getFrequencyRangeInformation() {
        // Collect the information fields to render to a single-column table.
        final String[] information = new String[ 4 ];
        int i = 0;
        information[ i++ ] = _relativeBandwidthLabel.getText();
        information[ i++ ] = _centerFrequencyLabel.getText();
        information[ i++ ] = _startFrequencyLabel.getText();
        information[ i++ ] = _stopFrequencyLabel.getText();
        return information;
    }
}
