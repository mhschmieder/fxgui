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

import com.mhschmieder.fxcontrols.control.TextEditor;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.io.RenderedGraphicsExportOptions;
import com.mhschmieder.fxgraphics.paint.ForegroundManager;
import com.mhschmieder.fxgui.swing.RenderedGraphicsTitledVectorizationPanel;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;

import java.awt.EventQueue;

import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * This is the main content pane for Rendered Graphics Export Preview windows.
 */
public final class RenderedGraphicsExportPreviewPane extends BorderPane implements
                                                                        ForegroundManager {

    // Cache the Client Properties (System Type, Locale, Client Type, etc.).
    public ClientProperties clientProperties;

    private HBox titleBox;
    private TextEditor titleEditor;

    // Cache the Rendered Graphics Export Options.
    private RenderedGraphicsExportOptions renderedGraphicsExportOptions;

    // Cache the Swing Node wrapper for the Graphics Export Source, for
    // background fills.
    private SwingNode graphicsPreviewNode;

    // Maintain a Swing Component reference for Graphics Export actions.
    private RenderedGraphicsTitledVectorizationPanel
            renderedGraphicsExportSource;

    public RenderedGraphicsExportPreviewPane( final ClientProperties pClientProperties,
                                              final RenderedGraphicsExportOptions pRenderedGraphicsExportOptions ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;

        renderedGraphicsExportOptions = pRenderedGraphicsExportOptions;

        graphicsPreviewNode = new SwingNode();

        try {
            initPane();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane() {
        final String title = renderedGraphicsExportOptions.getTitle();
        titleEditor = new TextEditor( title,
                                      "Title for EPS Document Header",
                                      true,
                                      true,
                                      clientProperties );
        titleEditor.setPrefWidth( 480.0d );
        titleEditor.setMinWidth( 480.0d );

        titleBox = GuiUtilities.getLabeledTextFieldPane( "Title", titleEditor );
        titleBox.setAlignment( Pos.CENTER );

        // Set the Title Editor to the top of the layout.
        setTop( titleBox );

        // NOTE: We defer the layout of the main content pane, as it is
        // dependent upon run-time content generation.
        setPadding( new Insets( 6.0d, 6.0d, 6.0d, 6.0d ) );

        // Bind the Title Editor to its associated property.
        titleEditor.textProperty()
                   .bindBidirectional( renderedGraphicsExportOptions.titleProperty() );

        // Load the change listener for the Export Auxiliary Panel property.
        renderedGraphicsExportOptions.exportAuxiliaryPanelProperty()
                                     .addListener( ( observable, oldValue,
                                                      newValue ) -> {
                                          // Update the visibility of the
                                          // associated panel.
                                          EventQueue.invokeLater( () -> renderedGraphicsExportSource.setAuxiliaryPanelVisible(
                                                  newValue ) );
                                      } );

        // Load the change listener for the Export Information Tables property.
        renderedGraphicsExportOptions.exportInformationTablesProperty()
                                     .addListener( ( observable, oldValue,
                                                      newValue ) -> {
                                          // Update the visibility of the
                                          // associated panel.
                                          EventQueue.invokeLater( () -> renderedGraphicsExportSource.setInformationTablesVisible(
                                                  newValue ) );
                                      } );

        // Load the change listener for the Export Optional Item property.
        renderedGraphicsExportOptions.exportOptionalItemProperty()
                                     .addListener( ( observable, oldValue,
                                                      newValue ) -> {
                                          // Update the visibility of the
                                          // associated panel.
                                          EventQueue.invokeLater( () -> renderedGraphicsExportSource.setOptionalItemVisible(
                                                  newValue ) );
                                      } );
    }

    public RenderedGraphicsExportOptions getRenderedGraphicsExportOptions() {
        return renderedGraphicsExportOptions;
    }

    public void setRenderedGraphicsExportOptions( final RenderedGraphicsExportOptions pRenderedGraphicsExportOptions ) {
        // Update the current export options (usually from preferences).
        renderedGraphicsExportOptions.setRenderedGraphicsExportOptions(
                pRenderedGraphicsExportOptions );
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground(
                backColor );
        setBackground( background );

        titleBox.setBackground( background );
    }

    /**
     * This method sets the container reference for exported graphics.
     *
     * @param pRenderedGraphicsExportSource The Swing container for the layout
     *                                     group to be exported
     */
    public void setRenderedGraphicsExportSource( final RenderedGraphicsTitledVectorizationPanel pRenderedGraphicsExportSource ) {
        // Cache the Graphics Export Source locally, for reference by panel
        // visibility change listeners.
        renderedGraphicsExportSource = pRenderedGraphicsExportSource;

        // Set the Swing Node wrapper for the provided Swing container.
        graphicsPreviewNode.setContent( renderedGraphicsExportSource );

        // Reset the Exported Graphics Preview Node to the Border Layout.
        setExportedGraphicsPreviewNode( graphicsPreviewNode );
    }

    /**
     * This method encapsulates the centered Border Pane layout position of the
     * Exported Graphics Preview Node.
     *
     * @param exportedGraphicsPreviewNode Exported graphics preview node
     */
    private void setExportedGraphicsPreviewNode( final Node exportedGraphicsPreviewNode ) {
        // First, wrap the content in a scroll pane so the user has more
        // flexibility and to compensate for small laptop screens.
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent( exportedGraphicsPreviewNode );

        // Replace the main preview content in the center of the layout.
        setCenter( scrollPane );

        // Attempt to force a re-layout as layout sizes may have changed.
        setNeedsLayout( true );
    }

    public void updateExportOptionsView() {
        // Make sure the previously selected options immediately take hold.
        EventQueue.invokeLater( () -> renderedGraphicsExportSource.updateExportOptionsView(
                renderedGraphicsExportOptions ) );
    }
}
