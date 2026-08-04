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

import com.mhschmieder.fxcontrols.model.NaturalEnvironmentProperties;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jgraphics.input.ScrollingSensitivity;
import com.mhschmieder.jphysics.PhysicsConstants;
import com.mhschmieder.jphysics.measure.Altitude;
import com.mhschmieder.jphysics.measure.DistanceUnit;
import com.mhschmieder.jphysics.measure.PressureUnit;
import com.mhschmieder.jphysics.measure.TemperatureUnit;
import org.apache.commons.math3.util.FastMath;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public final class NaturalEnvironmentPane extends HBox {

    public TemperaturePane _temperaturePane;
    public HumidityPane _humidityPane;
    // Cache a reference to the global Natural Environment.
    public NaturalEnvironmentProperties naturalEnvironmentProperties;
    protected PressurePane _pressurePane;
    protected AltitudePane _altitudePane;

    public NaturalEnvironmentPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties clientProperties ) {
        _temperaturePane = new TemperaturePane( clientProperties );
        _humidityPane = new HumidityPane( clientProperties );
        _pressurePane = new PressurePane( clientProperties );
        _altitudePane = new AltitudePane( clientProperties );

        getChildren().addAll( _temperaturePane,
                              _humidityPane,
                              _pressurePane,
                              _altitudePane );

        setSpacing( 12d );
        setPadding( new Insets( 10.0d ) );

        // Make sure the Altitude Pane always gets horizontal grow priority, so
        // it can resize to fit larger measurement units.
        HBox.setHgrow( _altitudePane, Priority.ALWAYS );

        // Register the Altitude Pane radio buttons listener.
        _altitudePane._altitudeToggleGroup.selectedToggleProperty()
                                          .addListener( ( observable,
                                                          oldToggle,
                                                          newToggle ) -> {
                                              // If no toggle button
                                              // selected, re-select the
                                              // previous
                                              // button, but wrap this in a
                                              // JavaFX runLater thread to
                                              // ensure all FX event code
                                              // precedes the custom selection.
                                              if ( ( newToggle == null ) ) {
                                                  Platform.runLater( () -> _altitudePane._altitudeToggleGroup.selectToggle(
                                                          oldToggle ) );
                                                  return;
                                              }

                                              final Object userData
                                                      = newToggle.getUserData();
                                              if ( userData != null ) {
                                                  _pressurePane.setPressurePa( ( Double ) userData );
                                              }
                                          } );

        // NOTE: Sliders might switch presentation units, whereas JavaFX Bean
        // Properties are specified with a single unchanging unit, so we have to
        // be careful to only sync the cached pressure property to the slider
        // when a real magnitude change occurred vs. a Pressure Unit change.
        // Only update the Altitude selection if there is an actual magnitude
        // change in the Pressure as well, and not just a Pressure Unit change.
        _pressurePane._pressureSlider.valueProperty()
                                     .addListener( ( observableValue,
                                                     oldValue, newValue ) -> {
                                         final double storedValue
                                                 =
                                                 _pressurePane.getPressurePa();
                                         final double sliderValue
                                                 =
                                                 _pressurePane._pressureSlider.getPressurePa();
                                         double eps = 1e-7;

                                         // Make sure we don't set dirty flag
                                         // because of round-off
                                         // errors in slider value when
                                         // changing units, but wrap this
                                         // in a JavaFX runLater thread to
                                         // ensure all FX event code
                                         // precedes the custom selection.
                                         if ( ( FastMath.abs(
                                                 storedValue - sliderValue )
                                                >= eps ) ) {
                                             Platform.runLater( () -> _pressurePane.setPressurePa(
                                                     sliderValue ) );
                                         }

                                         // Set the appropriate altitude
                                         // toggle button if the new
                                         // Pressure value corresponds to one
                                         // of their ranges.
                                         // TODO: Review this logic and
                                         //  possibly invert the order,
                                         // as this doesn't seem to ever set
                                         // the altitude choice
                                         // since it appears to only look at
                                         // specific cutoff values
                                         // vs. entire ranges of values
                                         // between the altitude choices.
                                         eps = 1e-2;
                                         if ( FastMath.abs( sliderValue
                                                            - PhysicsConstants.PRESSURE_LOW_ALTITUDE_PA )
                                              <= eps ) {
                                             _altitudePane.setAltitude( Altitude.LOW );
                                         }
                                         else if ( FastMath.abs( sliderValue
                                                                 - PhysicsConstants.PRESSURE_MEDIUM_ALTITUDE_PA )
                                                   <= eps ) {
                                             _altitudePane.setAltitude( Altitude.MEDIUM );
                                         }
                                         else if ( FastMath.abs( sliderValue
                                                                 - PhysicsConstants.PRESSURE_HIGH_ALTITUDE_PA )
                                                   <= eps ) {
                                             _altitudePane.setAltitude( Altitude.HIGH );
                                         }
                                         else {
                                             _altitudePane.clearAltitude();
                                         }
                                     } );
    }

    // Reset all fields to the default values, regardless of state.
    // NOTE: This is done from the view vs. the model, as there may be more
    // than one component per property (e.g. the radio buttons for Altitude, as
    // part of Atmospheric Pressure as an alternate specification of Pressure).
    public void reset() {
        // Forward this method to the subsidiary panes.
        _temperaturePane.reset();
        _humidityPane.reset();
        _pressurePane.reset();
        _altitudePane.reset();
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground(
                backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        _temperaturePane.setForegroundFromBackground( backColor );
        _humidityPane.setForegroundFromBackground( backColor );
        _pressurePane.setForegroundFromBackground( backColor );
        _altitudePane.setForegroundFromBackground( backColor );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the lower-level layout containers.
        _temperaturePane.setGesturesEnabled( gesturesEnabled );
        _humidityPane.setGesturesEnabled( gesturesEnabled );
        _pressurePane.setGesturesEnabled( gesturesEnabled );
    }

    // Set and bind the Natural Environment reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setNaturalEnvironment( final NaturalEnvironmentProperties pNaturalEnvironmentProperties ) {
        // Cache the Natural Environment reference.
        naturalEnvironmentProperties = pNaturalEnvironmentProperties;

        // Forward this reference's observables to the subsidiary panes.
        _temperaturePane.setTemperatureKProperty( naturalEnvironmentProperties.temperatureKProperty() );
        _humidityPane.setHumidityRelativeProperty( naturalEnvironmentProperties.humidityRelativeProperty() );
        _pressurePane.setPressurePaProperty( naturalEnvironmentProperties.pressurePaProperty() );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the lower-level layout containers.
        _temperaturePane.setScrollingSensitivity( scrollingSensitivity );
        _humidityPane.setScrollingSensitivity( scrollingSensitivity );
        _pressurePane.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        // Forward this method to the lower-level layout containers.
        _temperaturePane.toggleGestures();
        _humidityPane.toggleGestures();
        _pressurePane.toggleGestures();
    }

    /**
     * Propagate the new Distance Unit to the relevant subcomponents.
     *
     * @param distanceUnit The Distance Unit to use for Altitude
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Altitude Pane.
        _altitudePane.updateDistanceUnit( distanceUnit );
    }

    /**
     * Propagate the new Pressure Unit to the relevant subcomponents.
     *
     * @param pressureUnit The Pressure Unit to use for the Environment
     */
    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Forward this method to the Pressure Pane.
        _pressurePane.updatePressureUnit( pressureUnit );
    }

    /**
     * Propagate the new Temperature Unit to the relevant subcomponents.
     *
     * @param temperatureUnit The Temperature Unit to use for the Environment
     */
    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Forward this method to the Temperature Pane.
        _temperaturePane.updateTemperatureUnit( temperatureUnit );
    }
}
