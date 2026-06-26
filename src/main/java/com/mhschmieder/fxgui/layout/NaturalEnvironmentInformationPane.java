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
 * This file is part of the FxPhysics Library
 *
 * You should have received a copy of the MIT License along with the
 * FxPhysics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxphysics
 */
package com.mhschmieder.fxgui.layout;

import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.fxphysicscontrols.model.NaturalEnvironmentProperties;
import com.mhschmieder.fxgui.swing.NaturalEnvironmentInformationComponent;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.measure.PressureUnit;
import com.mhschmieder.jphysics.measure.TemperatureUnit;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.NumberFormat;

public final class NaturalEnvironmentInformationPane extends VBox {

    public static String getAirAttenuationLabel( final NaturalEnvironmentProperties naturalEnvironmentProperties) {
        final String airAttenuationLabel = NaturalEnvironmentInformationComponent.AIR_ATTENUATION_LABEL_LABEL
                + ( naturalEnvironmentProperties.isAirAttenuationApplied() ? " On" : " Off" ); //$NON-NLS-1$ //$NON-NLS-2$
        return airAttenuationLabel;
    }

    @SuppressWarnings("nls")
    public static String getPressureLabel( final NaturalEnvironmentProperties naturalEnvironmentProperties,
                                           final PressureUnit pressureUnit,
                                           final NumberFormat numberFormat ) {
        numberFormat.setMinimumFractionDigits( 2 );
        numberFormat.setMaximumFractionDigits( 2 );
        final String pressureLabel = NaturalEnvironmentInformationComponent.PRESSURE_LABEL_LABEL + " = "
                + numberFormat.format( naturalEnvironmentProperties.getPressure( pressureUnit ) ) + " "
                + pressureUnit.label();
        return pressureLabel;
    }

    @SuppressWarnings("nls")
    public static String getRelativeHumidityLabel( final NaturalEnvironmentProperties naturalEnvironmentProperties,
                                                   final NumberFormat percentFormat ) {
        percentFormat.setMinimumFractionDigits( 1 );
        percentFormat.setMaximumFractionDigits( 1 );
        final String relativeHumidityLabel = NaturalEnvironmentInformationComponent.RELATIVE_HUMIDITY_LABEL_LABEL + " = "
                + percentFormat.format( naturalEnvironmentProperties.getHumidityRelative() * 0.01d );
        return relativeHumidityLabel;
    }

    @SuppressWarnings("nls")
    public static String getTemperatureLabel( final NaturalEnvironmentProperties naturalEnvironmentProperties,
                                              final TemperatureUnit temperatureUnit,
                                              final NumberFormat numberFormat ) {
        numberFormat.setMinimumFractionDigits( 1 );
        numberFormat.setMaximumFractionDigits( 1 );
        final String temperatureLabel = NaturalEnvironmentInformationComponent.TEMPERATURE_LABEL_LABEL + " = "
                + numberFormat.format( naturalEnvironmentProperties.getTemperature( temperatureUnit ) )
                + temperatureUnit.abbreviation();
        return temperatureLabel;
    }

    public Label               _airAttenuationLabel;
    public Label               _temperatureLabel;
    public Label               _pressureLabel;
    public Label               _relativeHumidityLabel;

    // Keep a cached copy of the Natural Environment reference, in case the
    // units are changed between predictions.
    private NaturalEnvironmentProperties _naturalEnvironmentProperties;

    // Keep track of what units we're using to display, for later conversion.
    private TemperatureUnit    _temperatureUnit;
    private PressureUnit       _pressureUnit;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat     _numberFormat;

    // Percent format cache used for locale-specific percent formatting.
    protected NumberFormat     _percentFormat;

    public NaturalEnvironmentInformationPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        _temperatureUnit = TemperatureUnit.defaultValue();
        _pressureUnit = PressureUnit.defaultValue();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties clientProperties ) {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        _percentFormat = NumberFormat.getPercentInstance( clientProperties.locale );

        _airAttenuationLabel = GuiUtilities.getStatusLabel( NaturalEnvironmentInformationComponent.AIR_ATTENUATION_LABEL_DEFAULT );
        _temperatureLabel = GuiUtilities.getStatusLabel( NaturalEnvironmentInformationComponent.TEMPERATURE_LABEL_DEFAULT );
        _pressureLabel = GuiUtilities.getStatusLabel( NaturalEnvironmentInformationComponent.PRESSURE_LABEL_DEFAULT );
        _relativeHumidityLabel = GuiUtilities.getStatusLabel( NaturalEnvironmentInformationComponent.RELATIVE_HUMIDITY_LABEL_DEFAULT );

        getChildren().addAll( _airAttenuationLabel,
                              _temperatureLabel,
                              _pressureLabel,
                              _relativeHumidityLabel );
        setAlignment( Pos.CENTER_LEFT );

        setPadding( new Insets( 6.0d ) );
    }

    public void reset() {
        _airAttenuationLabel.setText( NaturalEnvironmentInformationComponent.AIR_ATTENUATION_LABEL_DEFAULT );
        _temperatureLabel.setText( NaturalEnvironmentInformationComponent.TEMPERATURE_LABEL_DEFAULT );
        _pressureLabel.setText( NaturalEnvironmentInformationComponent.PRESSURE_LABEL_DEFAULT );
        _relativeHumidityLabel.setText( NaturalEnvironmentInformationComponent.RELATIVE_HUMIDITY_LABEL_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _airAttenuationLabel.setTextFill( foregroundColor );
        _temperatureLabel.setTextFill( foregroundColor );
        _pressureLabel.setTextFill( foregroundColor );
        _relativeHumidityLabel.setTextFill( foregroundColor );
    }

    // Set and propagate the Natural Environment reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setNaturalEnvironment( final NaturalEnvironmentProperties naturalEnvironmentProperties) {
        // Cache the current Natural Environment in case the Measurement
        // Units change before the next prediction is run.
        _naturalEnvironmentProperties = naturalEnvironmentProperties;

        // Load the invalidation listener for the "Natural Environment Changed"
        // binding.
        _naturalEnvironmentProperties.naturalEnvironmentChangedProperty().addListener(
            invalidationListener -> updateLabels() );
    }

    public void updateView() {
        updateLabels();
    }

    public void updateLabels() {
        final String airAttenuationLabel = getAirAttenuationLabel(_naturalEnvironmentProperties);
        _airAttenuationLabel.setText( airAttenuationLabel );

        final String temperatureLabel = getTemperatureLabel(_naturalEnvironmentProperties,
                                                             _temperatureUnit,
                                                             _numberFormat );
        _temperatureLabel.setText( temperatureLabel );

        final String pressureLabel = getPressureLabel(_naturalEnvironmentProperties,
                                                       _pressureUnit,
                                                       _numberFormat );
        _pressureLabel.setText( pressureLabel );

        final String relativeHumidityLabel = getRelativeHumidityLabel(_naturalEnvironmentProperties,
                                                                       _percentFormat );
        _relativeHumidityLabel.setText( relativeHumidityLabel );
    }

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        _pressureUnit = pressureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        _temperatureUnit = temperatureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }

    public String[] getNaturalEnvironmentInformation() {
        // Collect the information fields to render to a single-column table.
        final String[] information = new String[ 4 ];
        int i = 0;
        information[ i++ ] = _airAttenuationLabel.getText();
        information[ i++ ] = _temperatureLabel.getText();
        information[ i++ ] = _pressureLabel.getText();
        information[ i++ ] = _relativeHumidityLabel.getText();
        return information;
    }
}
