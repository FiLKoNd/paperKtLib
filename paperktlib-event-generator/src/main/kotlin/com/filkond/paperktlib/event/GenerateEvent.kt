package com.filkond.paperktlib.event

/**
 * Marks a *spec* class from which a Bukkit/Paper Event will be generated.
 *
 * Recommended usage:
 *
 * ```kotlin
 * @GenerateEvent(cancellable = true)
 * class MyEventSpec(
 *   val playerName: String,
 *   val pos1: Location,
 *   val pos2: Location,
 * )
 * ```
 *
 * The processor generates `MyEvent` in the same package.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateEvent(
    /** Whether to generate `org.bukkit.event.Cancellable` implementation. */
    val cancellable: Boolean = false,

    /**
     * Generated class name. If blank, the spec class name is used and the `Spec` suffix (if present) is removed.
     */
    val name: String = "",
)

