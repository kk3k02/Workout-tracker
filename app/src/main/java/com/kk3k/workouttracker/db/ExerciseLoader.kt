package com.kk3k.workouttracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.db.TargetMuscle
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ExerciseLoader {

    @Volatile
    private var isInitialized = false

    // Singleton function to load exercises if not already loaded
    fun loadExercisesIfNeeded(context: Context, exerciseViewModel: ExerciseViewModel) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
                        // Directly use the function from ViewModel to check the exercise count
                        exerciseViewModel.getExerciseCount { count ->
                            if (count == 0) {
                                addExercises(exerciseViewModel, context)
                            }
                        }
                        isInitialized = true
                    }
                }
            }
        }
    }

    // Function to add basic exercises if none exist in the database
    private fun addExercises(viewModel: ExerciseViewModel, context: Context) {
        val exercises = listOf(
            // BICEPS
            Exercise(
                name = "Uginanie ramion ze sztangą",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Uginanie ramion ze sztangą to jedno z najbardziej podstawowych i skutecznych ćwiczeń na rozwój bicepsa. Stajemy w pozycji wyprostowanej, chwytamy sztangę podchwytem i unosimy ją do momentu, aż przedramiona znajdą się w pozycji pionowej."
            ),
            Exercise(
                name = "Uginanie ramion z hantlami",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Uginanie ramion z hantlami pozwala na bardziej naturalny zakres ruchu. Ćwiczenie angażuje oba bicepsy jednocześnie, poprawiając równomierność siły i masy mięśniowej."
            ),
            Exercise(
                name = "Uginanie ramion na modlitewniku",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Uginanie ramion na modlitewniku doskonale izoluje bicepsy, zapewniając maksymalną efektywność ruchu i minimalizując oszukiwanie podczas wykonywania ćwiczenia."
            ),
            Exercise(
                name = "Uginanie ramion z linką wyciągu",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Uginanie ramion z linką wyciągu pozwala na utrzymanie stałego napięcia w bicepsach przez cały zakres ruchu, co pomaga w ich efektywnym wzmacnianiu."
            ),
            Exercise(
                name = "Skoncentrowane uginanie ramion",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Skoncentrowane uginanie ramion to doskonałe ćwiczenie na rozwój szczytu bicepsa. Wykonujemy je siedząc, opierając łokieć o wewnętrzną część uda i unosząc hantel do klatki piersiowej."
            ),

            // TRICEPS
            Exercise(
                name = "Wyciskanie francuskie sztangi leżąc",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Wyciskanie francuskie to jedno z najlepszych ćwiczeń izolowanych na tricepsy. Leżąc na ławce, opuszczamy sztangę do czoła, a następnie prostujemy ręce, angażując tricepsy."
            ),
            Exercise(
                name = "Prostowanie ramion na wyciągu",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Prostowanie ramion na wyciągu to klasyczne ćwiczenie na tricepsy, które zapewnia pełny zakres ruchu i stałe napięcie mięśniowe. Trzymamy uchwyt wyciągu, prostujemy ramiona i kontrolujemy powrót."
            ),
            Exercise(
                name = "Pompki na triceps w podporze tyłem",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Pompki w podporze tyłem to ćwiczenie wykorzystujące ciężar ciała do intensywnego zaangażowania tricepsów. Opieramy dłonie na ławce za sobą i zginamy ramiona, opuszczając ciało, a następnie prostujemy je."
            ),
            Exercise(
                name = "Prostowanie ramion z hantlem jednorącz",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Jednoręczne prostowanie ramion z hantlem to świetne ćwiczenie na izolację tricepsa. Wykonujemy je w pozycji stojącej lub siedzącej, unosząc hantel nad głowę, a następnie prostując ramię."
            ),
            Exercise(
                name = "Wyciskanie sztangielki oburącz nad głową",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Wyciskanie sztangielki oburącz nad głową doskonale angażuje długą głowę tricepsa. Wykonujemy je, trzymając hantel obiema rękami i prostując ramiona nad głową."
            ),

            // SHOULDERS
            Exercise(
                name = "Wyciskanie sztangi sprzed głowy",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie to angażuje przednie i boczne aktony mięśni naramiennych. Wyciskanie sztangi sprzed głowy można wykonywać zarówno w pozycji siedzącej, jak i stojącej."
            ),
            Exercise(
                name = "Wyciskanie sztangi zza głowy",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Angażuje głównie przednie i boczne aktony mięśni naramiennych. Wyciskanie zza głowy wymaga dużej kontroli, aby uniknąć kontuzji."
            ),
            Exercise(
                name = "Wyciskanie hantli nad głowę",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Wyciskanie hantli nad głowę pozwala na lepszą izolację mięśni naramiennych. Ćwiczenie to można wykonywać w pozycji siedzącej, aby zwiększyć stabilizację."
            ),
            Exercise(
                name = "Arnoldki",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wymyślone przez Arnolda Schwarzeneggera. Ruch wyciskania połączony z rotacją dłoni angażuje wszystkie aktony mięśni naramiennych."
            ),
            Exercise(
                name = "Unoszenie sztangielek bokiem w górę",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie izolujące boczne aktony mięśni naramiennych. Wykonujemy je w pozycji stojącej lub siedzącej, unosząc ramiona bokiem."
            ),

            // CHEST
            Exercise(
                name = "Wyciskanie sztangi na ławce poziomej",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Podstawowe ćwiczenie na rozwój mięśni klatki piersiowej. Wyciskanie sztangi na ławce poziomej angażuje zarówno górne, jak i dolne partie klatki piersiowej."
            ),
            Exercise(
                name = "Rozpiętki ze sztangielkami",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Rozpiętki na ławce to doskonałe ćwiczenie izolujące mięśnie klatki piersiowej. Wykonujemy je, opuszczając ramiona szeroko na boki, a następnie łącząc je nad klatką."
            ),
            Exercise(
                name = "Wyciskanie sztangi na ławce skośnej",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ćwiczenie to angażuje głównie górną część mięśni klatki piersiowej. Wykonywane na ławce skośnej w pozycji leżącej."
            ),
            Exercise(
                name = "Wyciskanie hantli na ławce poziomej",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Podobne do wyciskania sztangi, jednak z większym zakresem ruchu. Używamy hantli do pracy nad równomiernym rozwojem mięśni."
            ),
            Exercise(
                name = "Pompki klasyczne",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Pompki klasyczne to podstawowe ćwiczenie na klatkę piersiową i tricepsy. Wykonywane na podłodze z ciężarem ciała."
            ),

            // BACK
            Exercise(
                name = "Martwy ciąg",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Jedno z najważniejszych ćwiczeń wielostawowych, które angażuje mięśnie pleców, pośladków i nóg. Wymaga odpowiedniej techniki, aby uniknąć kontuzji."
            ),
            Exercise(
                name = "Wiosłowanie sztangą w opadzie",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Ćwiczenie na rozwój mięśni grzbietu. Wykonujemy je w opadzie tułowia, wiosłując sztangą do brzucha."
            ),
            Exercise(
                name = "Podciąganie na drążku",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Ćwiczenie angażujące mięśnie najszersze grzbietu, bicepsy oraz stabilizatory tułowia. Można wykonywać w szerokim lub wąskim uchwycie."
            ),
            Exercise(
                name = "Ściąganie linki wyciągu górnego",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Alternatywa dla podciągania. Angażuje te same mięśnie, ale pozwala na lepszą kontrolę obciążenia."
            ),
            Exercise(
                name = "Wiosłowanie hantlem jednorącz",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Jednoręczne wiosłowanie hantlem angażuje mięśnie grzbietu oraz stabilizatory tułowia. Ćwiczenie to pozwala na lepszą izolację mięśni."
            ),

            // ABS
            Exercise(
                name = "Plank (deska)",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Plank to doskonałe ćwiczenie izometryczne na mięśnie brzucha oraz stabilizację całego ciała. Trzymamy ciało w linii prostej, opierając się na przedramionach i palcach stóp."
            ),
            Exercise(
                name = "Brzuszki klasyczne",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Brzuszki klasyczne to podstawowe ćwiczenie na mięśnie brzucha. Leżymy na plecach, zginamy kolana i unosimy tułów w kierunku kolan."
            ),
            Exercise(
                name = "Unoszenie nóg w leżeniu",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Unoszenie nóg to doskonałe ćwiczenie na dolne partie mięśni brzucha. Leżymy na plecach, unosimy proste nogi, trzymając dolną część pleców przy podłodze."
            ),
            Exercise(
                name = "Brzuszki skośne",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Brzuszki skośne angażują mięśnie skośne brzucha. Wykonujemy je, unosząc tułów do jednego kolana, następnie do drugiego."
            ),
            Exercise(
                name = "Przyciąganie kolan do klatki w zwisie",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Ćwiczenie to angażuje dolne partie mięśni brzucha. Wykonujemy je, przyciągając kolana do klatki piersiowej, wisząc na drążku."
            ),

            // LEGS
            Exercise(
                name = "Przysiady ze sztangą",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Przysiady ze sztangą to jedno z najważniejszych ćwiczeń na nogi, angażujące mięśnie czworogłowe, pośladki oraz mięśnie dolnej części pleców."
            ),
            Exercise(
                name = "Wykroki",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Wykroki to doskonałe ćwiczenie na mięśnie nóg i pośladków. Wykonujemy je, stawiając jedną nogę przed sobą i schodząc w dół do kąta prostego."
            ),
            Exercise(
                name = "Wspięcia na palce",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Wspięcia na palce to klasyczne ćwiczenie na rozwój mięśni łydek. Wykonujemy je w pozycji stojącej, unosząc pięty do góry."
            ),
            Exercise(
                name = "Prostowanie nóg na maszynie",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Prostowanie nóg na maszynie izoluje mięśnie czworogłowe uda. Wykonujemy je w pozycji siedzącej, prostując nogi przed sobą."
            ),
            Exercise(
                name = "Uginanie nóg na maszynie",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Uginanie nóg na maszynie to ćwiczenie izolujące mięśnie dwugłowe uda. Wykonujemy je w pozycji leżącej, uginając nogi w kolanach."
            )
        )

        exercises.forEach { exercise ->
            viewModel.insertExercise(exercise)
        }
    }

    // Helper function to load GIFs as ByteArray
    private fun getGifAsByteArray(context: Context, resourceId: Int): ByteArray? {
        return try {
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            val byteStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                byteStream.write(buffer, 0, length)
            }
            byteStream.toByteArray()  // Returning GIF as ByteArray
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
