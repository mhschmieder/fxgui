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

import com.mhschmieder.fxcontrols.model.Region2DProperties;
import com.mhschmieder.fxcontrols.model.SurfaceProperties;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.fxgraphics.paint.ForegroundManager;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * This is an information pane for a 2D Region's Surfaces/Materials.
 */
public final class SurfacesInformationPane extends VBox implements
                                                        ForegroundManager {

    public static final String SURFACE_LABEL_LABEL = "Surface";

    // Declare default formatted data for each label.
    private static final String SURFACE1_LABEL_DEFAULT = SURFACE_LABEL_LABEL
                                                         + " 1 = Bypassed";
    private static final String SURFACE2_LABEL_DEFAULT = SURFACE_LABEL_LABEL
                                                         + " 2 = Bypassed";
    private static final String SURFACE3_LABEL_DEFAULT = SURFACE_LABEL_LABEL
                                                         + " 3 = Bypassed";
    private static final String SURFACE4_LABEL_DEFAULT = SURFACE_LABEL_LABEL
                                                         + " 4 = Bypassed";

    private Label surface1Label;
    private Label surface2Label;
    private Label surface3Label;
    private Label surface4Label;

    // Keep a cached copy of the Region2D reference, as it is global per
    // session and can be used to update status and Surface Materials.
    private Region2DProperties _region2DProperties;

    public SurfacesInformationPane( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties pClientProperties ) {
        surface1Label = GuiUtilities.getStatusLabel( SURFACE1_LABEL_DEFAULT );
        surface2Label = GuiUtilities.getStatusLabel( SURFACE2_LABEL_DEFAULT );
        surface3Label = GuiUtilities.getStatusLabel( SURFACE3_LABEL_DEFAULT );
        surface4Label = GuiUtilities.getStatusLabel( SURFACE4_LABEL_DEFAULT );

        getChildren().addAll( surface1Label, surface2Label, surface3Label,
                              surface4Label );
        setAlignment( Pos.CENTER_LEFT );

        setPadding( new Insets( 6.0d ) );
    }

    public void reset() {
        surface1Label.setText( SURFACE1_LABEL_DEFAULT );
        surface2Label.setText( SURFACE2_LABEL_DEFAULT );
        surface3Label.setText( SURFACE3_LABEL_DEFAULT );
        surface4Label.setText( SURFACE4_LABEL_DEFAULT );
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground(
                backColor );
        setBackground( background );

        final Color foregroundColor
                = ColorUtilities.getForegroundFromBackground( backColor );
        surface1Label.setTextFill( foregroundColor );
        surface2Label.setTextFill( foregroundColor );
        surface3Label.setTextFill( foregroundColor );
        surface4Label.setTextFill( foregroundColor );
    }

    // Set and propagate the Region2D reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setRegion2D( final Region2DProperties region2DProperties ) {
        // Cache the current Region2D reference, for Surface Materials.
        _region2DProperties = region2DProperties;

        // Load the invalidation listener for the "Surface Name Changed"
        // binding.
        _region2DProperties.surfaceNameChangedProperty()
                           .addListener( invalidationListener -> updateLabels() );

        // Load the invalidation listener for the "Surface Value Changed"
        // binding.
        _region2DProperties.surfaceValueChangedProperty()
                           .addListener( invalidationListener -> updateLabels() );
    }

    // Update the cached Surface Materials and Bypassed/Enabled status.
    public void updateLabels() {
        final ObservableList< SurfaceProperties > numberedSurfaceProperties
                = _region2DProperties.getSurfaceProperties();

        final SurfaceProperties surface1Properties
                = numberedSurfaceProperties.get( 0 );
        final String sSurface1Status = surface1Properties.isSurfaceBypassed()
                                       ? "Bypassed"
                                       //$NON-NLS-1$
                                       : surface1Properties.getSurfaceMaterial()
                                                           .abbreviation();
        final String newSurface1Label = "Surface "
                                     + surface1Properties.getSurfaceNumber()
                                     + ": " //$NON-NLS-1$ //$NON-NLS-2$
                                     + surface1Properties.getSurfaceName()
                                     + " = " + sSurface1Status; //$NON-NLS-1$

        final SurfaceProperties surface2Properties
                = numberedSurfaceProperties.get( 1 );
        final String sSurface2Status = surface2Properties.isSurfaceBypassed()
                                       ? "Bypassed"
                                       //$NON-NLS-1$
                                       : surface2Properties.getSurfaceMaterial()
                                                           .abbreviation();
        final String newSurface2Label = "Surface "
                                     + surface2Properties.getSurfaceNumber()
                                     + ": " //$NON-NLS-1$ //$NON-NLS-2$
                                     + surface2Properties.getSurfaceName()
                                     + " = " + sSurface2Status; //$NON-NLS-1$

        final SurfaceProperties surface3Properties
                = numberedSurfaceProperties.get( 2 );
        final String sSurface3Status = surface3Properties.isSurfaceBypassed()
                                       ? "Bypassed"
                                       //$NON-NLS-1$
                                       : surface3Properties.getSurfaceMaterial()
                                                           .abbreviation();
        final String newSurface3Label = "Surface "
                                     + surface3Properties.getSurfaceNumber()
                                     + ": " //$NON-NLS-1$ //$NON-NLS-2$
                                     + surface3Properties.getSurfaceName()
                                     + " = " + sSurface3Status; //$NON-NLS-1$

        final SurfaceProperties surface4Properties
                = numberedSurfaceProperties.get( 3 );
        final String sSurface4Status = surface4Properties.isSurfaceBypassed()
                                       ? "Bypassed"
                                       //$NON-NLS-1$
                                       : surface4Properties.getSurfaceMaterial()
                                                           .abbreviation();
        final String newSurface4Label = "Surface "
                                     + surface4Properties.getSurfaceNumber()
                                     + ": " //$NON-NLS-1$ //$NON-NLS-2$
                                     + surface4Properties.getSurfaceName()
                                     + " = " + sSurface4Status; //$NON-NLS-1$

        // Update the associated labels in the information pane.
        surface1Label.setText( newSurface1Label );
        surface2Label.setText( newSurface2Label );
        surface3Label.setText( newSurface3Label );
        surface4Label.setText( newSurface4Label );
    }

    public void updateView() {
        updateLabels();
    }

    public String[] getSurfaceInformation() {
        // Collect the information fields to render to a single-column table.
        final String[] information = new String[ 4 ];
        int i = 0;
        information[ i++ ] = surface1Label.getText();
        information[ i++ ] = surface2Label.getText();
        information[ i++ ] = surface3Label.getText();
        information[ i++ ] = surface4Label.getText();
        return information;
    }
}
