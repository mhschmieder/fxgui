/*
 * MIT License
 *
 * Copyright (c) 2020, 2025, Mark Schmieder. All rights reserved.
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
package com.mhschmieder.fxgui.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * {@code LayoutFactory} is a static factory class for ensuring a reduction in
 * copy/paste code for layout construction and styling that should be maintained
 * consistently across different GUI hierarchies and custom derived classes.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class LayoutFactory {

    /**
     * This method serves merely as a sanity check that the Maven integration
     * and builds work properly and also behave correctly inside Eclipse IDE. It
     * will likely get removed once I gain more confidence that I have solved
     * the well-known issues with Maven inside Eclipse as I move on to more
     * complex projects with dependencies (this project is quite simple and has
     * no dependencies at this time, until more functionality is added).
     *
     * @param args
     *            The command-line arguments for executing this class as the
     *            main entry point for an application
     *
     * @since 1.0
     */
    public static void main( final String[] args ) {
        System.out.println( "Hello Maven from FxGuiToolkit!" ); //$NON-NLS-1$
    }

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private LayoutFactory() {}

    /**
     * Returns a {@link GridPane} with no initial contents, but with most layout
     * styling initialized to match the passed parameters.
     *
     * @param alignment
     *            The alignment of the grid within its width and height
     * @param padding
     *            The top, left, bottom, and right padding around the region's
     *            content
     * @param hgap
     *            The width of the horizontal gaps between columns
     * @param vgap
     *            The height of the vertical gaps between rows
     * @return A {@link GridPane} with no initial contents, but with most layout
     *         styling initialized to match the passed parameters
     *
     * @version 1.0
     */
    public static GridPane makeGridPane( final Pos alignment,
                                         final Insets padding,
                                         final double hgap,
                                         final double vgap ) {
        final GridPane gridPane = new GridPane();

        gridPane.setAlignment( alignment );

        gridPane.setHgap( hgap );
        gridPane.setVgap( vgap );

        gridPane.setPadding( padding );

        return gridPane;
    }

    public static HBox makeCenteredLabeledHBox( final Labeled... labeled ) {
        // Host the Labeled in an HBox so we can center it properly.
        final HBox centeredLabeledHBox = new HBox( 24d );
        centeredLabeledHBox.getChildren().addAll( labeled );
        centeredLabeledHBox.setAlignment( Pos.CENTER );

        return centeredLabeledHBox;
    }
}
