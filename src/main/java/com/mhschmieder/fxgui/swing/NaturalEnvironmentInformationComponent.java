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
package com.mhschmieder.fxgui.swing;

import com.mhschmieder.fxcontrols.model.NaturalEnvironmentProperties;
import com.mhschmieder.fxgui.layout.NaturalEnvironmentInformationPane;
import com.mhschmieder.jcommons.lang.StringConstants;
import com.mhschmieder.jcontrols.component.JxDataViewComponent;
import com.mhschmieder.jphysics.measure.PressureUnit;
import com.mhschmieder.jphysics.measure.TemperatureUnit;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.SwingConstants;

/**
 * {@code NaturalEnvironmentInformationTable} is a specialization of
 * {@link JxDataViewComponent} that presents the current values of a
 * {@link NaturalEnvironmentProperties} instance.
 * <p>
 * As this component hosts a read-only parameter set, it needs one number
 * formatter for numbers and another for percents, but no number parsers.
 * <p>
 * Furthermore, this component has a specific style applied, for blending in
 * with the background (as opposed to usual black text on white background) and
 * skipping the horizontal and vertical grid lines.
 *
 * @author Mark Schmieder
 * @version 1.0
 */
public final class NaturalEnvironmentInformationComponent
        extends JxDataViewComponent {
    // Declare strings for the static part of the settings formatting.
    public static final String AIR_ATTENUATION_LABEL_LABEL = "Air Attenuation";
    //$NON-NLS-1$
    // Declare default formatted data for each label.
    public static final String AIR_ATTENUATION_LABEL_DEFAULT =
            AIR_ATTENUATION_LABEL_LABEL + " Off";
    //$NON-NLS-1$
    public static final String TEMPERATURE_LABEL_LABEL = "Temperature";
    //$NON-NLS-1$
    @SuppressWarnings( "nls" )
    public static final String TEMPERATURE_LABEL_DEFAULT =
            TEMPERATURE_LABEL_LABEL + " = 20" + StringConstants.DEGREES_CELSIUS;
    public static final String PRESSURE_LABEL_LABEL = "Pressure";
    //$NON-NLS-1$
    @SuppressWarnings( "nls" )
    public static final String PRESSURE_LABEL_DEFAULT = PRESSURE_LABEL_LABEL
                                                        + " = 101325 "
                                                        + PressureUnit.PASCALS.label();
    public static final String RELATIVE_HUMIDITY_LABEL_LABEL
            = "Relative Humidity";                  //$NON-NLS-1$
    @SuppressWarnings( "nls" )
    public static final String RELATIVE_HUMIDITY_LABEL_DEFAULT =
            RELATIVE_HUMIDITY_LABEL_LABEL + " = 50%";
    /**
     *
     */
    private static final long serialVersionUID = -5504900411550793534L;

    // Declare the array of column names to be displayed in the table header.
    // NOTE: These are all empty strings, but are necessary to imply table
    //  width.
    private final Object[] _columnNames = { "" };
    //$NON-NLS-1$

    // Declare an array of row data to be displayed in the rows of the table.
    // NOTE: The second row is dynamically adjusted; the first row is static.
    // NOTE: The second row cells are not converted to the proper type for
    //  display, but instead are always represented as pre-formatted strings.
    private final Object[][] _rowData = {
            {
                    AIR_ATTENUATION_LABEL_DEFAULT
            }, {
                    TEMPERATURE_LABEL_DEFAULT
            }, {
                    PRESSURE_LABEL_DEFAULT
            }, {
                    RELATIVE_HUMIDITY_LABEL_DEFAULT
            }
    };
    /**
     * Number format cache used for locale-specific number formatting.
     */
    protected NumberFormat numberFormat;
    /**
     * Number format cache used for locale-specific percent formatting.
     */
    protected NumberFormat percentFormat;
    // Keep a cached copy of the natural environment object, in case the units
    // are changed between predictions.
    private NaturalEnvironmentProperties _naturalEnvironmentProperties;
    // Keep track of what units we're using to display, for later conversion.
    private TemperatureUnit _temperatureUnit;
    private PressureUnit _pressureUnit;

    public NaturalEnvironmentInformationComponent() {
        // Always call the superclass constructor first!
        super();

        _naturalEnvironmentProperties = new NaturalEnvironmentProperties();

        _temperatureUnit = TemperatureUnit.defaultValue();
        _pressureUnit = PressureUnit.defaultValue();

        try {
            initComponent();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initComponent() {
        // First make sure the table components exist and are initialized.
        super.initTable( _rowData, _columnNames, SwingConstants.LEFT, false );

        // Initialize the Number Formatters, using the host's default Locale.
        initNumberFormatters( Locale.getDefault() );

        // This is generally the narrowest table, but attempt to force a size
        // that accommodates its largest values.
        setPreferredSize( new Dimension( 120, 100 ) );

        // Ensure that there are valid initial defaults.
        reset();
    }

    /**
     * Initializes and caches the number formatters, as the system-level query
     * for the host's default locale is expensive and can cause a freeze-up.
     * <p>
     * The method is declared final, as any derived classes should avoid
     * unwanted side effects and simply write their own number formatter
     * initialization method that resets precision to what is preferred in the
     * context of that tighter scope. These defaults are fairly common ones.
     *
     * @param locale The locale to use for number formatting purposes
     */
    private void initNumberFormatters( final Locale locale ) {
        // Cache the number formats so that we don't have to get information
        // about locale from the OS each time we format a number.
        numberFormat = NumberFormat.getNumberInstance( locale );
        percentFormat = NumberFormat.getPercentInstance( locale );
    }

    protected void reset() {
        table.setValueAt( AIR_ATTENUATION_LABEL_DEFAULT, 0, 0 );
        table.setValueAt( TEMPERATURE_LABEL_DEFAULT, 1, 0 );
        table.setValueAt( PRESSURE_LABEL_DEFAULT, 2, 0 );
        table.setValueAt( RELATIVE_HUMIDITY_LABEL_DEFAULT, 3, 0 );

        // Force a repaint event, to display/update the new table values.
        repaint();
    }

    public void setNaturalEnvironment( final NaturalEnvironmentProperties naturalEnvironmentProperties ) {
        // Cache the current Natural Environment in case the Measurement
        // Units change before the next prediction is run.
        _naturalEnvironmentProperties = naturalEnvironmentProperties;
    }

    public void updateView() {
        updateLabels();
    }

    public void updateLabels() {
        final String airAttenuationLabel
                = NaturalEnvironmentInformationPane.getAirAttenuationLabel(
                _naturalEnvironmentProperties );
        table.setValueAt( airAttenuationLabel, 0, 0 );

        final String temperatureLabel
                = NaturalEnvironmentInformationPane.getTemperatureLabel(
                _naturalEnvironmentProperties,
                _temperatureUnit,
                numberFormat );
        table.setValueAt( temperatureLabel, 1, 0 );

        final String pressureLabel
                = NaturalEnvironmentInformationPane.getPressureLabel(
                _naturalEnvironmentProperties,
                _pressureUnit,
                numberFormat );
        table.setValueAt( pressureLabel, 2, 0 );

        final String relativeHumidityLabel
                = NaturalEnvironmentInformationPane.getRelativeHumidityLabel(
                _naturalEnvironmentProperties,
                percentFormat );
        table.setValueAt( relativeHumidityLabel, 3, 0 );

        // Force a repaint event, to display/update the new table values.
        repaint();
    }

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Cache the new Pressure Unit.
        _pressureUnit = pressureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Cache the new Temperature Unit.
        _temperatureUnit = temperatureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }
}