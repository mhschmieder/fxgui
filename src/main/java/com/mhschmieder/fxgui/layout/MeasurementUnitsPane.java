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
import com.mhschmieder.fxcontrols.control.ControlUtilities;
import com.mhschmieder.fxcontrols.control.XComboBox;
import com.mhschmieder.fxcontrols.model.MeasurementUnitProperties;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.measure.DensityUnit;
import com.mhschmieder.jphysics.measure.DistanceUnit;
import com.mhschmieder.jphysics.measure.PressureUnit;
import com.mhschmieder.jphysics.measure.TemperatureUnit;
import com.mhschmieder.jphysics.measure.MassUnit;

import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class MeasurementUnitsPane extends VBox {

    protected XComboBox< DistanceUnit > distanceUnitSelector;
    // protected XComboBox< AngleUnit > angleUnitSelector;
    protected XComboBox< MassUnit > massUnitSelector;
    protected XComboBox< TemperatureUnit > temperatureUnitSelector;
    protected XComboBox< PressureUnit > pressureUnitSelector;
    protected XComboBox< DensityUnit > densityUnitSelector;

    // Cache a reference to the global Measurement Units.
    protected MeasurementUnitProperties measurementUnitProperties;

    public MeasurementUnitsPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties pClientProperties ) {
        final Label distanceUnitLabel = ControlUtilities.getControlLabel(
                "Distance Unit" );
        distanceUnitSelector = ControlFactory.makeDistanceUnitSelector(
                pClientProperties,
                true,
                false,
                DistanceUnit.defaultValue() );

        /*
        final Label angleUnitLabel = GuiUtilities.getControlLabel( "Angle
        Unit" );
        angleUnitSelector = PhysicsControlFactory.makeAngleUnitSelector(
                pClientProperties,
                true,
                AngleUnit.defaultValue() );
        */

        final Label massUnitLabel = ControlUtilities.getControlLabel(
                "Mass Unit" );
        massUnitSelector = ControlFactory.makeMassUnitSelector(
                pClientProperties,
                true,
                MassUnit.defaultValue() );

        final Label temperatureUnitLabel = ControlUtilities.getControlLabel(
                "Temperature Unit" );
        temperatureUnitSelector = ControlFactory.makeTemperatureUnitSelector(
                pClientProperties,
                true,
                TemperatureUnit.defaultValue() );

        final Label pressureUnitLabel = ControlUtilities.getControlLabel(
                "Pressure Unit" );
        pressureUnitSelector = ControlFactory.makePressureUnitSelector(
                pClientProperties,
                true,
                PressureUnit.defaultValue() );

        // Create a grid to host the Measurement Units controls.
        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 16.0d );
        gridPane.setVgap( 16.0d );
        gridPane.setPadding( new Insets( 0.0d, 16.0d, 0.0d, 16.0d ) );

        int row = 0;
        gridPane.add( distanceUnitLabel, 0, row );
        gridPane.add( distanceUnitSelector, 1, row++ );

        // TODO: Give some thoughts to Angle Units and implement.
        /*
        gridPane.add( angleUnitLabel, 0, row );
        gridPane.add( angleUnitSelector, 1, row++ );
        */

        gridPane.add( massUnitLabel, 0, row );
        gridPane.add( massUnitSelector, 1, row++ );

        gridPane.add( temperatureUnitLabel, 0, row );
        gridPane.add( temperatureUnitSelector, 1, row++ );

        gridPane.add( pressureUnitLabel, 0, row );
        gridPane.add( pressureUnitSelector, 1, row++ );

        getChildren().addAll( gridPane );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 16 ) );

        // Ensure that stacked components are all the same width.
        distanceUnitSelector.setMinWidth( 120.0d );
        distanceUnitSelector.setMaxWidth( 120.0d );

        /*
        angleUnitSelector.minWidthProperty().bind( distanceUnitSelector
        .widthProperty() );
        angleUnitSelector.maxWidthProperty().bind( distanceUnitSelector
        .widthProperty() );
        */

        massUnitSelector.minWidthProperty()
                        .bind( distanceUnitSelector.widthProperty() );
        massUnitSelector.maxWidthProperty()
                        .bind( distanceUnitSelector.widthProperty() );

        temperatureUnitSelector.minWidthProperty()
                               .bind( distanceUnitSelector.widthProperty() );
        temperatureUnitSelector.maxWidthProperty()
                               .bind( distanceUnitSelector.widthProperty() );

        pressureUnitSelector.minWidthProperty()
                            .bind( distanceUnitSelector.widthProperty() );
        pressureUnitSelector.maxWidthProperty()
                            .bind( distanceUnitSelector.widthProperty() );

        // Bind the data model to the respective GUI components.
        // TODO: Determine whether the listeners below are rendered redundant.
        // TODO: Re-enable this after figuring out why it wasn't part of the
        //  previous release.
        // bindProperties();

        // Load the event handler for the Distance Unit Selector.
        distanceUnitSelector.setOnAction( evt -> {
            final DistanceUnit distanceUnit = distanceUnitSelector.getValue();
            measurementUnitProperties.setDistanceUnit( distanceUnit );
        } );

        // Load the event handler for the Angle Unit Selector.
        /*
        angleUnitSelector.setOnAction( evt -> {
            final AngleUnit angleUnit = _angleUnitSelector.getValue();
            measurementUnits.setAngleUnit( angleUnit );
        } );
        */

        // Load the event handler for the Mass Unit Selector.
        massUnitSelector.setOnAction( evt -> {
            final MassUnit massUnit = massUnitSelector.getValue();
            measurementUnitProperties.setMassUnit( massUnit );
        } );

        // Load the event handler for the Temperature Unit Selector.
        temperatureUnitSelector.setOnAction( evt -> {
            final TemperatureUnit temperatureUnit
                    = temperatureUnitSelector.getValue();
            measurementUnitProperties.setTemperatureUnit( temperatureUnit );
        } );

        // Load the event handler for the Pressure Unit Selector.
        pressureUnitSelector.setOnAction( evt -> {
            final PressureUnit pressureUnit = pressureUnitSelector.getValue();
            measurementUnitProperties.setPressureUnit( pressureUnit );
        } );
    }

    /**
     * Reset all fields to the default values, regardless of state.
     */
    public void reset() {
        measurementUnitProperties.reset();

        // NOTE: We have to update the selected items as well, as the bindings
        // only work with respect to user actions vs. programmatic updates.
        updateMeasurementUnits( measurementUnitProperties );
    }

    public void updateMeasurementUnits( final MeasurementUnitProperties pMeasurementsUnits ) {
        updateDistanceUnit( pMeasurementsUnits.getDistanceUnit() );
        // updateAngleUnit( pMeasurementsUnits.getAngleUnit() );
        updateMassUnit( pMeasurementsUnits.getMassUnit() );
        updateDensityUnit( pMeasurementsUnits.getDensityUnit() );
        updateTemperatureUnit( pMeasurementsUnits.getTemperatureUnit() );
        updatePressureUnit( pMeasurementsUnits.getPressureUnit() );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        distanceUnitSelector.setValue( distanceUnit );
    }

    public void updateMassUnit( final MassUnit massUnit ) {
        massUnitSelector.setValue( massUnit );
    }

    public void updateDensityUnit( final DensityUnit densityUnit ) {
        densityUnitSelector.setValue( densityUnit );
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        temperatureUnitSelector.setValue( temperatureUnit );
    }

    /*
    public final void updateAngleUnit( final AngleUnit angleUnit ) {
        angleUnitSelector.setValue( angleUnit );
    }
    */

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        pressureUnitSelector.setValue( pressureUnit );
    }

    // Set and bind the Measurement Units reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setMeasurementUnits( final MeasurementUnitProperties pMeasurementUnitProperties ) {
        // Cache the Measurement Units reference.
        measurementUnitProperties = pMeasurementUnitProperties;

        // Set all the initial selections, as the bindings don't do this due to
        // differences between object properties and computed string values
        // regarding trigger points for bindings to kick in on initial
        // evaluation of an unchanged state.
        updateMeasurementUnits( pMeasurementUnitProperties );

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    private void bindProperties() {
        // A failed attempt to be less verbose. Perhaps could be improved.
        // Callable< String > distUnitStr = () -> measurementUnits
        // .getDistanceUnit().toCanonicalString();
        // ObjectProperty< DistanceUnit > distanceUnitProp = measurementUnits
        // .distanceUnitProperty();
        // StringBinding sb = Bindings.createStringBinding( distUnitStr,
        // distanceUnitProp );
        // distanceUnitSelector.valueProperty().bind( sb );

        distanceUnitSelector.getEditor()
                            .textProperty()
                            .bind( new StringBinding() {
                                {
                                    bind( measurementUnitProperties.distanceUnitProperty() );
                                }

                                @Override
                                protected String computeValue() {
                                    return measurementUnitProperties.getDistanceUnit()
                                                                    .label();
                                }
                            } );

        /*
        angleUnitSelector.getEditor().textProperty().bind( new StringBinding() {
            {
                bind( measurementUnits.angleUnitProperty() );
            }

            @Override
           protected String computeValue() {
                return measurementUnits.getAngleUnit().label();
            }
        } );
        */

        massUnitSelector.getEditor()
                        .textProperty()
                        .bind( new StringBinding() {
                              {
                                  bind( measurementUnitProperties.massUnitProperty() );
                              }

                              @Override
                              protected String computeValue() {
                                  return measurementUnitProperties.getMassUnit()
                                                                  .label();
                              }
                          } );

        densityUnitSelector.getEditor()
                          .textProperty()
                          .bind( new StringBinding() {
                              {
                                  bind( measurementUnitProperties.densityUnitProperty() );
                              }

                              @Override
                              protected String computeValue() {
                                  return measurementUnitProperties.getDensityUnit()
                                                                  .label();
                              }
                          } );

        temperatureUnitSelector.getEditor()
                               .textProperty()
                               .bind( new StringBinding() {
                                   {
                                       bind( measurementUnitProperties.temperatureUnitProperty() );
                                   }

                                   @Override
                                   protected String computeValue() {
                                       return measurementUnitProperties.getTemperatureUnit()
                                                                       .label();
                                   }
                               } );

        pressureUnitSelector.getEditor()
                            .textProperty()
                            .bind( new StringBinding() {
                                {
                                    bind( measurementUnitProperties.pressureUnitProperty() );
                                }

                                @Override
                                protected String computeValue() {
                                    return measurementUnitProperties.getPressureUnit()
                                                                    .label();
                                }
                            } );
    }
}
