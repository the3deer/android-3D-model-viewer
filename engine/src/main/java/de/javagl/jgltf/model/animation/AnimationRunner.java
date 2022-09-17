/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model.animation;

import java.util.Objects;

/**
 * Simple utility class to run an {@link AnimationManager} in an own thread
 */
public final class AnimationRunner
{
    /**
     * The {@link AnimationManager}
     */
    private final AnimationManager animationManager;
    
    /**
     * Whether this runner is currently running
     */
    private boolean running = false;
    
    /**
     * The animation thread
     */
    private Thread animationThread;
    
    /**
     * The step size, in milliseconds
     */
    private final long stepSizeMs = 10;
    
    /**
     * Create a new runner for the given {@link AnimationManager}
     * 
     * @param animationManager The {@link AnimationManager}
     */
    public AnimationRunner(AnimationManager animationManager)
    {
        Objects.requireNonNull(animationManager, 
            "The animationManager may not be null");
        this.animationManager = animationManager;
    }
    
    /**
     * Start this runner. If the runner is already {@link #isRunning()},
     * then this has no effect.
     */
    public synchronized void start()
    {
        if (isRunning())
        {
            return;
        }
        animationThread = new Thread(this::runAnimations, "animationThread");
        animationThread.setDaemon(true);
        animationThread.start();
        running = true;
    }
    
    /**
     * Stop this runner. If the runner is not {@link #isRunning()},
     * then this has no effect.
     */
    public synchronized void stop()
    {
        if (!isRunning())
        {
            return;
        }
        running = false;
        animationThread = null;
    }

    /**
     * Returns whether this runner is currently running
     * 
     * @return Whether this runner is currently running 
     */
    boolean isRunning()
    {
        return running;
    }
    
    /**
     * Will be called in an own thread to perform time steps in the
     * {@link AnimationManager}
     */
    private void runAnimations()
    {
        long previousNs = System.nanoTime();
        while (isRunning())
        {
            long currentNs = System.nanoTime();
            long deltaNs = currentNs - previousNs;
            animationManager.performStep(deltaNs);
            previousNs = currentNs;
            try
            {
                Thread.sleep(stepSizeMs);
            } 
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
    
}
