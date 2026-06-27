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
package com.mhschmieder.fxgui.stage;

import com.mhschmieder.fxcontrols.action.NaturalEnvironmentActions;
import com.mhschmieder.fxcontrols.control.MenuFactory;
import com.mhschmieder.fxcontrols.control.NaturalEnvironmentToolBar;
import com.mhschmieder.fxcontrols.model.NaturalEnvironmentProperties;
import com.mhschmieder.fxgui.layout.NaturalEnvironmentPane;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jgraphics.input.ScrollingSensitivity;
import com.mhschmieder.jphysics.measure.DistanceUnit;
import com.mhschmieder.jphysics.measure.PressureUnit;
import com.mhschmieder.jphysics.measure.TemperatureUnit;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

public final class NaturalEnvironmentStage extends XStage {

    public static final String       NATURAL_ENVIRONMENT_FRAME_TITLE_DEFAULT  =
                                                                             "Natural Environment"; //$NON-NLS-1$

    // Default window locations and dimensions.
    public static final int          NATURAL_ENVIRONMENT_STAGE_X_DEFAULT      = 20;
    public static final int          NATURAL_ENVIRONMENT_STAGE_Y_DEFAULT      = 20;
    private static final int         NATURAL_ENVIRONMENT_STAGE_WIDTH_DEFAULT  = 640;
    private static final int         NATURAL_ENVIRONMENT_STAGE_HEIGHT_DEFAULT = 400;

    // Declare the actions.
    public NaturalEnvironmentActions _actions;

    // Declare the main tool bar.
    public NaturalEnvironmentToolBar _toolBar;

    // Declare the main content pane.
    public NaturalEnvironmentPane _naturalEnvironmentPane;

    // Cache a reference to the global Natural Environment.
    public NaturalEnvironmentProperties _naturalEnvironmentProperties;
    
    // Flag for whether vector graphics are supported.
    private final boolean _vectorGraphicsSupported;
    
    // Flag for whether Use Air Attenuation should be initialized to on or off.
    private final boolean _initialUseAirAttenuation;

