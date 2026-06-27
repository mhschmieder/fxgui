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

import com.mhschmieder.fxcontrols.action.SimulationActions;
import com.mhschmieder.fxcontrols.control.PredictToolBar;
import com.mhschmieder.fxgui.layout.DitheringPane;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public final class DitheringStage extends XStage {

    public static final String DITHERING_FRAME_TITLE_DEFAULT
            = "Dithering Amount";

    // Declare the actions.
    public SimulationActions simulationActions;

    // Declare the main tool bar.
    public PredictToolBar toolBar;

    // Declare the main content pane.
    public DitheringPane ditheringPane;
    
    // Initial value for dithering disablement (can't be passed to layout pane).
    protected final boolean initialDisableDithering;

    public DitheringStage( final ProductBranding pProductBranding,
                           final ClientProperties pClientProperties,
                           final boolean pInitialDisableDithering ) {
        // Always call the superclass constructor first!
        super( DITHERING_FRAME_TITLE_DEFAULT, 
               "dithering", 
               pProductBranding, 
               pClientProperties );
        
        initialDisableDithering = pInitialDisableDithering;

        initStage();
    }

    public double getDitheringAmount() {
        // Forward this method to the Dithering Pane.
        return ditheringPane.getDitheringAmount();
    }

    private void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/yusukeKamiyamane/fugue/ImageBlur16.png", 
                   240.0d, 
                   120.0d, 
                   false );
    }

    public boolean isUseDithering() {
        // Forward this method to the Dithering Pane.
        return ditheringPane.isUseDithering();
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        simulationActions = new SimulationActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        ditheringPane = new DitheringPane( clientProperties, initialDisableDithering );
        return ditheringPane;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        toolBar = new PredictToolBar( clientProperties, simulationActions );

        // Return the Tool Bar so the superclass can use it.
        return toolBar;
    }

    // NOTE: This is the method to use when updating from Preferences.
    public void updateDithering( final boolean useDithering, 
                                 final double ditheringAmount ) {
        // Forward this method to the Dithering Pane.
        ditheringPane.updateDithering( useDithering, ditheringAmount );
    }
}