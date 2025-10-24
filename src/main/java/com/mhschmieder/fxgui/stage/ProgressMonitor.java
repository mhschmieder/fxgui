/*
 * MIT License
 *
 * Copyright (c) 2024, 2025 Mark Schmieder
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
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxgui.stage;

import com.mhschmieder.fxcontrols.control.ControlUtilities;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.image.ImageUtilities;
import com.mhschmieder.fxgraphics.paint.ColorConstants;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.jcommons.text.NumberFormatUtilities;
import com.mhschmieder.jcommons.util.SystemType;
import com.mhschmieder.jmath.MathUtilities;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.math3.util.FastMath;

import java.text.NumberFormat;
import java.util.List;

/**
 * A Progress Monitor for contexts that do not have an observable Task.
 * <p>
 * If you have an observable Task, use ControlsFX TaskProgressView instead.
 * <p>
 * This class is designed to be reusable for multiple contexts, by setting
 * the text and number of steps appropriate to each context before use.
 * <p>
 * A typical use case is to watch a Task or VirtualThread, especially for
 * current step, to update the percentage of steps performed.
 * <p>
 * Register "setOnAction()" on the Cancel Button in your application code,
 * to use this Progress Monitor to support cancellation of a task or thread.
 */
public class ProgressMonitor extends Stage {

    private Label progressBanner;
    private Label progressRatioLabel;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    
    private Button cancelButton;
    
    private long numberOfSteps;

