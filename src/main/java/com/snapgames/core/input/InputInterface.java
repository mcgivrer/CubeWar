package com.snapgames.core.input;

import java.awt.event.KeyEvent;

/**
 * A new interface to implements specific Input actions  at any levels.
 *
 * <blockquote><b>Note:</b>You can create any implementation of this interface to distribute concerns.</blockquote>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public interface InputInterface {
    /**
     * live key management, called from the game loop.
     *
     * @param ih the {@link InputHandler} instance.
     */
    void input(InputHandler ih);

    /**
     * Process Pressed key event.
     *
     * @param key
     */
    void onKeyPressed(InputHandler ih, KeyEvent key);

    /**
     * Process Released key event.
     *
     * @param key
     */
    void onKeyReleased(InputHandler ih, KeyEvent key);

    /**
     * Processed Typed key event.
     *
     * @param key
     */
    void onKeyTyped(InputHandler ih, KeyEvent key);
}
