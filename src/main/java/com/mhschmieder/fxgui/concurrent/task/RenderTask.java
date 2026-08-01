/*
 * MIT License
 *
 * Copyright (c) 2025, 2026 Mark Schmieder. All rights reserved.
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
package com.mhschmieder.fxgui.concurrent.task;

import com.mhschmieder.jgraphics.render.RenderingProgress;
import com.mhschmieder.jgraphics.render.RenderingState;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

import java.time.Month;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RenderTask extends Task< Image > {

    protected final RenderingProgress renderingProgress;

    protected final Month month;

    protected final String runDirectory;

    protected final WritableImage image;

    protected final PixelWriter pixelWriter;

    protected final int width;

    protected final int height;

    public RenderTask( final RenderingProgress pRenderingProgress,
                       final Month pMonth,
                       final String pRunDirectory,
                       final WritableImage pImage ) {
        runDirectory =  pRunDirectory;
        renderingProgress = pRenderingProgress;
        month = pMonth;

        if ( pImage == null ) {
            image = new WritableImage( 1, 1 );
            cancel();
        } else {
            image = pImage;
        }

        pixelWriter = image.getPixelWriter();
        width = ( int ) FastMath.ceil( image.getWidth() );
        height = ( int ) FastMath.ceil( image.getHeight() );
    }

    @Override
    protected Image call() {
        if ( image == null ) {
            cancel();
            return null;
        }

        renderingProgress.renderingState = RenderingState.STARTED;
        renderingProgress.numberOfSteps = ( long ) width * height;
        renderingProgress.currentStep = 0L;

        loopThroughImagePixels();

        return image;
    }

    /**
     * Iterates through every pixel location within the {@link WritableImage}
     * to call {@link #fillImage(int, int)} for every {@link Point2D}.
     */
    protected void loopThroughImagePixels() {
        final int renderThreadCount = isAsyncFillImageEnabled()
                ? getRenderThreadCount()
                : 1;
        if ( renderThreadCount <= 1 ) {
            loopThroughImagePixelsSequentially();
            return;
        }

        loopThroughImagePixelsAsync( renderThreadCount );
    }

    /**
     * Returns the number of worker threads to use for asynchronous rendering.
     * <p>
     * The count is bounded by the image width so that no worker is created
     * without at least one possible starting column.
     *
     * @return the number of worker threads to use for this render.
     */
    private int getRenderThreadCount() {
        final int numberOfAvailableProcessors = Runtime.getRuntime()
                .availableProcessors();
        return Math.clamp( width, 1, numberOfAvailableProcessors );
    }

    /**
     * Indicates whether this task can compute pixels asynchronously.
     * <p>
     * Subclasses should return {@code false} when their underlying model,
     * coordinate callback, or data source cannot safely support concurrent
     * calls. The base implementation enables async rendering.
     *
     * @return {@code true} if {@link #fillImage(int, int)} may be called from
     * multiple worker threads, otherwise {@code false}.
     */
    protected boolean isAsyncFillImageEnabled() {
        return true;
    }

    /**
     * Renders every image column on the current task thread.
     * <p>
     * This is used as the fallback path for single-column images and for
     * subclasses that opt out of asynchronous rendering.
     */
    private void loopThroughImagePixelsSequentially() {
        final AtomicLong currentStep = new AtomicLong(
                renderingProgress.currentStep );
        for ( int x = 0; x < width; x ++ ) {
            if ( isCancelled() ) {
                return;
            }

            fillImageColumn( x, currentStep );
        }
    }

    /**
     * Renders image columns using a fixed worker pool.
     * <p>
     * Work is distributed by column stride rather than adjacent column ranges.
     * For example, with four workers, worker zero renders columns
     * {@code 0, 4, 8...}, worker one renders {@code 1, 5, 9...}, and so on.
     * This avoids concentrating early work into neighboring columns when some
     * areas of the image are more expensive than others.
     *
     * @param renderThreadCount the number of worker tasks to submit.
     */
    private void loopThroughImagePixelsAsync( final int renderThreadCount ) {
        final AtomicLong currentStep = new AtomicLong(
                renderingProgress.currentStep );
        final ExecutorService executorService
                = Executors.newFixedThreadPool( renderThreadCount );
        final CompletionService< Void > completionService
                = new ExecutorCompletionService<>( executorService );

        for ( int workerIndex = 0;
              workerIndex < renderThreadCount;
              workerIndex++ ) {
            final int firstColumnIndex = workerIndex;
            completionService.submit( () -> {
                fillImageColumns(
                        firstColumnIndex,
                        renderThreadCount,
                        currentStep );
                return null;
            } );
        }

        try {
            for( int idx = 0; idx < renderThreadCount; idx++){
                if ( isCancelled() ) {
                    return;
                }
                final Future< Void > completedTask = completionService.take();
                if ( completedTask == null ) {
                    continue;
                }
                completedTask.get();
            }
        }
        catch ( final InterruptedException e ) {
            Thread.currentThread().interrupt();
            cancel();
        }
        catch ( final ExecutionException e ) {
            cancel();
            throw new RuntimeException( e.getCause() );
        }
        finally {
            if ( isCancelled() ) {
                executorService.shutdownNow();
            }
            else {
                executorService.shutdown();
            }
        }
    }

    /**
     * Renders all pixels in one image column.
     *
     * @param x           the column index to render.
     * @param currentStep shared progress counter for this render pass.
     */
    private void fillImageColumn( final int x,
                                  final AtomicLong currentStep ) {
        for ( int y = 0; y < height; y ++ ) {
            if ( isCancelled() ) {
                return;
            }

            fillImage( x, y );

            // NOTE: We don't yet have a way to do the bump map in JavaFX as
            //  the relevant features are only in the 3D part of that API.
			/*
			ImageFxUtilities.updateBumpMap(
					greyScaleWriter,
					x,
					y,
					isShading,
					value,
					model.getMinValue(),
					model.getMaxValue() );
			*/

            renderingProgress.currentStep = currentStep.incrementAndGet();
        }
    }

    /**
     * Renders every {@code columnStride} column, starting at
     * {@code firstColumnIndex}.
     *
     * @param firstColumnIndex The first column assigned to the worker.
     * @param columnStride     The spacing between columns assigned to the same
     *                         worker.
     * @param currentStep      Shared progress counter for this render pass.
     */
    private void fillImageColumns( final int firstColumnIndex,
                                   final int columnStride,
                                   final AtomicLong currentStep ) {
        for ( int x = firstColumnIndex;
              x < width;
              x += columnStride ) {
            if ( isCancelled() ) {
                return;
            }

            fillImageColumn( x, currentStep );
        }
    }

    /**
     * Stores a rendered pixel color in the task-local ARGB buffer.
     * <p>
     * Worker threads call this instead of writing directly to
     * {@link PixelWriter}. The buffered image is committed to the
     * {@link WritableImage} once all workers have completed.
     *
     * @param x     the pixel x coordinate.
     * @param y     the pixel y coordinate.
     * @param color the color to store.
     */
    protected final void setPixelColor( final int x,
                                        final int y,
                                        final Color color ) {
        synchronized ( pixelWriter ){
            pixelWriter.setColor(x, y, color);
        }
    }

    /**
     * Sets the color value of a pixel for a {@link WritableImage} through a
     * {@link PixelWriter}.
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     */
    protected abstract void fillImage(
            final int x,
            final int y );
}
