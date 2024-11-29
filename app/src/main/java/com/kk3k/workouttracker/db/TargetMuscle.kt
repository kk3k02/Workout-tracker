package com.kk3k.workouttracker.db

/**
 * Enum class representing different muscle groups targeted by exercises.
 */
enum class TargetMuscle {
    BICEPS,  // Exercises targeting the biceps
    TRICEPS,  // Exercises targeting the triceps
    SHOULDERS,  // Exercises targeting the shoulders
    CHEST,  // Exercises targeting the chest
    BACK,  // Exercises targeting the back
    FOREARMS,  // Exercises targeting the forearms
    ABS,  // Exercises targeting the abdominal muscles (abs)
    LEGS,  // Exercises targeting the legs
    CALVES,  // Exercises targeting the calves
    OTHER;  // Exercises added by the user (custom exercises not tied to a specific muscle group)

    /**
     * Method to return the display name of the muscle group in Polish.
     * @return The display name as a string in Polish.
     */
    fun getDisplayName(): String {
        return when (this) {
            BICEPS -> "BICEPS" // Biceps in Polish remains the same
            TRICEPS -> "TRICEPS" // Triceps in Polish remains the same
            SHOULDERS -> "BARKI" // Shoulders in Polish
            CHEST -> "KLATKA PIERSIOWA" // Chest in Polish
            BACK -> "PLECY" // Back in Polish
            FOREARMS -> "PRZEDRAMIONA" // Forearms in Polish
            ABS -> "BRZUCH" // Abs in Polish
            LEGS -> "NOGI" // Legs in Polish
            CALVES -> "ŁYDKI" // Calves in Polish
            OTHER -> "INNE" // Other in Polish
        }
    }
}
