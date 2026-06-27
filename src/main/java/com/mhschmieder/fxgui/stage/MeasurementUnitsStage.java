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
 * You should have received a copy of the MIT License along with the FxPhysics
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxphysics
 */
package com.mhschmieder.fxgui.stage;

import com.mhschmieder.fxcontrols.action.MeasurementUnitsActions;
import com.mhschmieder.fxcontrols.control.MeasurementUnitsToolBar;
import com.mhschmieder.fxgui.layout.MeasurementUnitsPane;
import com.mhschmieder.fxphysicscontrols.model.MeasurementUnitProperties;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class MeasurementUnitsStage extends XStage {

    public static final String     MEASUREMENT_UNITS_FRAME_TITLE_DEFAULT  = "Measurement Units"; //$NON-NLS-1$

    // Default window locations and dimensions.
    private static final int       MEASUREMENT_UNITS_FRAME_WIDTH_DEFAULT  = 320;
    private static final int       MEASUREMENT_UNITS_FRAME_HEIGHT_DEFAULT = 280;

    // Declare the actions.
    public MeasurementUnitsActions _actions;

    // Declare the main tool bar.
    public MeasurementUnitsToolBar _toolBar;

    // Declare the main content pane.
    public MeasurementUnitsPane _measurementUnitsPane;

    @SuppressWarnings("nls")
    public MeasurementUnitsStage( final boolean needDistanceUnits,
                                  final boolean needAngleUnits,
                                  final boolean needWeightUnits,
                                  final boolean needTemperatureUnits,
                                  final boolean needPressureUnits,
                                  final ProductBranding productBranding,
                                  final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( MEASUREMENT_UNITS_FRAME_TITLE_DEFAULT,
               "measurementUnits",
               productBranding,
               pClientProperties );

        try {
            initStage( needDistanceUnits,
                       needAngleUnits,
                       needWeightUnits,
                       needTemperatureUnits,
                       needPressureUnits );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add all of the relevant action handlers.
    @Override
    protected void addActionHandlers() {
        // Load the action handler for the "Reset" action.
        _actions._resetAction.setEventHandler( evt -> doReset() );
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Detect the ENTER key while the Reset Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _toolBar._resetButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Reset action.
                doReset();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    protected void doReset() {
        reset();
    }

    @SuppressWarnings("nls")
    protected void initStage( final boolean needDistanceUnits,
                              final boolean needAngleUnits,
                              final boolean needWeightUnits,
                              final boolean needTemperatureUnits,
                              final boolean needPressureUnits ) {
        // First have the superclass initialize its content.
        initStage( "/com/led24/icons/RulerCorner16.png",
                   MEASUREMENT_UNITS_FRAME_WIDTH_DEFAULT,
                   MEASUREMENT_UNITS_FRAME_HEIGHT_DEFAULT,
                   false );
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        _actions = new MeasurementUnitsActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _measurementUnitsPane = new MeasurementUnitsPane( clientProperties );
        return _measurementUnitsPane;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new MeasurementUnitsToolBar( clientProperties, _actions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    // Reset all fields to the default values, regardless of state.
    // NOTE: This is done from the view vs. the model, as there may be more
    // than one component per property.
    @Override
    protected void reset() {
        // Forward this method to the Measurement Units Pane.
        _measurementUnitsPane.reset();
    }

    // Set and propagate the Measurement Units reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setMeasurementUnits( final MeasurementUnitProperties pMeasurementUnitProperties) {
        // Forward this reference to the Measurement Units Pane.
        _measurementUnitsPane.setMeasurementUnits(pMeasurementUnitProperties);
    }
}
