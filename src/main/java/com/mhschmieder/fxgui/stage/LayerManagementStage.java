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
 * This file is part of the fxlayergui Library
 *
 * You should have received a copy of the MIT License along with the
 * fxlayergui Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxlayergui
 */
package com.mhschmieder.fxgui.stage;

import com.mhschmieder.fxgui.dialog.DialogUtilities;
import com.mhschmieder.fxcontrols.action.LayerManagementActions;
import com.mhschmieder.fxlayercontrols.control.LayerManagementMenuFactory;
import com.mhschmieder.fxlayercontrols.control.LayerManagementToolBar;
import com.mhschmieder.fxlayercontrols.control.LayerPropertiesTable;
import com.mhschmieder.fxcontrols.model.LayerProperties;
import com.mhschmieder.fxcontrols.util.LayerManagementMessageFactory;
import com.mhschmieder.fxcontrols.util.LayerPropertiesManager;
import com.mhschmieder.fxgui.layout.LayerManagementPane;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Color;

import java.util.Optional;

public final class LayerManagementStage extends XStage {

    public static final String                LAYER_MANAGEMENT_FRAME_TITLE_DEFAULT  =
                                                                                   "Layer Management"; //$NON-NLS-1$

    // Default window locations and dimensions.
    public static final int                   LAYER_MANAGEMENT_STAGE_X_DEFAULT      = 20;
    public static final int                   LAYER_MANAGEMENT_STAGE_Y_DEFAULT      = 20;
    public static final int                   LAYER_MANAGEMENT_STAGE_WIDTH_DEFAULT  = 640;
    public static final int                   LAYER_MANAGEMENT_STAGE_HEIGHT_DEFAULT = 300;

    // Declare the actions.
    public LayerManagementActions             _actions;

    // Declare the main tool bar.
    public LayerManagementToolBar             _toolBar;

    // Cache the Layer Collection reference.
    private ObservableList<LayerProperties> _layerCollection;

    // Declare the main content pane.
    protected LayerManagementPane             _layerManagementPane;

    @SuppressWarnings("nls")
    public LayerManagementStage( final ProductBranding productBranding,
                                 final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( LAYER_MANAGEMENT_FRAME_TITLE_DEFAULT,
               "layerManagement",
               true,
               true,
               productBranding,
               pClientProperties );

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
    }

    public void addLayerSelectionListener() {
        // Forward this method to the Layer Management Pane.
        _layerManagementPane.addLayerSelectionListener();
    }

    // Add the Tool Bar's event listeners.
    // NOTE: This method is invoked on the JavaFX Application Thread.
    // TODO: Use appropriate methodology to add an action linked to both
    //  the toolbar buttons and their associated menu items, so that when one
    //  is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Load the event handler for the Create Layer Button.
        _toolBar._layerActionButtons._createButton.setOnAction( evt -> createLayer() );

