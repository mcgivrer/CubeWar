/**
 * This package will rpopsoe the core library to create a simple game, based on
 * some provided systems.
 * <p>
 * This {@link Application} class is based on some generic {@link GSystem}
 * namanaged through the {@link GSystemManager}, and implementing some useful
 * services:
 * <ul>
 * <li>{@link SceneManager}</li>
 * <li>{@link PhysicEngine}</li>
 * <li>{@link Renderer}</li>
 * <li>{@link InputHandler}</li>
 * </ul>
 * <p>
 * * This {@link Application} class will manage list of {@link Scene} through
 * the {@link SceneManager}.
 * <p>
 * A {@link Scene} must be implemented by inheriting the {@link AbstractScene}
 * class.
 * <p>
 * Each {@link Scene} contains a bunch of {@link Entity} or {@link TextObject}
 * and a {@link Camera} to display amazing things on the rendering buffer before
 * displaying it on the {@link JFrame} window.
 * <p>
 * It also maintains some basic physic math about moves for the
 * {@link Entity#active}.
 * <p>
 * {@link Entity} can be from 3 physic nature:
 * <ul>
 * <li><code>{@link PhysicType#NONE}</code>, will not be processed by
 * {@link PhysicEngine},</li>
 * <li><code>{@link PhysicType#STATIC}</code>, stick to the display screen,</li>
 * <li><code>{@link PhysicType#DYNAMIC}</code>, move according to the first
 * Newton's law on movement.</li>
 * </ul>
 * <p>
 * A basic Entity could be the {@link GameObject}, designed to be drawn as one
 * of the possible {@link GameObjectType} values:
 * <ul>
 * <li><code>TYPE_POINT</code></li>
 * <li><code>TYPE_LINE</code></li>
 * <li><code>TYPE_RECTANGLE</code></li>
 * <li><code>TYPE_ELLIPSE</code></li>
 * <li><code>TYPE_IMAGE</code></li>
 * </ul>
 * point, line, rectangle, ellipse or image.
 * <p>
 * {@link Renderer} service will manage entities rendering through a plugin
 * mechanism.
 * Each type of {@link Entity} will have its own corresponding
 * {@link RendererPlugin}
 * implementation. Specific operations can be enhanced according to draw
 * required operation.
 * 
 * @authhor Frédéric Delorme
 * @since 1.0.0
 */

package com.snapgames.core;