package com.xiaoxun.xun.base.glidelifecycle;

/**
 * An interface for listening to Activity/Fragment lifecycle events.
 * <p>
 * Copy {@link com.bumptech.glide.manager.Lifecycle}
 */
public interface LifeCycleManager {
    /**
     * Adds the given listener to the set of listeners managed by this Lifecycle implementation.
     */
    void addLifeCycleListener(LifeCycleCallBack listener);

    /**
     * Removes the given listener from the set of listeners managed by this Lifecycle implementation,
     * returning {@code true} if the listener was removed successfully, and {@code false} otherwise.
     * <p>
     * <p>This is an optimization only, there is no guarantee that every added listener will
     * eventually be removed.
     */
    void removeListener(LifeCycleCallBack listener);

    void clearListeners();
}