        // Load the event handler for the Delete Layer(s) Button.
        _toolBar._layerActionButtons._deleteButton.setOnAction( evt -> deleteLayers() );
    }

    // Clear any selected rows in the Layers Table.
    protected void clearSelection() {
        // Forward this method to the Layer Management Pane.
        _layerManagementPane.clearSelection();
    }

    // Create a new Layer cloned from the selected Layer.
    public void createLayer() {
        // Insert the new Layer into the Layer Properties Table.
        final int referenceIndex = _layerManagementPane.createLayer();
        if ( referenceIndex < 0 ) {
            return;
        }

        // Immediately place editing focus into the default new Layer Name.
        setEditingFocus( referenceIndex, LayerPropertiesTable.COLUMN_LAYER_NAME );

        // Update the contextual Layer Management settings.
        // NOTE: This is redundant due to the callback on selection changed.
        // updateContextualSettings();
    }

    // Conditionally delete the selected Layer(s).
    public void deleteLayers() {
        // Make sure the user is aware of the repercussions first.
        final String message = LayerManagementMessageFactory.getDeleteLayersMessage();
        final String masthead = LayerManagementMessageFactory.getDeleteLayersMasthead();
        final String title = LayerManagementMessageFactory.getDeleteLayersTitle();
        final Optional< ButtonType > response = DialogUtilities
                .showConfirmationAlert( message, masthead, title, false );

        final ButtonType button = response.get();
        if ( !ButtonType.OK.equals( button ) && !ButtonType.YES.equals( button ) ) {
            return;
        }

        // Delete the selected Layer(s) from the Layer Properties Table.
        final int referenceIndex = _layerManagementPane.deleteLayers();

        // If the current Active Layer was deleted, reset it and the selected
        // row to the Default Layer. Otherwise, select the reference row from
        // the delete action if it is valid, or the last row in the Layer
        // Properties Table otherwise.
        // NOTE: Commented out, because the row isn't available yet, now that
        // we modify the source list directly and wait for dirty flag handlers
        // to sync the table's displayed rows.
        // NOTE: Deferred instead, in hopes that it's valid after dirty flag
        // callback has finished executing.
        if ( !LayerPropertiesManager.hasActiveLayer( _layerCollection ) ) {
            LayerPropertiesManager.setDefaultLayerActive( _layerCollection );
            _layerManagementPane.setSelectedRow( LayerPropertiesTable.ROW_DEFAULT_LAYER );
        }
        else {
            _layerManagementPane.setSelectedRow( referenceIndex );
        }

        // Update the contextual Layer Management settings.
        // NOTE: This is redundant due to the callback on selection changed.
        // updateContextualSettings();
    }

    // This method is used during File Load post-processing of graphical
    // objects that have Layer assignments, to add ones not already present. As
    // these Layers come from files vs. user editing, we already know the names
    // are either unique or correspond to Layers already present in the project,
    // thus we do not want to disambiguate names as with addLayer().
    public LayerProperties importLayer( final LayerProperties layerCandidate ) {
        // Add the Layer candidate to the collection if not already present.
        final LayerProperties layerAdjusted
                = LayerPropertiesManager.importLayer(
                        _layerCollection, layerCandidate );

        // Update the contextual Layer Management settings.
        updateContextualSettings();

        // Return the imported Layer if added, or the Default Layer.
        return layerAdjusted;
    }

    @SuppressWarnings("nls")
    public void initStage( final boolean resizable ) {
        // First have the superclass initialize its content.
        initStage( "/com/ahaSoft/icons/Layers16.png",
                   LAYER_MANAGEMENT_STAGE_WIDTH_DEFAULT,
                   LAYER_MANAGEMENT_STAGE_HEIGHT_DEFAULT,
                   resizable );

        // Clear any selected rows in the Layers Table, to set enablement.
        clearSelection();
        updateContextualSettings();
        
        graphicsCategory = "Layers";
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all the actions.
        _actions = new LayerManagementActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _layerManagementPane = new LayerManagementPane(
                this, clientProperties );
        return _layerManagementPane;
    }

    // Add the Menu Bar for this Stage.
    @Override
    protected MenuBar loadMenuBar() {
        // Build the Menu Bar for this Stage.
        final MenuBar menuBar = LayerManagementMenuFactory
                .getLayerManagementMenuBar( clientProperties, _actions );

        // Return the Menu Bar so the superclass can use it.
        return menuBar;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new LayerManagementToolBar( clientProperties, _actions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    public void removeLayerSelectionListener() {
        // Forward this method to the Layer Management Pane.
        _layerManagementPane.removeLayerSelectionListener();
    }

    // Place editing focus in the specified row and column.
    protected void setEditingFocus( final int rowIndex, final int columnIndex ) {
        // Forward this method to the Layer Management Pane.
        _layerManagementPane.setEditingFocus( rowIndex, columnIndex );
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this method to the Layer Management Pane.
        _layerManagementPane.setForegroundFromBackground( backColor );
    }

    // This method should only be called once, at startup, but it is now
    // written safely anyway, in that it removes the change listener (if
    // present) before adding it, due to the stacked weak listener issue.
    public void setLayerCollection( final ObservableList< LayerProperties > layerCollection ) {
        // Cache a local copy of the Layer Collection.
        _layerCollection = layerCollection;

        // Forward this method to the Layer Management Pane.
        _layerManagementPane.setLayerCollection( layerCollection );
    }

    // Update the contextual Layer Management settings.
    @Override
    public void updateContextualSettings() {
        // Determine whether the Layer Properties Table is populated or empty,
        // whether any rows other than the initial first-row Default "Layer 0"
        // are selected, and additionally whether the Layers on any selected
        // rows are hidden vs. visible. If no rows are selected, we seed the
        // logic auto-selected to the last row (current hard-wired as such).
        final boolean canDeleteRows = _layerManagementPane.canDeleteTableRows();

        // Conditionally disable or re-enable the Delete Button based on whether
        // it is legal to delete either the selected row(s) or the default
        // auto-select row (which is currently hard-wired to be the last row).
        _toolBar.setLayerDeleteEnabled( canDeleteRows );
    }

    public void updateLayerCollection() {
        // Forward this method to the Layer Management Pane.
        _layerManagementPane.updateLayerCollection();

        // The available options may change based on the new Layer Collection.
        updateContextualSettings();
    }
    
    @Override
    public String getBackgroundColor() {
        return _actions.getSelectedBackgroundColorName();
    }

    @Override
    public void selectBackgroundColor( final String backgroundColorName ) {
        _actions.selectBackgroundColor( backgroundColorName );
    }
}