    @SuppressWarnings("nls")
    public NaturalEnvironmentStage( final ProductBranding productBranding,
                                    final ClientProperties pClientProperties,
                                    final boolean vectorGraphicsSupported,
                                    final boolean initialUseAirAttenuation ) {
        // Always call the superclass constructor first!
        super( NATURAL_ENVIRONMENT_FRAME_TITLE_DEFAULT,
               "naturalEnvironment2",
               true,
               true,
               productBranding,
               pClientProperties );
        
        _vectorGraphicsSupported = vectorGraphicsSupported;
        _initialUseAirAttenuation = initialUseAirAttenuation;
        
        try {
            initStage( true );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add all of the relevant action handlers.
    @Override
    protected void addActionHandlers() {
        // Load the action handlers for the "File" actions.
        _actions.fileActions._closeWindowAction.setEventHandler( evt -> doCloseWindow() );
        _actions.fileActions._pageSetupAction.setEventHandler( evt -> doPageSetup() );
        _actions.fileActions._printAction.setEventHandler( evt -> doPrint() );

        // Load the action handlers for the "Export" actions.
        _actions.fileActions._exportActions._exportRasterGraphicsAction
                .setEventHandler( evt -> doExportImageGraphics() );
        _actions.fileActions._exportActions._exportVectorGraphicsAction
                .setEventHandler( evt -> doExportVectorGraphics() );

        // Load the action handlers for the "Background Color" choices.
        addBackgroundColorChoiceHandlers( _actions.settingsActions._backgroundColorChoices );

        // Load the action handlers for the "Window Size" actions.
        addWindowSizeActionHandlers( _actions.settingsActions._windowSizeActions );

        // Load the action handlers for the "Tools" actions.
        // NOTE: These are registered at the top-most level of the application.

        // Load the action handler for the "Reset" action.
        _actions.resetAction.setEventHandler( evt -> doReset() );
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Detect the ENTER key while the Use Air Attenuation Check Box has
        // focus, and use it to toggle the state (standard expected behavior).
        _toolBar._useAirAttenuationCheckBox.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Use Air Attenuation Toggle action.
                doUseAirAttenuationToggle();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

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

    private void bindProperties() {
        // The Use Air Attenuation flag is a simple boolean so can be
        // bi-directionally bound to its corresponding check box.
        _toolBar._useAirAttenuationCheckBox.selectedProperty()
                .bindBidirectional( _naturalEnvironmentProperties.airAttenuationAppliedProperty() );
    }

    private void doReset() {
        reset();
    }

    private void doUseAirAttenuationToggle() {
        // Toggle the state of the Use Air Attenuation Check Box.
        // NOTE: As this is tab-focus related, it must be performed on the
        // Check Box itself rather than on the associated action container.
        _toolBar._useAirAttenuationCheckBox
                .setSelected( !_toolBar._useAirAttenuationCheckBox.isSelected() );
    }

    protected void initStage( final boolean resizable ) {
        // First have the superclass initialize its content.
        initStage( "/icons/mhschmieder/TemperatureCelsius16.png",
                   NATURAL_ENVIRONMENT_STAGE_WIDTH_DEFAULT,
                   NATURAL_ENVIRONMENT_STAGE_HEIGHT_DEFAULT,
                   resizable );
        
        graphicsCategory = "Natural Environment";
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        _actions = new NaturalEnvironmentActions( clientProperties, _vectorGraphicsSupported );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _naturalEnvironmentPane = new NaturalEnvironmentPane( clientProperties );
        return _naturalEnvironmentPane;
    }

    // Add the Menu Bar for this Stage.
    @Override
    protected MenuBar loadMenuBar() {
        // Build the Menu Bar for this Stage.
        final MenuBar menuBar = MenuFactory.getNaturalEnvironmentMenuBar(
                clientProperties,
                _actions );

        // Return the Menu Bar so the superclass can use it.
        return menuBar;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new NaturalEnvironmentToolBar( clientProperties, _actions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    // Reset all fields to the default values, regardless of state.
    // NOTE: This is done from the view vs. the model, as there may be more
    //  than one component per property (e.g. the radio buttons for altitude, as
    //  part of atmospheric pressure as an alternate specification of pressure).
    @Override
    protected void reset() {
        _toolBar._useAirAttenuationCheckBox.setSelected( _initialUseAirAttenuation );

        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.reset();
    }
    
    @Override
    public String getBackgroundColor() {
        return _actions.getSelectedBackgroundColorName();
    }

    @Override
    public void selectBackgroundColor( final String backgroundColorName ) {
        _actions.selectBackgroundColor( backgroundColorName );
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this reference to the Natural Environment Pane.
        _naturalEnvironmentPane.setForegroundFromBackground( backColor );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.setGesturesEnabled( gesturesEnabled );
    }

    // Set and propagate the Natural Environment reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setNaturalEnvironment( final NaturalEnvironmentProperties pNaturalEnvironmentProperties) {
        // Cache the Natural Environment reference.
        _naturalEnvironmentProperties = pNaturalEnvironmentProperties;

        // Forward this reference to the Natural Environment Pane.
        _naturalEnvironmentPane.setNaturalEnvironment(_naturalEnvironmentProperties);

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( 
            final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this reference to the Natural Environment Pane.
        _naturalEnvironmentPane.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.toggleGestures();
    }

    /*
     * Propagate the new Distance Unit to the relevant subcomponents.
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.updateDistanceUnit( distanceUnit );
    }

    /*
     * Propagate the new Pressure Unit to the relevant subcomponents.
     */
    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.updatePressureUnit( pressureUnit );
    }

    /*
     * Propagate the new Temperature Unit to the relevant subcomponents.
     */
    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Forward this method to the Natural Environment Pane.
        _naturalEnvironmentPane.updateTemperatureUnit( temperatureUnit );
    }
}