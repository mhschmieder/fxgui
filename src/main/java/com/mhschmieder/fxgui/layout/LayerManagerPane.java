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

import com.mhschmieder.fxcontrols.control.ControlUtilities;
import com.mhschmieder.fxcontrols.control.LayerPropertiesTable;
import com.mhschmieder.fxcontrols.model.LayerProperties;
import com.mhschmieder.fxcontrols.util.LayerPropertiesManagement;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgui.stage.XStage;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

public final class LayerManagerPane extends BorderPane {

    // Declare the table and controls used for the Layer Properties.
    public LayerPropertiesTable layerPropertiesTable;

    // Maintain a reference to the owning stage, for enablement updates.
    protected XStage layerManager;

    // Declare change listeners for various observable properties.
    protected InvalidationListener layerSelectionChangeListener;

    public LayerManagerPane( final XStage pLayerManager,
                             final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        layerManager = pLayerManager;

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void addCallbackListeners() {
        // Detect changes in the table row selection, for enablement.
        addLayerSelectionListener();

        // If the user clicks outside the table, deselect all rows.
        addEventFilter( MouseEvent.MOUSE_CLICKED, evt -> {
            final Node sourceNode = evt.getPickResult().getIntersectedNode();
            if ( !ControlUtilities.isNodeInHierarchy(
                    sourceNode, layerPropertiesTable ) ) {
                // NOTE: We must remove the selection listeners while clearing
                // the selection, or we get run-time array out of bounds index
                // exceptions due to interim states where the selection index is
                // set to -1 inside Oracle's implementation code.
                removeLayerSelectionListener();
                clearSelection();
                addLayerSelectionListener();
                layerManager.updateContextualSettings();
            }
        } );
    }

    public void addLayerSelectionListener() {
        // Register an invalidation listener to update the contextual Layer
        // Management options when the selection changes.
        if ( layerSelectionChangeListener == null ) {
            layerSelectionChangeListener = listener -> layerManager
                    .updateContextualSettings();
        }
        layerPropertiesTable.getSelectionModel().selectedIndexProperty()
                .addListener( layerSelectionChangeListener );
    }

    public boolean canDeleteTableRows() {
        // Forward this method to the Layer Properties Table.
        return layerPropertiesTable.canDeleteTableRows();
    }

    public void clearSelection() {
        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.clearSelection();
    }

    public int createLayer() {
        // Forward this method to the Layer Properties Table.
        return layerPropertiesTable.insertTableRow();
    }

    public int deleteLayers() {
        // Forward this method to the Layer Properties Table.
        final int referenceIndex = layerPropertiesTable.deleteTableRows();

        return referenceIndex;
    }

    // TODO: Determine the need for this method as compared to similar methods
    //  for graphical objects, and restore the forwarding while implementing the
    //  method on the table class itself.
    // TODO: Switch from Apache Math to Apache RNG for the Random Generator.
    @SuppressWarnings("static-method")
    public String getNewLayerNameDefault() {
        // Forward this method to the Layer Properties Table.
        // return _layerPropertiesTable.getNewLayerNameDefault();
        return LayerPropertiesManagement.LAYER_NAME_DEFAULT + FastMath.random();
    }

    // TODO: Determine the need for this method as compared to similar methods
    //  for graphical objects, and restore the forwarding while implementing the
    //  method on the table class itself.
    // TODO: Switch from Apache Math to Apache RNG for the Random Generator.
    @SuppressWarnings("static-method")
    public String getUniqueLayerName( final String layerNameCandidate ) {
        // Forward this method to the Layer Properties Table.
        // return _layerPropertiesTable.getUniqueLayerName( layerNameCandidate
        // );
        return LayerPropertiesManagement.LAYER_NAME_DEFAULT + FastMath.random();
    }

    private void initPane( final ClientProperties pClientProperties ) {
        layerPropertiesTable = new LayerPropertiesTable( pClientProperties );

        setCenter(layerPropertiesTable);

        setPadding( new Insets( 12d ) );

        // Add the callback listeners for Layer Selection, etc.
        addCallbackListeners();
    }

    // TODO: Determine the need for this method as compared to similar
    // methods for graphical objects, and restore the forwarding while
    // implementing the method on the table class itself.
    @SuppressWarnings("static-method")
    public boolean isLayerNameUnique( final String layerNameCandidate ) {
        // Forward this method to the Layer Properties Table.
        // return _layerPropertiesTable.isLayerNameUnique( layerNameCandidate );
        return true;
    }

    public void removeLayerSelectionListener() {
        // Unregister the invalidation listener to avoid invalid selection index
        // exceptions during interim states where the table is empty before a
        // collection replacement.
        if ( layerSelectionChangeListener != null ) {
            layerPropertiesTable.getSelectionModel().selectedIndexProperty()
                    .removeListener( layerSelectionChangeListener );
        }
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        layerPropertiesTable.reset();
    }

    // Place editing focus in the specified row and column.
    public void setEditingFocus( final int rowIndex, final int columnIndex ) {
        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.setEditingFocus( rowIndex, columnIndex );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.setForegroundFromBackground( backColor );
    }

    public void setLayerCollection( final ObservableList<LayerProperties> layerCollection ) {
        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.setLayerCollection( layerCollection );
    }

    public void setSelectedRow( final int selectedRowIndex ) {
        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.selectRow( selectedRowIndex );
    }

    public void updateLayerCollection() {
        // Forward this method to the Layer Properties Table.
        layerPropertiesTable.updateLayerCollection();
    }

}
