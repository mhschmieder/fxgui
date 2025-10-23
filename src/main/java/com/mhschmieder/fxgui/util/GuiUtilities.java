/*
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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
package com.mhschmieder.fxgui.util;

import com.mhschmieder.fxcontrols.control.ControlUtilities;
import com.mhschmieder.fxcontrols.util.MessageFactory;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.fxgui.dialog.DialogUtilities;
import com.mhschmieder.fxgui.layout.LayoutFactory;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.SystemType;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.tools.Borders;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * {@code GuiUtilities} is a utility class for methods related to top-level
 * JavaFX GUI functionality.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class GuiUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private GuiUtilities() {}

    // Predetermined Splash Screen dimensions; image will scale to fit.
    public static final int                             SPLASH_WIDTH                    = 600;
    public static final int                             SPLASH_HEIGHT                   = 400;

    public static final int                             LABEL_EDITOR_WIDTH_DEFAULT      = 320;

    // Default smallest screen size (4:3 AR), based on laptops (not netbooks).
    // :OTE: The next level up is typically 1280 x 1024, which is more useful.
    // NOTE: For retina displays, it is more commonly 1366 x 768 (native),
    // 1344 x 756 or 1280 x 720 (16:9), 1152 x 720 (16:10) or 1024 x 768 (4:3).
    public static final int                             LEGACY_SCREEN_WIDTH_DEFAULT     = 1024;
    public static final int                             LEGACY_SCREEN_HEIGHT_DEFAULT    = 768;

    // Modern screen size assumptions are based on 16:10 (in this case) or 16:9.
    public static final int                             SCREEN_WIDTH_DEFAULT            = 1440;
    public static final int                             SCREEN_HEIGHT_DEFAULT           = 900;

    // Set the minimum width and height for primary application windows.
    public static final int                             MINIMUM_WINDOW_WIDTH            = 500;
    public static final int                             MINIMUM_WINDOW_HEIGHT           = 300;

    // Toggle Buttons tend to be given bindings related to aspect ratio, and
    // must be given an initial preferred size or the bindings don't kick in on
    // the first layout round, so we experimented to find the width that is
    // least likely to make the button get taller -- on Windows 10, at least.
    public static final int                             TOGGLE_BUTTON_WIDTH_DEFAULT     = 72;

    /**
     * Labels by default are made as small as possible to contain their text,
     * but we prefer to have sufficient horizontal and vertical gaps for
     * legibility and separation of neighboring controls.
     */
    public static final Insets                          STATUS_LABEL_INSETS_DEFAULT     =
                                                                                    new Insets( 3.0d,
                                                                                                10.0d,
                                                                                                3.0d,
                                                                                                10.0d );

    // To avoid cut/paste errors with resource references, make global constants
    // for the CSS theme to be used for dark vs. light backgrounds.
    @SuppressWarnings("nls") public static final String DARK_BACKGROUND_CSS             =
                                                                            "/css/theme-dark.css";
    @SuppressWarnings("nls") public static final String LIGHT_BACKGROUND_CSS            =
                                                                             "/css/theme-light.css";

    public static void addStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                   final String jarRelativeStylesheetFilename ) {
        // If no valid style sheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilename == null )
                || ( jarRelativeStylesheetFilename.length() < 5 ) ) {
            return;
        }

        final URL stylesheetUrl = GuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        try {
            // If not found, the returned string is null, so we should either
            // check for null and throw an exception, or let the null string
            // throw an exception when used, which we capture below.
            final String stylesheetFilename = stylesheetUrl.toExternalForm();

            // NOTE: CSS loading can be timing-sensitive to JavaFX API calls
            // that also affect style attributes, so it might be safer to defer
            // the CSS loading so that it is applied to a more stable GUI.
            stylesheetFilenames.add( stylesheetFilename );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public static void addStylesheetAsJarResource( final Parent parent,
                                                   final String jarRelativeStylesheetFilename ) {
        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
    }

    public static void addStylesheetAsJarResource( final Scene scene,
                                                   final String jarRelativeStylesheetFilename ) {
        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
    }

    public static void addStylesheetsAsJarResource( final Parent parent,
                                                    final List< String > jarRelativeStylesheetFilenames ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilenames == null )
                || ( jarRelativeStylesheetFilenames.isEmpty() ) ) {
            return;
        }

        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        for ( final String jarRelativeStylesheetFilename : jarRelativeStylesheetFilenames ) {
            addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
        }
    }

    public static void addStylesheetsAsJarResource( final Scene scene,
                                                    final List< String > jarRelativeStylesheetFilenames ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilenames == null )
                || ( jarRelativeStylesheetFilenames.isEmpty() ) ) {
            return;
        }

        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        for ( final String jarRelativeStylesheetFilename : jarRelativeStylesheetFilenames ) {
            addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
        }
    }

    @SuppressWarnings("nls")
    public static List< String > getJarRelativeStylesheetFilenames( final SystemType systemType ) {
        // NOTE: The CSS files are copied from FxGuiToolkit as a starting point
        // and thus doesn't even begin to yet match our LAF for main Desktop.
        final List< String > jarRelativeStylesheetFilenames = new ArrayList<>();
        jarRelativeStylesheetFilenames.add( "/css/skin.css" );
        final String fontStylesheet = SystemType.MACOS.equals( systemType )
            ? "/css/font-mac.css"
            : "/css/font.css";
        jarRelativeStylesheetFilenames.add( fontStylesheet );
        return jarRelativeStylesheetFilenames;
    }

    @SuppressWarnings("nls")
    public static Label getTitleLabel( final String title ) {
        final Label titleLabel = new Label( title );

        // NOTE: This is temporary until we figure out why the CSS style from
        // the main stylesheet doesn't appear to be loaded when this is invoked.
        // titleLabel.getStyleClass().add( "title-text" );
        titleLabel
                .setStyle( "-fx-font-family: 'sans-serif'; -fx-font-size: 150.0%; -fx-font-style: normal; -fx-font-weight: bold; -fx-alignment: center;" );

        return titleLabel;
    }

    // TODO: Pass in the minimum height as a parameter?
    public static HBox getTitlePane( final Label titleLabel ) {
        final HBox titlePane = LayoutFactory.makeCenteredLabeledHBox( titleLabel );
        titlePane.setMinHeight( 32d );
        titleLabel.prefHeightProperty().bind( titlePane.heightProperty() );

        return titlePane;
    }

    public static Label getSvgImageLabel( final SVGPath svgImage,
                                          final Color svgColor,
                                          final double imageSize ) {
        final Label svgImageLabel = new Label();
        svgImageLabel.setAlignment( Pos.CENTER );
        svgImageLabel.setBackground( RegionUtilities.makeRegionBackground( svgColor ) );
        svgImageLabel.setOpacity( 100d );
        svgImageLabel.setMinSize( imageSize, imageSize );
        svgImageLabel.setMaxSize( imageSize, imageSize );
        svgImageLabel.setPrefSize( imageSize, imageSize );
        svgImageLabel.setShape( svgImage );

        return svgImageLabel;
    }

    public static Label getSvgImageLabel( final String svgContent,
                                          final Color svgColor,
                                          final double imageSize ) {
        final SVGPath svgImage = ControlUtilities.getSvgImage( svgContent );

        return getSvgImageLabel( svgImage, svgColor, imageSize );
    }

    public static VBox getSvgImageBox( final String svgContent,
                                       final Color svgColor,
                                       final double imageSize ) {
        final Label svgImageLabel = getSvgImageLabel( svgContent, svgColor, imageSize );

        return ControlUtilities.getImageBox( svgImageLabel, imageSize );
    }


    /**
     * Makes a stack from text and an image, for a controlled overlay, but
     * does not return it as the user provides containers to host the stack.
     * 
     * @param imageContainer The square that hosts the text and image
     * @param imageContainerDimension The width/height dimension of the square
     * @param imagePlaceholder The shape to use as an image placeholder
     * @param textItems The text items to overlay, in their VBox container
     * @param hBox1 The HBox that provides some initial offsets for the stack
     * @param hBox2 The HBox that provides some final offsets for the stack
     */
    public static void initTextAndImageStack( final Rectangle imageContainer,  
                                              final double imageContainerDimension,
                                              final Shape imagePlaceholder,
                                              final VBox textItems,
                                              final HBox hBox1,
                                              final HBox hBox2 ) {
        final Region imagePlaceholderIconRegion = new Region();
        imagePlaceholderIconRegion.setShape( imagePlaceholder );
        imagePlaceholderIconRegion.setBackground(
                RegionUtilities.makeRegionBackground( Color.WHITE, new Insets( 8d ) ) );

        final StackPane imagePane = new StackPane();
        imagePane.getChildren().add( imageContainer );
        imagePane.getChildren().add( imagePlaceholderIconRegion );
        imagePane.setAlignment( Pos.CENTER );
        imagePane.setMinSize( imageContainerDimension, imageContainerDimension );
        imagePane.setMaxSize( imageContainerDimension, imageContainerDimension );
        imagePane.setPadding( new Insets( 8d ) );

        // Create a border region that spans grid columns to avoid clipping.
        final Region borderRegion = new Region();

        final GridPane listItemRoot = new GridPane();
        listItemRoot.add( hBox1, 0, 0 );
        listItemRoot.add( imagePane, 1, 0 );
        listItemRoot.add( textItems, 2, 0 );
        listItemRoot.add( borderRegion, 1, 0, 3, 1 );
        listItemRoot.add( hBox2, 3, 0 );

        // NOTE: This didn't create the expected look, at least on macOS.
        // listItemRoot.setGridLinesVisible( true );

        listItemRoot.setHgap( 8d );

        final ColumnConstraints col0 = new ColumnConstraints();
        final ColumnConstraints col1 = new ColumnConstraints();
        final ColumnConstraints col2 = new ColumnConstraints(
                Double.MIN_VALUE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE );
        col2.setHgrow( Priority.ALWAYS );
        listItemRoot.getColumnConstraints().addAll( col0, col1, col2 );
    }

    /**
     * Creates a random color every time the method is called.
     *
     * @return A random Color with full opacity
     */
    public static Paint randomColor() {
        final Random random = new Random();
        final int r = random.nextInt( 255 );
        final int g = random.nextInt( 255 );
        final int b = random.nextInt( 255 );

        return Color.rgb( r, g, b );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Region region ) {
        final Insets insets = region.getInsets();

        return detectResizeTarget( mouseEvent, scene, insets );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Insets insets ) {
        final double borderWidth = 8.0d;

        return detectResizeTarget( mouseEvent, scene, insets, borderWidth );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Insets insets,
                                                   final double borderWidth ) {
        final double resizeMarginTop = FastMath.max( borderWidth, 0.5d * insets.getTop() );
        final double resizeMarginLeft = FastMath.max( borderWidth, 0.5d * insets.getLeft() );
        final double resizeMarginBottom = FastMath.max( borderWidth, 0.5d * insets.getBottom() );
        final double resizeMarginRight = FastMath.max( borderWidth, 0.5d * insets.getRight() );

        return detectResizeTarget( mouseEvent,
                                   scene,
                                   resizeMarginTop,
                                   resizeMarginRight,
                                   resizeMarginBottom,
                                   resizeMarginLeft );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final double resizeMarginTop,
                                                   final double resizeMarginRight,
                                                   final double resizeMarginBottom,
                                                   final double resizeMarginLeft ) {
        final double yMin = mouseEvent.getSceneY();
        final double xMin = mouseEvent.getSceneX();
        final double yMax = scene.getHeight() - yMin;
        final double xMax = scene.getWidth() - xMin;

        return ResizeTarget.detectResizeTarget( yMin,
                                                xMin,
                                                yMax,
                                                xMax,
                                                resizeMarginTop,
                                                resizeMarginRight,
                                                resizeMarginBottom,
                                                resizeMarginLeft );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Region region,
                                                   final Bounds layoutBounds,
                                                   final double borderWidth ) {
        final double mouseX = mouseEvent.getX();
        final double mouseY = mouseEvent.getY();

        final Insets insets = region.getInsets();

        final double diffMinY = FastMath.abs( ( layoutBounds.getMinY() - mouseY ) + insets.getTop() );
        final double diffMinX = FastMath.abs( ( layoutBounds.getMinX() - mouseX ) + insets.getLeft() );
        final double diffMaxY = FastMath.abs( layoutBounds.getMaxY() - mouseY - insets.getBottom() );
        final double diffMaxX = FastMath.abs( layoutBounds.getMaxX() - mouseX - insets.getRight() );

        final double resizeMarginTop = FastMath.max( borderWidth, 0.5d * insets.getTop() );
        final double resizeMarginLeft = FastMath.max( borderWidth, 0.5d * insets.getLeft() );
        final double resizeMarginBottom = FastMath.max( borderWidth, 0.5d * insets.getBottom() );
        final double resizeMarginRight = FastMath.max( borderWidth, 0.5d * insets.getRight() );

        return ResizeTarget.detectResizeTarget( diffMinY,
                                                diffMinX,
                                                diffMaxY,
                                                diffMaxX,
                                                resizeMarginTop,
                                                resizeMarginRight,
                                                resizeMarginBottom,
                                                resizeMarginLeft );
    }

    public static Cursor getCursorForResizeTarget( final ResizeTarget resizeTarget ) {
        Cursor cursor = Cursor.DEFAULT;
        
        switch ( resizeTarget ) {
        case NONE:
            cursor = Cursor.DEFAULT;
            break;
        case TOP:
            cursor = Cursor.N_RESIZE;
            break;
        case TOP_RIGHT:
            cursor = Cursor.NE_RESIZE;
            break;
        case RIGHT:
            cursor = Cursor.E_RESIZE;
            break;
        case BOTTOM_RIGHT:
            cursor = Cursor.SE_RESIZE;
            break;
        case BOTTOM:
            cursor = Cursor.S_RESIZE;
            break;
        case BOTTOM_LEFT:
            cursor = Cursor.SW_RESIZE;
            break;
        case LEFT:
            cursor = Cursor.W_RESIZE;
            break;
        case TOP_LEFT:
            cursor = Cursor.NW_RESIZE;
            break;
        default:
            break;
        }

        return cursor;
    }

    public static void clampStageSize( final Stage stage ) {
        final double width = stage.getWidth();
        final double clampedWidth = getClampedWidth( stage, width );
        if ( clampedWidth != width ) {
            stage.setWidth( clampedWidth );
        }

        final double height = stage.getHeight();
        final double clampedHeight = getClampedHeight( stage, height );
        if ( clampedHeight != height ) {
            stage.setHeight( clampedHeight );
        }

    }

    public static double getClampedWidth( final Stage stage, final double resizeWidthCandidate ) {
        final Screen activeScreen = findActiveScreen( stage );
        final Rectangle2D screenBounds = activeScreen.getVisualBounds();

        return getClampedWidth( stage, resizeWidthCandidate, screenBounds );
    }

    public static double getClampedWidth( final Stage stage,
                                          final double resizeWidthCandidate,
                                          final Rectangle2D bounds ) {
        final double allowedWidth = bounds.getWidth();

        return getClampedWidth( stage, resizeWidthCandidate, allowedWidth );
    }

    public static double getClampedWidth( final Stage stage,
                                          final double resizeWidthCandidate,
                                          final double allowedWidth ) {
        return ( resizeWidthCandidate > stage.getMaxWidth() )
            ? stage.getMaxWidth()
            : ( resizeWidthCandidate < stage.getMinWidth() )
                ? stage.getMinWidth()
                : FastMath.min( resizeWidthCandidate, allowedWidth );
    }

    public static double getClampedHeight( final Stage stage, final double resizeHeightCandidate ) {
        final Screen activeScreen = findActiveScreen( stage );
        final Rectangle2D screenBounds = activeScreen.getVisualBounds();

        return getClampedHeight( stage, resizeHeightCandidate, screenBounds );
    }

    public static double getClampedHeight( final Stage stage,
                                           final double resizeHeightCandidate,
                                           final Rectangle2D bounds ) {
        final double allowedHeight = bounds.getHeight();
        return getClampedHeight( stage, resizeHeightCandidate, allowedHeight );
    }

    public static double getClampedHeight( final Stage stage,
                                           final double resizeHeightCandidate,
                                           final double allowedHeight ) {
        return ( resizeHeightCandidate > stage.getMaxHeight() )
            ? stage.getMaxHeight()
            : ( resizeHeightCandidate < stage.getMinHeight() )
                ? stage.getMinHeight()
                : FastMath.min( resizeHeightCandidate, allowedHeight );
    }

    public static Screen findActiveScreen( final Window window ) {
        final double minX = window.getX();
        final double minY = window.getY();
        final double width = window.getWidth();
        final double height = window.getHeight();
        final Rectangle2D bounds = new Rectangle2D( minX, minY, width, height );

        final List< Screen > screens = Screen.getScreens();

        for ( final Screen screen : screens ) {
            final Rectangle2D screenRect = screen.getVisualBounds();

            // First, check for simple containment, as only one
            // screen can fully contain the supplied window.
            if ( screenRect.contains( bounds ) ) {
                return screen;
            }

            // Next, check for intersection of the interior.
            if ( screenRect.intersects( bounds ) ) {
                return screen;
            }
        }

        return Screen.getPrimary();
    }

    public static void updateToggleButtonSilently( final ToggleButton toggleButton,
                                                   final EventHandler< ActionEvent > selectionHandler,
                                                   final boolean selected ) {
        // Remove any existing selection handler so we don't get infinite
        // recursion on selection change callbacks during manual updates.
        toggleButton.setOnAction( actionEvent -> {} );
        toggleButton.setSelected( selected );
        toggleButton.setOnAction( selectionHandler );
    }

    // Converts a color to an rgba syntax that works in JavaFX 8 CSS
    // where other syntaxes don't (but should; there are bugs in Java 8).
    //
    // Primarily, this method is needed when a color is specified with
    // an alpha value; colors with no alpha work using every available
    // CSS syntax, as does the default color "name" for "transparent".
    //
    // As with some other methods here, this one actually comes from
    // FxGuiToolkit's ColorUtilities class, which we use very little of.
    public static String colorToRgba( final Color color ) {
        return "rgba(" + Double.toString( FastMath.floor( color.getRed() * 255.0d ) ) + ", "
                + Double.toString( FastMath.floor( color.getGreen() * 255.0d ) ) + ", "
                + Double.toString( FastMath.floor( color.getBlue() * 255.0d ) ) + ", "
                + Double.toString( color.getOpacity() ) + ")";
    }

    // Never speak of this code... ever again!
    public static void resizeTextAreaHeight( final TextArea textArea ) {
        final double totalWidth = textArea.getPrefWidth();
        resizeTextAreaHeight( textArea, totalWidth );
    }

    // Never speak of this code... ever again!
    public static void resizeTextAreaHeight( final TextArea textArea, final double totalWidth ) {
        final String text = textArea.getText();

        final Label l = new Label( text );
        l.setFont( textArea.getFont() );
        l.applyCss();

        final Text t = new Text( text );
        t.setFont( textArea.getFont() );
        t.setWrappingWidth( totalWidth );
        t.applyCss();

        final HBox hl = new HBox();
        hl.setMinWidth( totalWidth );
        hl.setPrefWidth( totalWidth );
        hl.setMaxWidth( totalWidth );
        hl.getChildren().add( l );
        final Scene sl = new Scene( hl );

        final HBox ht = new HBox();
        ht.setMinWidth( totalWidth );
        ht.setPrefWidth( totalWidth );
        ht.setMaxWidth( totalWidth );
        ht.getChildren().add( t );
        final Scene st = new Scene( ht );

        final double lHeight = l.prefHeight( Region.USE_COMPUTED_SIZE ) + 16d;
        final double tHeight = t.prefHeight( Region.USE_COMPUTED_SIZE ) + 16d;
        final double finalHeight = FastMath.max( lHeight, tHeight );

        textArea.setMinHeight( finalHeight );
        textArea.setPrefHeight( finalHeight );
        textArea.setMaxHeight( finalHeight );
    }

    public static void adaptDividerToRegionBounds( final Region region ) {
        region.layoutBoundsProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue == null ) {
                return;
            }

            final double x = 0.5d * newValue.getWidth();
            final double y = 0.5d * newValue.getHeight();
            final Color white0 = Color.web( "white", 0.0d );

            final Stop[] dividerStops = new Stop[] {
                                                     new Stop( 0.0d, Color.WHITE ),
                                                     new Stop( 0.5d, white0 ),
                                                     new Stop( 1.0d, white0 ) };

            final RadialGradient dividerGradient = new RadialGradient( 0.0d,
                                                                       0.0d,
                                                                       x,
                                                                       y,
                                                                       newValue.getWidth(),
                                                                       false,
                                                                       CycleMethod.NO_CYCLE,
                                                                       dividerStops );

            region.setBackground( RegionUtilities.makeRegionBackground( dividerGradient ) );
        } );
    }

    /**
     * This method centers a window on the screen, and takes the place of
     * Window.centerOnScreen() as that method doesn't seem to account for
     * screen resolution or other factors and thus results in off-centeredness.
     *
     * @param window
     *            The window to be centered on the screen
     */
    public static void centerOnScreen( final Window window ) {
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        window.setX( ( bounds.getMinX() + ( bounds.getWidth() / 2.0d ) )
                - ( SPLASH_WIDTH / 2.0d ) );
        window.setY( ( bounds.getMinY() + ( bounds.getHeight() / 2.0d ) )
                - ( SPLASH_HEIGHT / 2.0d ) );
    }

    /**
     * Clips the children of the specified {@link Region} to its current size.
     * This requires attaching a change listener to the region’s layout bounds
     * as JavaFX does not currently provide any built-in way to clip children.
     *
     * @param region
     *            The {@link Region} whose children to clip
     * @param arc
     *            The Rectangle arcWidth and {arcHeight} of the clipping
     *            {@link Rectangle}
     * @throws NullPointerException
     *             If {@code region} is {@code null}
     */
    public static void clipChildren( final Region region, final double arc ) {
        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth( arc );
        outputClip.setArcHeight( arc );
        region.setClip( outputClip );

        region.layoutBoundsProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    outputClip.setWidth( newValue.getWidth() );
                    outputClip.setHeight( newValue.getHeight() );
                } );
    }

    public static BackgroundImage getBackgroundImage( final Image image,
                                                      final boolean preserveRatio,
                                                      final double aspectRatio,
                                                      final double fitWidth,
                                                      final double fitHeight ) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        // Determine whether the source image Aspect Ratio should be preserved.
        // If not, use the supplied fit dimensions (if valid) or the intrinsic
        // image dimensions.
        final double fitWidthAdjusted = preserveRatio
            ? fitWidth
            : ( ( float ) aspectRatio != 0f )
                ? ( fitWidth > 0.0d )
                    ? fitWidth
                    : ( fitHeight > 0.0d ) ? fitHeight * aspectRatio : imageHeight * aspectRatio
                : ( fitWidth > 0.0d ) ? fitWidth : -1d;
        final double fitHeightAdjusted = preserveRatio
            ? fitHeight
            : ( ( float ) aspectRatio != 0f )
                ? ( fitHeight > 0.0d )
                    ? fitHeight
                    : ( fitWidth > 0.0d ) ? fitWidth / aspectRatio : imageWidth / aspectRatio
                : ( fitHeight > 0.0d ) ? fitHeight : -1d;

        final BackgroundSize backgroundSize = new BackgroundSize( fitWidthAdjusted,
                                                                  fitHeightAdjusted,
                                                                  false,
                                                                  false,
                                                                  true,
                                                                  true );
        final BackgroundImage backgroundImage = new BackgroundImage( image,
                                                                     BackgroundRepeat.NO_REPEAT,
                                                                     BackgroundRepeat.NO_REPEAT,
                                                                     BackgroundPosition.CENTER,
                                                                     backgroundSize );

        return backgroundImage;
    }

    /**
     * This method constructs an @HBox to center a @Label constructed from a
     * provided @String and set to adhere to style guidelines via custom CSS.
     *
     * @param bannerText
     *            The string to use for the Banner @Label
     * @return An @HBox that centers the Banner @Label
     */
    @SuppressWarnings("nls")
    public static HBox getBanner( final String bannerText ) {
        final Label bannerLabel = new Label( bannerText );
        bannerLabel.getStyleClass().add( "banner-text" );

        final HBox banner = new HBox();
        banner.getChildren().add( bannerLabel );
        banner.setAlignment( Pos.CENTER );

        return banner;
    }

    /**
     * @param labelText
     *            The text to use for a Column Header
     * @return The Label to use for a Column Header
     */
    @SuppressWarnings("nls")
    public static Label getColumnHeader( final String labelText ) {
        // We enforce a style of centered column headers, using bold italic
        // text.
        final Label columnHeader = new Label( labelText );

        columnHeader.getStyleClass().add( "column-header" );

        return columnHeader;
    }

    @SuppressWarnings("nls")
    public static Label getInfoLabel( final String info ) {
        final String infoLabelText = info;
        final Label infoLabel = new Label( infoLabelText );

        infoLabel.getStyleClass().add( "info-text" );

        return infoLabel;
    }

    // TODO: Pass in the minimum height as a parameter?
    public static HBox getInfoPane( final Label infoLabel ) {
        final HBox infoPane = new HBox();

        infoPane.setAlignment( Pos.CENTER );
        infoPane.getChildren().add( infoLabel );
        infoPane.setMinHeight( 40d );
        infoLabel.prefHeightProperty().bind( infoPane.heightProperty() );

        return infoPane;
    }

    /**
     * This method returns a completely initialized and styled button.
     *
     * Use this version when needing custom background and/or foreground colors,
     * and when resource lookup is not necessary as the optional label text is
     * already at hand. All parameters are optional and check for null pointers.
     *
     * @param buttonText
     *            Optional text label for the button
     * @param tooltipText
     *            Optional @Tooltip text
     * @param backColor
     *            The color to use for the background of the button
     * @param foreColor
     *            The color to use for the label text on the button
     * @return A labeled @Button adhering to custom style guidelines
     */
    public static Button getLabeledButton( final String buttonText,
                                           final String tooltipText,
                                           final Color backColor,
                                           final Color foreColor ) {
        // Some Action Buttons are made blank, as they may also indicate Status.
        // NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Button button = ( ( buttonText != null ) && !buttonText.trim().isEmpty() )
            ? new Button( buttonText )
            : new Button();

        // Optionally add tool tip text for the button, more verbose than label.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            button.setTooltip( new Tooltip( tooltipText ) );
        }

        if ( backColor != null ) {
            final Background background = ControlUtilities.getButtonBackground( backColor );
            button.setBackground( background );
        }
        if ( foreColor != null ) {
            button.setTextFill( foreColor );
        }

        // Apply drop-shadow effects when the mouse enters the Button.
        ControlUtilities.applyDropShadowEffect( button );

        return button;
    }

    /**
     * @param labelText
     *            The text to use for the Choice Box label
     * @param choiceBox
     *            The Choice Box to apply the label to
     * @return An {@link HBox} layout pane container for the Choice Box with its
     *         Label
     */
    public static HBox getLabeledChoiceBoxPane( final String labelText,
                                                final ChoiceBox< ? > choiceBox ) {
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( choiceBox );

        final HBox labeledChoiceBoxPane = new HBox();
        labeledChoiceBoxPane.getChildren().addAll( labelLabel, choiceBox );
        labeledChoiceBoxPane.setAlignment( Pos.CENTER_LEFT );
        labeledChoiceBoxPane.setPadding( new Insets( 12d ) );
        labeledChoiceBoxPane.setSpacing( 12d );

        return labeledChoiceBoxPane;
    }

    /**
     * @param labelText
     *            The text to use for the Combo Box label
     * @param comboBox
     *            The Combo Box to apply the label to
     * @return An {@link HBox} layout pane container for the Combo Box with its
     *         Label
     */
    public static HBox getLabeledComboBoxPane( final String labelText,
                                               final ComboBox< ? > comboBox ) {
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( comboBox );

        final HBox labeledComboBoxPane = new HBox();
        labeledComboBoxPane.getChildren().addAll( labelLabel, comboBox );
        labeledComboBoxPane.setAlignment( Pos.CENTER_LEFT );
        labeledComboBoxPane.setPadding( new Insets( 12d ) );
        labeledComboBoxPane.setSpacing( 12d );

        return labeledComboBoxPane;
    }

    /**
     * @param labelLabel
     *            The Label to use for a labeled Label
     * @param label
     *            The Label to which to apply the supplied Label
     * @return An {@link HBox} layout pane container for the Label with its
     *         Label
     */
    public static HBox getLabeledLabelPane( final Label labelLabel, final Label label ) {
        final HBox labeledLabelPane = new HBox();

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Label
     * @param label
     *            The Label to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Label with its
     *         Label
     */
    public static HBox getLabeledLabelPane( final String labelText, final Label label ) {
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        final HBox labeledLabelPane = new HBox();

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER_LEFT );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Spinner
     * @param spinner
     *            The Spinner to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Spinner with its
     *         Label
     */
    public static HBox getLabeledSpinnerPane( final String labelText, final Spinner< ? > spinner ) {
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( spinner );

        final HBox labeledSpinnerPane = new HBox();
        labeledSpinnerPane.setSpacing( 16d );
        labeledSpinnerPane.getChildren().addAll( labelLabel, spinner );

        return labeledSpinnerPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Text Field
     * @param textField
     *            The Text Field to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Text Field with its
     *         Label
     */
    public static HBox getLabeledTextFieldPane( final String labelText,
                                                final TextField textField ) {
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( textField );

        final HBox labeledTextFieldPane = new HBox();
        labeledTextFieldPane.getChildren().setAll( labelLabel, textField );
        labeledTextFieldPane.setAlignment( Pos.CENTER_LEFT );
        labeledTextFieldPane.setPadding( new Insets( 12d ) );
        labeledTextFieldPane.setSpacing( 12d );

        return labeledTextFieldPane;
    }

    @SuppressWarnings("nls")
    public static Label getPropertySheetLabel( final String labelText ) {
        final String propertySheetLabelText
                = labelText + ControlUtilities.LABEL_DELIMITER;

        final Label propertySheetLabel = new Label( propertySheetLabelText );

        propertySheetLabel.getStyleClass().add( "property-sheet-label" );

        return propertySheetLabel;
    }

    public static HBox getPropertySheetLabelPane( final String labelText, final Label label ) {
        final Label propertySheetLabel = getPropertySheetLabel( labelText );

        final HBox labeledLabelPane = new HBox();
        labeledLabelPane.getChildren().addAll( propertySheetLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER );
        labeledLabelPane.setPadding( new Insets( 6.0d ) );
        labeledLabelPane.setSpacing( 6.0d );

        return labeledLabelPane;
    }

    @SuppressWarnings("nls")
    public static Label getRowHeader( final String labelText ) {
        // We enforce a style of right-justified row headers using bold
        // italic text, and we add a colon and space for better comprehension of
        // context when setting a label as a row header vs. a column header.
        final String rowHeaderText = labelText + ControlUtilities.LABEL_DELIMITER;
        final Label rowHeader = new Label( rowHeaderText );

        rowHeader.getStyleClass().add( "row-header" );

        return rowHeader;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It automatically
     * applies the CSS Style ID.
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel() {
        // Get a status Label with the custom CSS Style ID applied.
        final Label statusLabel = getStatusLabel( true, true );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It provides the
     * option of skipping the CSS styling, as some contexts are too complex to
     * just have one setting. It assumes an initially empty text label.
     *
     * @param applyCssStyleId
     *            Flag for whether or not to apply the CSS Style ID
     * @param applyPadding
     *            Flag for whether or not to apply left and right side padding
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel( final boolean applyCssStyleId,
                                        final boolean applyPadding ) {
        // Get a status Label without any initial text applied.
        final Label statusLabel = getStatusLabel( null, applyCssStyleId, applyPadding );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It automatically
     * skips the CSS Style ID, but does provide initial text for the label.
     *
     * @param labelText
     *            The text to apply to the Label
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel( final String labelText ) {
        // Get a status Label without the custom CSS Style ID applied.
        final Label statusLabel = getStatusLabel( labelText, false, false );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It provides the
     * option of skipping the CSS styling, as some contexts are too complex to
     * just have one setting. It also optionally takes an initial text string.
     *
     * @param labelText
     *            The text to apply to the Label
     * @param applyCssStyleId
     *            Flag for whether or not to apply the CSS Style ID
     * @param applyPadding
     *            Flag for whether or not to apply left and right side padding
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    @SuppressWarnings("nls")
    public static Label getStatusLabel( final String labelText,
                                        final boolean applyCssStyleId,
                                        final boolean applyPadding ) {
        // Some Status Labels are made blank, as there may be no default Status.
        // NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Label statusLabel = ( ( labelText != null ) && !labelText.trim().isEmpty() )
            ? new Label( labelText )
            : new Label();

        // Some Labels are meant to just blend in with their background, or have
        // complex rules for rendering, so we flag whether to apply styles.
        if ( applyCssStyleId ) {
            // Match this toolkit's Look-and-Feel for non-editable Status Labels.
            statusLabel.getStyleClass().add( "fxguitoolkit-label" );
        }

        // Some Labels are used in paired contexts that already account for
        // padding; whereas some are used in large grid layouts and need it.
        if ( applyPadding ) {
            // Labels by default are made as small as possible to contain their
            // text, but we prefer to have sufficient horizontal and vertical
            // gaps for legibility and separation of neighboring controls.
            statusLabel.setPadding( STATUS_LABEL_INSETS_DEFAULT );
        }

        // Apply drop-shadow effects when the mouse enters a Status Label.
        ControlUtilities.applyDropShadowEffect( statusLabel );

        // Labels do not have context menus by default, and all we'd want to do
        // anyway is copy the text, just when it's something of interest like a
        // status or a value. Fortunately, it is trivial to add our own menu.
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem copyMenuItem = new MenuItem( "Copy" );
        copyMenuItem.setOnAction( evt -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString( statusLabel.getText() );
            clipboard.setContent( content );
        } );
        contextMenu.getItems().add( copyMenuItem );

        statusLabel.setOnContextMenuRequested( evt -> contextMenu
                .show( statusLabel, evt.getScreenX(), evt.getScreenY() ) );

        return statusLabel;
    }

    public static Node getTitledBorderWrappedNode( final Node node, final String title ) {
        // NOTE: The etched border doesn't handle radii very smoothly, so we
        // use the line border. A thin border is less obtrusive and distracting,
        // but still achieves the goal of slightly setting aside groups of
        // related GUI elements so that the user can see the workflow better.
        final Node titledBorderWrappedNode = Borders.wrap( node ).lineBorder().color( Color.WHITE )
                .thickness( 1.0d ).title( title ).radius( 2.5d, 2.5d, 2.5d, 2.5d ).build().build();

        return titledBorderWrappedNode;
    }
    
    /**
     * Wraps the provided {@link Node} with the provided {@link JFXPanel}.
     * <p>
     * The provided background color is converted from AWT to JavaFX, applied
     * to the JavaFX content pane that hosts the {@link Node}, and returned.
     * 
     * @param node The JavaFX Node to wrap in JFXPanel for use in Swing
     * @param jfxPanel The JFXPanel to use to wrap the JavaFX Node for Swing
     * @param awtColor The AWT color to convert to JavaFX and use for the panel
     * @param systemType The OS and architecture we are running on
     * @return The background {@link Color} of the wrapped panel
     */
    public static Color wrapNodeWithJFXPanel( final Node node,
                                              final JFXPanel jfxPanel,
                                              final java.awt.Color awtColor,
                                              final SystemType systemType ) {
        final BorderPane contentPane = new BorderPane();
        final Color fxColor = ColorUtilities.getColor( awtColor );
        contentPane.setBackground( ControlUtilities.getButtonBackground(fxColor ) );
        contentPane.setCenter( node );
        
        final Group rootGroup = new Group();
        rootGroup.getChildren().add( contentPane );
        
        final Scene scene = new Scene( rootGroup );
        jfxPanel.setScene( scene );
        
        // As the JavaFX Node will be hosted by a Swing JFXPanel, it is
        // necessary to manually set the CSS stylesheet on the owning Scene.
        final List< String > jarRelativeStylesheetFilenames 
            = getJarRelativeStylesheetFilenames( systemType );
        jarRelativeStylesheetFilenames.add( LIGHT_BACKGROUND_CSS );
        addStylesheetsAsJarResource( scene, jarRelativeStylesheetFilenames );
        
        return fxColor;
    }

    /**
     * This method initializes the persistent shared attributes of decorator
     * node groups, which generally are application managed and non-interactive.
     *
     * @param decoratorNodeGroup
     *            The decorator node group whose persistent shared attributes
     *            are to be set at initialization time
     */
    public static void initDecoratorNodeGroup( final Group decoratorNodeGroup ) {
        // Mark the decorator node group as unmanaged, as its preferred size
        // changes should not affect our layout, and as otherwise changes to
        // Distance Unit can create interim states that we never recover from
        // due to JavaFX making layout decisions for managed nodes/groups.
        decoratorNodeGroup.setManaged( false );

        // Do not auto-size decorator node group children, as we are managing
        // the nodes ourselves, and as otherwise changes to Distance Unit can
        // create interim states that we never recover from due to JavaFX making
        // layout decisions for auto-sized children.
        decoratorNodeGroup.setAutoSizeChildren( false );

        // For now, we do not allow mouse-picking of decorator node groups.
        decoratorNodeGroup.setMouseTransparent( true );
        decoratorNodeGroup.setPickOnBounds( false );
    }

    // Launch the user's default browser set to the specified initial URL.
    public static void launchBrowser( final HostServices hostServices, final String url ) {
        try {
            final URI uri = new URI( url );
            hostServices.showDocument( uri.toString() );
        }
        catch ( final NullPointerException | URISyntaxException e ) {
            // In theory, we produce the URL so it can't be null or invalid.
            e.printStackTrace();

            // Alert the user that the default browser couldn't launch.
            final String browserLaunchErrorMessage = MessageFactory.getBrowserLaunchErrorMessage();
            final String browserLaunchErrorMasthead = MessageFactory.getBadUrlMasthead();
            final String browserLaunchErrorTitle = MessageFactory.getBrowserLaunchErrorTitle();
            DialogUtilities.showWarningAlert(
                    browserLaunchErrorMessage,
                    browserLaunchErrorMasthead,
                    browserLaunchErrorTitle );
        }
    }

    public static void redirectTouchEvents( final Window window ) {
        // NOTE: This is an experiment to see if this fixes the crashes on the
        // new Touch Bars that Apple added to MacBook Pros in 2017.
        window.addEventFilter( TouchEvent.ANY, touchEvent -> {
            // Consume the touch event
            touchEvent.consume();

            // Create a fake Mouse Clicked Event for the current Touch Event.
            final TouchPoint touchPoint = touchEvent.getTouchPoint();
            final int clickCount = 1;
            final MouseEvent mouseEvent = new MouseEvent( touchEvent.getSource(),
                                                          touchEvent.getTarget(),
                                                          MouseEvent.MOUSE_CLICKED,
                                                          touchPoint.getX(),
                                                          touchPoint.getY(),
                                                          touchPoint.getScreenX(),
                                                          touchPoint.getScreenY(),
                                                          MouseButton.PRIMARY,
                                                          clickCount,
                                                          false,
                                                          false,
                                                          false,
                                                          false,
                                                          true,
                                                          false,
                                                          false,
                                                          true,
                                                          false,
                                                          false,
                                                          null );

            // Fire the fake traditional Mouse Event.
            final Scene scene = window.getScene();
            Event.fireEvent( scene.getRoot(), mouseEvent );
        } );
    }

    public static void removeStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                      final String jarRelativeStylesheetFilename ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilename == null )
                || ( jarRelativeStylesheetFilename.length() < 5 ) ) {
            return;
        }

        final URL stylesheetUrl = GuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        final String stylesheetFilename = stylesheetUrl.toExternalForm();
        try {
            // NOTE: CSS loading can be timing-sensitive to JavaFX API calls
            // that also affect style attributes, so it might be safer to defer
            // the CSS loading so that it is applied to a more stable GUI.
            stylesheetFilenames.remove( stylesheetFilename );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public static void replaceStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        removeStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilenameOld );
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilenameNew );
    }

    public static void replaceStylesheetAsJarResource( final Parent parent,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        replaceStylesheetAsJarResource( stylesheetFilenames,
                                        jarRelativeStylesheetFilenameOld,
                                        jarRelativeStylesheetFilenameNew );
    }

    public static void replaceStylesheetAsJarResource( final Scene scene,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        replaceStylesheetAsJarResource( stylesheetFilenames,
                                        jarRelativeStylesheetFilenameOld,
                                        jarRelativeStylesheetFilenameNew );
    }

    public static void setColumnHeaderLabelForeground( final GridPane gridPane,
                                                       final int firstColumn,
                                                       final int lastColumn,
                                                       final Color foregroundColor ) {
        // Set the column header label foreground.
        final ObservableList< Node > nodes = gridPane.getChildren();
        for ( int columnIndex = firstColumn; columnIndex <= lastColumn; columnIndex++ ) {
            final int columnHeaderNodeIndex = columnIndex;
            final Node node = nodes.get( columnHeaderNodeIndex );
            if ( node instanceof Labeled ) {
                final Labeled label = ( Labeled ) node;
                label.setTextFill( foregroundColor );
            }
        }
    }

    public static void setRowHeaderLabelForeground( final GridPane gridPane,
                                                    final int firstRow,
                                                    final int lastRow,
                                                    final int columnHeaderIndexAdjustment,
                                                    final int rowHeaderIndexAdjustment,
                                                    final int numberOfColumns,
                                                    final Color foregroundColor ) {
        // Set the row header label foreground.
        final ObservableList< Node > nodes = gridPane.getChildren();
        for ( int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++ ) {
            final int rowArrayIndexAdjustment = ( rowIndex - 1 ) * numberOfColumns;
            final int rowHeaderNodeIndex = columnHeaderIndexAdjustment + rowArrayIndexAdjustment
                    + rowHeaderIndexAdjustment;
            final Node node = nodes.get( rowHeaderNodeIndex );
            if ( node instanceof Labeled ) {
                final Labeled label = ( Labeled ) node;
                label.setTextFill( foregroundColor );
            }
        }
    }

    // Try to globally change the foreground theme for elements not exposed
    // in Java API calls, using our custom dark vs. light theme CSS files.
    // NOTE: For now, we assume dark and light themes only.
    public static void setStylesheetForTheme( final Parent parent,
                                              final Color backColor,
                                              final String jarRelativeStylesheetFilenameDark,
                                              final String jarRelativeStylesheetFilenameLight ) {
        final boolean isDark = ControlUtilities.isColorDark( backColor );
        if ( isDark ) {
            replaceStylesheetAsJarResource( parent,
                                            jarRelativeStylesheetFilenameLight,
                                            jarRelativeStylesheetFilenameDark );
        }
        else {
            replaceStylesheetAsJarResource( parent,
                                            jarRelativeStylesheetFilenameDark,
                                            jarRelativeStylesheetFilenameLight );
        }
    }

    // Try to globally change the foreground theme for elements not exposed
    // in Java API calls, using our custom dark vs. light theme CSS files.
    // NOTE: For now, we assume dark and light themes only.
    public static void setStylesheetForTheme( final Scene scene,
                                              final Color backColor,
                                              final String jarRelativeStylesheetFilenameDark,
                                              final String jarRelativeStylesheetFilenameLight ) {
        final boolean isDark = ControlUtilities.isColorDark( backColor );
        if ( isDark ) {
            replaceStylesheetAsJarResource( scene,
                                            jarRelativeStylesheetFilenameLight,
                                            jarRelativeStylesheetFilenameDark );
        }
        else {
            replaceStylesheetAsJarResource( scene,
                                            jarRelativeStylesheetFilenameDark,
                                            jarRelativeStylesheetFilenameLight );
        }
    }

    public static GridPane getLabeledTextAreaPane( final String labelText,
                                                   final TextArea textArea,
                                                   final ClientProperties clientProperties ) {
        final GridPane grid = LayoutFactory
                .makeGridPane( Pos.CENTER_LEFT, new Insets( 0.0d, 6.0d, 0.0d, 6.0d ), 6, 6 );

        // Although we put the label above the control, the size difference is
        // so huge that it won't be clear what the label is for unless we add a
        // colon as with horizontally paired controls.
        final Label labelLabel = ControlUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( textArea );

        labelLabel.getStyleClass().add( "text-area-label" );

        grid.add( labelLabel, 0, 0 );
        grid.add( textArea, 0, 1 );

        return grid;
    }
}
