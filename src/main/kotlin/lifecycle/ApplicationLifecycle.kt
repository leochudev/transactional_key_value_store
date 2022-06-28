package lifecycle

/**
 * An interface that defines the lifecycle of the application.
 */
interface ApplicationLifecycle {

    /**
     * Called when the application is starting.
     */
    fun onStart()

    /**
     * Called when the application is exiting.
     */
    fun onExit()
}