    /**
     * Makes a ProgressMonitor custom Stage with all parameters specified.
     * 
     * @param title The title to use in the title bar of the Stage
     * @param jarRelativeIconFilename JAR-relative path for title bar icon
     * @param bannerText The text to use for the banner atop the controls
     * @param cancelText The text to use for the Cancel Button
     * @param pNumberOfSteps The total number of steps to monitor progress
     * @param preferredWidth The preferred width of this window
     * @param preferredHeight The preferred height of this window
     * @param systemType The OS system type for the client
     */
    public ProgressMonitor( final String title,
                            final String jarRelativeIconFilename,
                            final String bannerText,
                            final String cancelText,
                            final long pNumberOfSteps,
                            final double preferredWidth,
                            final double preferredHeight,
                            final SystemType systemType ) {
        // Always call the superclass constructor first!
        super( StageStyle.DECORATED );

        // Initialize the Modality as soon as possible (API contract).
        initModality( Modality.NONE );
        
        numberOfSteps = pNumberOfSteps;

        try {
            initStage( title, 
                       jarRelativeIconFilename, 
                       bannerText, 
                       cancelText, 
                       preferredWidth, 
                       preferredHeight, 
                       systemType );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initStage( final String title,
                              final String jarRelativeIconFilename,
                              final String bannerText,
                              final String cancelText,
                              final double preferredWidth,
                              final double preferredHeight,
                              final SystemType systemType ) {
        progressBanner = new Label( bannerText );
        progressBanner.getStyleClass().add( "banner-text" );
       
        final Label progressControlsLabel = new Label( "Progress: " );
        progressRatioLabel = new Label();
        progressBar = new ProgressBar( -1.0d );
        progressIndicator = new ProgressIndicator( -1.0d );
        
        GridPane.setHalignment( progressBanner, HPos.LEFT );
        
        GridPane.setValignment( progressControlsLabel, VPos.TOP );
        GridPane.setValignment( progressRatioLabel, VPos.TOP );
        GridPane.setValignment( progressBar, VPos.TOP );
        GridPane.setValignment( progressIndicator, VPos.TOP );
        
        final GridPane gridPane = new GridPane();
        gridPane.setPadding( new Insets( 6.0d ) );
        gridPane.setHgap( 8.0d );
        gridPane.setVgap( 12.0d );
        gridPane.setAlignment( Pos.CENTER );
        
        gridPane.add( progressBanner, 0, 0, 3, 1 );
        
        gridPane.add( progressControlsLabel, 0, 2 );
        gridPane.add( progressRatioLabel, 1, 2 );
        gridPane.add( progressBar, 2, 2 );
        
        gridPane.add( progressIndicator, 3, 1, 1, 2 );
        
        final ButtonBar actionButtonBar = new ButtonBar();
        actionButtonBar.setPadding( new Insets(
                6.0d, 12.0d, 6.0d, 12.0d ) );
        cancelButton = ControlUtilities.getLabeledButton(
                cancelText, null, "cancel-button" );
        ButtonBar.setButtonData( cancelButton, ButtonData.CANCEL_CLOSE );
        cancelButton.setPrefWidth( 160.0d );
        final ObservableList< Node > actionButtons = actionButtonBar
                .getButtons();
        actionButtons.add( cancelButton );
        
        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter( gridPane );
        borderPane.setBottom( actionButtonBar );
        
        final Scene scene = new Scene( borderPane );
        
        final List< String > jarRelativeStylesheetFilenames = GuiUtilities
                .getJarRelativeStylesheetFilenames( systemType );
        GuiUtilities.addStylesheetsAsJarResource(
                scene,
                jarRelativeStylesheetFilenames );
        
        final Color backColor = ColorConstants.WINDOW_BACKGROUND_COLOR;
        final Background background = RegionUtilities.makeRegionBackground(
                backColor );
        borderPane.setBackground( background );
        GuiUtilities.setStylesheetForTheme( scene, 
                                            backColor, 
                                            XStage.DARK_BACKGROUND_CSS, 
                                            XStage.LIGHT_BACKGROUND_CSS );
        
        setWidth( preferredWidth );
        setHeight( preferredHeight );
        setResizable( false );

        setMinimizeIcon( jarRelativeIconFilename );

        setTitle( title );
        
        setScene( scene );
        
        // Make sure clicking "X" to close the window triggers Cancel.
        setOnCloseRequest( evt -> cancelButton.fire() );
    }

    /**
     * Sets the minimize icon, replacing any icon that was previously set.
     * <p>
     * This allows the Progress Monitor to be made once for multiple calling
     * contexts, with the icon replaced each time the monitor ius used for a
     * different task. The constructor still allows it to be set, for basic use.
     *
     * @param jarRelativeIconFilename the filename of the new minimize icon
     */
    public void setMinimizeIcon( final String jarRelativeIconFilename ) {
        final Image minimizeIconImage = ImageUtilities.loadImageAsJarResource(
                jarRelativeIconFilename, false );
        if ( minimizeIconImage != null ) {
            getIcons().setAll( minimizeIconImage );
        }
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
    
    public void setProgressBannerText( final String bannerText ) {
        progressBanner.setText( bannerText );
    }
    
    public void setCancelButtonText( final String cancelText ) {
        cancelButton.setText( cancelText );
    }
    
    public void setNumberOfSteps( final long pNumberOfSteps ) {
        numberOfSteps = pNumberOfSteps;
    }
    
    public void updateProgress( final long currentStep ) {
        // NOTE: Progress is percentile based but represented as { 0, 1 }, so we
        //  round down and clamp to avoid exceeding 100% (represented as 1.0).
        final int numberOfDecimalPlaces = 2;
        final double progressRatio = ( double ) currentStep / numberOfSteps;
        final double progressRatioClamped = FastMath.min( MathUtilities
                        .roundDownDecimal( progressRatio,
                                numberOfDecimalPlaces ), 1.0d );
        
        // Show the percentage of executed steps in the Progress Bar.
        // NOTE: For consistency, we always show exactly two decimal places.
        final NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits( 1 );
        numberFormat.setMaximumIntegerDigits( 1 );
        numberFormat.setMinimumFractionDigits( numberOfDecimalPlaces );
        numberFormat.setMaximumFractionDigits( numberOfDecimalPlaces );
        progressRatioLabel.setText( NumberFormatUtilities.formatDouble(
                progressRatioClamped, numberFormat ) );
        
        progressBar.setProgress( progressRatioClamped );
        progressIndicator.setProgress( progressRatioClamped );
        
        if ( !isShowing() ) {
            show();
        }
    }
}
