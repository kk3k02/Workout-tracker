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
                name = "UGINANIE RAMION ZE SZTANGĄ STOJAC PODCHWYTEM",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Stajemy w rozkroku(na szerokość barków lub nieco szerzej)-sztangę chwytamy w zależności od tego, którą głowę bicepsu chcemy zaangażować bardziej. I tak odpowiednio:\n" +
                        "\n" +
                        "-uchwyt wąski(węższy niż szerokość ramion)-większe zaangażowanie głów krótkich,\n" +
                        "-uchwyt średni(na szerokość ramion)-obie głowy zaangażowane w równym stopniu,\n" +
                        "-uchwyt szeroki(szerszy od ramion)-większe zaangażowanie głów długich.\n" +
                        "\n" +
                        "Tułów podczas ćwiczenia utrzymujemy w pozycji wyprostowanej(bez bujania nim). Zakres ruchu: od pełnego rozgięcia bicepsów(nie ramion)do pełnego ich skurczu. Pełne rozciągnięcie bicepsów, to nie to samo, co pełny wyprost ramion. Należy unikać(nie tylko w tym ćwiczeniu) tzw. ”przeprostów” ramion, czyli nadmiernego ich wyprostowywania(do pełnego zakresu ruchu w stawie łokciowym).Łokcie przez cały czas przylegają do tułowia-nie powinny uciekać na boki, ani w przód, gdyż powoduje to zaangażowanie innych mięśni do pracy. Powietrza nabieramy w pozycji wyjściowej, wypuszczamy je dopiero po przejściu ciężaru przez najtrudniejszy punkt ruchu. W pozycji końcowej można zatrzymać na chwilę ciężar dla lepszego ukrwienia mięśnia, ale pod warunkiem utrzymania bicepsów w pełnym napięciu. Należy pamiętać, że ruch opuszczania musi być w pełni kontrolowany i wolniejszy od unoszenia. Do ćwiczenia można używać zarówno sztangi prostej, jak i łamanej-gryf łamany zmniejsza napięcia powstające w nadgarstkach.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Stabilna pozycja- powinno się unikać bujania tułowiem, oraz szarpanych ruchów-szczególnie podczas opuszczania sztangi-przeciąża to mocno stawy łokciowe. Łokcie nie powinny uciekać do tyłu, ani też nie powinny unosić się. Dopuszcza się wykonywanie na końcu serii ruchów oszukanych, ale nie poleca się tego początkującym ze względu na możliwość nabrania złych nawyków technicznych. Ciężar dobieramy tak, by nie „przeszkadzał” nam w poprawnym wykonaniu ćwiczenia-wielu kulturystów(szczególnie tych z mniejszym stażem)próbuje zbyt wielkich ciężarów, z czego często wynikają groźne kontuzje-tutaj apel właśnie do nich o rozwagę.",
                image = getGifAsByteArray(context, R.raw.biceps_1)
            ),
            Exercise(
                name = "UGINANIE RAMION ZE SZTANGIELKAMI STOJĄC PODCHWYTEM",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Ćwiczenie to można wykonywać zarówno w pozycji stojącej, jak i siedzącej. Bardzo dobre, jako rozgrzewkowe przed ciężkimi seriami ze sztangą, ale również jako samodzielne ćwiczenie rozwijające masę i kształt bicepsów. Polecane wykonanie z „supinacją” nadgarstka. Polega ona na stopniowym obracaniu dłoni w trakcie wykonywania ćwiczenia. W pozycji wyjściowej( ramiona wyprostowane) dłonie zwrócone są ku sobie palcami, a w miarę uginania ramion obracają się tak, by w końcowym momencie ruchu(przy zgiętych ramionach)małe palce były wyżej od kciuków. Daje to dodatkowe napięcie mięśni oraz kształtuje kulistość bicepsów. Prostowanie ramienia kończymy w momencie rozciągnięcia mięśni dwugłowych(nie do pełnej możliwości stawu łokciowego). Ruch można wykonywać na przemian-raz jedna ręka, raz druga(po 1 powtórzeniu), obiema rękami jednocześnie, lub opuszczając jedną rękę- jednocześnie unosząc drugą. Samemu trzeba wybrać, która wersja jest dla nas najefektywniejsza. Ułożenie łokci jak w ćwiczeniu 1-ze sztangą. Można również pominąć supinację nadgarstka, ale zmniejsza to efektywność ćwiczenia.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Zanim dobrze opanujemy poprawne wykonanie ćwiczenia z supinacją, używajmy do tego ćwiczenia umiarkowanego ciężaru. Przy zbyt dużym łokcie mają tendencje do uciekania na boki, a powinny być przy tułowiu przez cały czas trwania ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.biceps_2)
            ),
            Exercise(
                name = "UGINANIE RAMION ZE SZTANGIELKAMI STOJĄC",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Ćwiczenie z pozoru podobne do poprzedniego, jednak z zasadniczą różnicą-ułożenie nadgarstków jest neutralne i takie pozostaje przez całe ćwiczenie. Palce dłoni skierowane są do wewnątrz(kciukiem do góry).Ważne jest również utrzymanie stałej pozycji łokci przez cały czas trwania ćwiczenia. Ruch powrotny powinien być wolny i kontrolowany. Ruch można wykonywać na przemian-raz jedna ręka, raz druga(po 1 powtórzeniu), obiema rękami jednocześnie, lub opuszczając jedną rękę- jednocześnie unosząc drugą. Samemu trzeba wybrać, która wersja jest dla nas najefektywniejsza. Jednak w tym ćwiczeniu najlepsza, pod względem koncentracji wydaje się być wersja pojedynczych ruchów każdej ręki na przemian.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Nadgarstki zblokowane przez cały czas trwania ćwiczenia. Unikamy bujania tułowiem i ruchów szarpanych. Pamiętajmy o właściwym ułożeniu dłoni-ma ono zasadnicze znaczenie w tym ćwiczeniu.",
                image = getGifAsByteArray(context, R.raw.biceps_3)
            ),
            Exercise(
                name = "UGINANIE RAMION ZE SZTANGĄ NA „MODLITEWNIKU”",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Ćwiczenie zarówno na rozwój masy, jak i „wypiętrzenie” bicepsów, a to za sprawą mięśnia ramiennego, położonego pod dwugłowym, który wypycha go do góry. Siadamy na siodełku modlitewnika. Nogi rozstawiamy w taki sposób, by pozwoliły nam utrzymać stabilną pozycję. Górna krawędź modlitewnika powinna znaleźć się pod naszymi pachami. Ramiona rozstawione na szerokość barków-równolegle do siebie. Rozstaw dłoni, podobnie jak w ćwiczeniu ze sztanga stojąc-w zależności od celu ćwiczenia(zaangażowanie poszczególnych głów, jak w ćwiczeniu ze sztangą stojąc). Zakres ruchu: od pełnego rozgięcia bicepsów(nie ramion)do pełnego ich skurczu, przy czym przedramiona nie powinny przekraczać linii pionu. Pełne rozciągnięcie bicepsów, to nie to samo, co pełny wyprost ramion. Należy unikać(nie tylko w tym ćwiczeniu) tzw. ”przeprostów” ramion, czyli nadmiernego ich wyprostowywania(do pełnego zakresu ruchu w stawie łokciowym).Faza negatywna ruchu-prostowanie ramion powinna odbywać się przy pełnej kontroli ciężaru. Opuszczamy sztangę wolniej niż unosimy. Oddychanie jak w ćwiczeniu ze sztangą stojąc. Do ćwiczenia można używać zarówno sztangi prostej, jak i łamanej-gryf łamany zmniejsza napięcia powstające w nadgarstkach. Ćwiczenie to można wykonywać również zastępując sztangę drążkiem wyciągu dolnego(gif 4a.), lub na specjalnych maszynach(gif 4b.).\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie wymaga pełnej koncentracji. Dopuszcza się wykonywanie na końcu serii ruchów oszukanych, ale nie poleca się tego początkującym ze względu na możliwość nabrania złych nawyków technicznych. Ciężar dobieramy tak, by nie przeszkadzał nam w poprawnym wykonaniu ćwiczenia-wielu kulturystów(szczególnie tych z mniejszym stażem)próbuje zbyt wielkich ciężarów, z czego często wynikają groźne kontuzje-tutaj apel właśnie do nich o rozwagę. Opuszczamy sztangę powoli-nie „szarpiemy” ruchu.",
                image = getGifAsByteArray(context, R.raw.biceps_4)
            ),
            Exercise(
                name = "UGINANIE RAMIENIA ZE SZTANGIELKĄ NA „MODLITEWNIKU”",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Ćwiczenie zarówno na rozwój masy, jak i „wypiętrzenie” bicepsów, dzięki pracy mięśnia ramiennego, położonego pod mięśniem dwugłowym, który wypycha go do góry. Siadamy na siodełku modlitewnika. Nogi rozstawiamy w taki sposób, by zapewnić stabilną pozycję. Górna krawędź modlitewnika powinna znaleźć się pod naszymi pachami. Ramiona rozstawione na szerokość barków, równolegle do siebie. W tym ćwiczeniu sztangę zastępujemy sztangielkami, a można je unosić jednocześnie lub osobno. Ruch jest podobny do wersji ze sztangą - od pełnego rozciągnięcia bicepsów do pełnego ich skurczu, jednak należy zwrócić szczególną uwagę, aby sztangielki poruszały się w linii prostopadłej do podłoża i nie uciekały na boki. Zakres ruchu: od pełnego rozciągnięcia bicepsów (nie ramion) do pełnego ich skurczu. Przedramiona nie powinny przekraczać linii pionu. Pełne rozciągnięcie bicepsów nie oznacza pełnego wyprostu ramion. Należy unikać tzw. „przeprostów”, czyli nadmiernego wyprostowywania ramion. Faza negatywna ruchu (prostowanie ramion) powinna odbywać się przy pełnej kontroli ciężaru. Opuszczamy sztangielki wolniej niż je unosimy. Oddychanie jak w przypadku ćwiczenia ze sztangą.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie wymaga pełnej koncentracji. Dopuszcza się wykonywanie na końcu serii ruchów oszukanych, ale nie zaleca się tego początkującym, ze względu na możliwość nabrania złych nawyków technicznych. Ciężar dobieramy tak, by nie przeszkadzał nam w poprawnym wykonaniu ćwiczenia – wielu początkujących kulturystów próbuje zbyt dużych ciężarów, co może prowadzić do kontuzji. Sztangielki opuszczamy powoli, nie szarpiemy ruchu. Należy też szczególnie uważać, by sztangielka poruszała się po torze prostopadłym do podłoża i nie uciekała na boki.",
                image = getGifAsByteArray(context, R.raw.biceps_5)
            ),

            Exercise(
                name = "UGINANIE RAMION ZE SZTANGIELKAMI W SIADZIE NA ŁAWCE SKOŚNEJ",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Jest to jedno z tych ćwiczeń, które rozwijają zarówno masę mięśni dwugłowych, jak i charakterystyczny zaokrąglony kształt. Trzeba je wykonywać z dużą koncentracją. Siadamy na ławce skośnej, o nachyleniu ok.45 stopni. Przedramiona powinny być lekko odchylone od tułowia, a łokcie przylegać do niego. Wymodelowaniu kształtu mięśni służy „supinacja” nadgarstka. Polega ona na stopniowym obracaniu dłoni w trakcie wykonywania ćwiczenia. W pozycji wyjściowej( ramiona wyprostowane) dłonie zwrócone są ku sobie palcami, a w miarę uginania ramion obracają się tak, by w końcowym momencie ruchu(przy zgiętych ramionach)małe palce były wyżej od kciuków. Wskazane dla lepszego rozwoju bicepsów jest zatrzymanie ruchu w końcowym położeniu i maksymalne napięcie mięśni przez 1-3 sekundy. Ruch można wykonywać na przemian-raz jedna ręka, raz druga(po 1 powtórzeniu), obiema rękami jednocześnie, lub opuszczając jedną rękę jednocześnie unosząc drugą. Samemu trzeba wybrać, która wersja jest dla nas najefektywniejsza.\n" +
                        "\n" +
                        "UWAGI: Tułów powinien przez cały czas przylegać do oparcia. Ruch opuszczania wolniejszy od unoszenia lub w jednakowym umiarkowanym tempie. Unikamy ruchów szarpanych.",
                image = getGifAsByteArray(context, R.raw.biceps_6)
            ),

            Exercise(
                name = "UGINANIE RAMIENIA ZE SZTANGIELKĄ W SIADZIE-W PODPORZE O KOLANO",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Jest to ćwiczenie modelujące kształt bicepsów-ich wierzchołek. Wymaga ono dużej koncentracji w czasie wykonywania. Siadamy na ławce lub krześle, pochylamy się lekko do przodu. Chwytamy sztangielkę w dłoń i opieramy łokieć o wewnętrzną część uda. Ruch powinien mieć wolne tempo(zarówno podczas unoszenia i opuszczania)-jest to ćwiczenie koncentryczne i technika jest w nim ważniejsza od wielkości ciężaru. Można również w tym ćwiczeniu stosować supinację nadgarstka. Ramiona „zamykamy” do końca, napinając maksymalnie mięsień. Prostujemy ramię(jak w innych ćwiczeniach na mięśnie dwugłowe)tylko do momentu pełnego rozciągnięcia bicepsów, nie do pełnego zakresu ruchu w stawie łokciowym.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Ważne jest, by dbać o poprawność wykonania w tym ćwiczeniu-jest ona podstawą uzyskania efektów w kształtowaniu mięśni dwugłowych ramion(szczególnie wierzchołka). Unikamy szarpania ciężarem-wszelkich gwałtownych ruchów( „zarzucania” sztangielki)",
                image = getGifAsByteArray(context, R.raw.biceps_7)
            ),

            Exercise(
                name = "UGINANIE RAMION PODCHWYTEM STOJĄC-Z RĄCZKĄ WYCIĄGU",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Stajemy w rozkroku (na szerokość barków lub nieco szerzej) i chwytamy drążek wyciągu.\n" +
                        "\n" +
                        "Tułów podczas ćwiczenia utrzymujemy w pozycji wyprostowanej, unikając bujania. Zakres ruchu obejmuje pełne rozciągnięcie bicepsów do ich pełnego skurczu, jednak należy pamiętać, że pełne rozciągnięcie bicepsów nie oznacza pełnego wyprostu ramion. Należy unikać tzw. \"przeprostów\" w stawie łokciowym. Łokcie powinny przez cały czas przylegać do tułowia – nie mogą uciekać na boki ani w przód, aby nie angażować innych mięśni. Oddychamy nabierając powietrze w pozycji wyjściowej, a wypuszczamy je po przejściu ciężaru przez najtrudniejszy punkt ruchu. W pozycji końcowej można na chwilę zatrzymać ciężar, aby uzyskać lepsze ukrwienie mięśnia, pod warunkiem utrzymania pełnego napięcia w bicepsach. Ruch opuszczania ciężaru powinien być w pełni kontrolowany i wolniejszy od unoszenia.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Drążek powinien opadać powoli i nie uderzać o wyciąg – pełna kontrola ruchu podczas opuszczania jest kluczowa. Dostosujmy długość linki wyciągu do naszego wzrostu – jeśli to nie jest możliwe, można postawić coś pod nogi. Zachowaj pełny zakres ruchu, jak w innych ćwiczeniach na bicepsy.",
                image = getGifAsByteArray(context, R.raw.biceps_8)
            ),

            Exercise(
                name = "UGINANIE RAMION ZE SZTANGA NACHWYTEM STOJĄC",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Stajemy w rozkroku na szerokość barków(lub nieco szerszym) sztangę chwytamy nachwytem. Łokcie nieruchomo przy tułowiu, nadgarstki zblokowane w jednej pozycji przez cały czas ćwiczenia. Ruch odbywa się tylko w stawach łokciowych. Opuszczanie odbywa się wolnym tempem i pod pełną kontrolą ciężaru. Oddychanie, jak w ćwiczeniu ze sztangą podchwytem. Ćwiczenie to można wykonywać również zastępując sztangę drążkiem wyciągu dolnego.\n" +
                        "\n" +
                        "UWAGI: Pomimo, że wykonując to ćwiczenie ze sztangą o łamanym gryfie możemy użyć większego ciężaru, to zalecane jednak jest ćwiczenie sztanga o prostym gryfie-daje lepsze efekty.",
                image = getGifAsByteArray(context, R.raw.biceps_9)
            ),

            Exercise(
                name = "UGINANIE RAMION ZE SZTANGA NACHWYTEM NA „MODLITEWNIKU”",
                targetMuscle = TargetMuscle.BICEPS.name,
                description = "Ćwiczenie to wykonujemy w pozycji, jak przy wersji podchwytem, jednak sztangę chwytamy nadchwytem. Technika wykonania ćwiczenia różni się niewiele od tradycyjnego podchwytu, jednak zmiana uchwytu skutkuje większym zaangażowaniem mięśni ramiennych oraz przedramion, a mniejsze obciążenie przypada na mięśnie dwugłowe ramion. Niemniej jednak, ćwiczenie nadal wpływa na część przyłokciową bicepsów, co czyni je dobrym uzupełnieniem treningu na górną część ramion. Należy utrzymywać stabilną pozycję na siodełku modlitewnika, a ramiona rozstawione równolegle do siebie, z rozstawem dłoni podobnym, jak w wersji podchwytem.\n" +
                        "\n" +
                        "Zakres ruchu: od pełnego rozciągnięcia ramiennych, aż do pełnego ich skurczu. Przedramiona nie powinny przekraczać linii pionu, aby uniknąć nadmiernego zaangażowania innych mięśni. Opuszczamy sztangę wolniej, niż ją unosimy, kontrolując fazę negatywną ruchu. Oddychanie jak w klasycznym ćwiczeniu na bicepsy — wdech przy opuszczaniu, wydech przy unoszeniu.\n" +
                        "\n" +
                        "UWAGI : Ciężar dobieramy w taki sposób, aby nie zakłócał poprawnej techniki wykonania ćwiczenia. Wielu początkujących często wybiera zbyt duże ciężary, co może prowadzić do kontuzji — należy zachować ostrożność. Podczas wykonywania ćwiczenia należy unikać \"szarpania\" ciężaru oraz dbać o pełną kontrolę podczas jego opuszczania.",
                image = getGifAsByteArray(context, R.raw.biceps_10)
            ),

            // FOREARMS
            Exercise(
                name = "UGINANIE NADGARSTKÓW PODCHWYTEM W SIADZIE",
                targetMuscle = TargetMuscle.FOREARMS.name,
                description = "Ćwiczenie to rozwija wewnętrzną część przedramion-podstawowe dla rozwoju tej partii mięśni. Wyrabia siłę uścisku dłoni. Łapiemy sztangę w siadzie podchwytem, rozstaw dłoni ok. 15 cm(szerszy nadwyręża nadgarstki).Opieramy przedramiona o uda, tak by poza nogi wystawały jedynie nasze dłonie. Pracują tylko nadgarstki. Staramy się, aby zakres ruchu był jak największy i w tym celu pozwalamy w dolnym położeniu na stoczenie się sztangi aż do końców palców - po czym ponownie ściskamy dłoń i zginamy maksymalnie nadgarstek. Ćwiczenie można wykonywać również zastępując sztangę drążkiem wyciągu dolnego, lub sztangielką (jednorącz).\n" +
                        "\n" +
                        "UWAGI: Ilość powtórzeń w tym ćwiczeniu jest na ogół większa niż w innych ćwiczeniach(np. na mięśnie dwugłowe)-ćwiczymy do momentu, kiedy poczujemy palenie w przedramionach, ale nie przerywamy wtedy, lecz staramy się wykonać jeszcze kilka powtórzeń. Ciężar należy dobierać wyjątkowo ostrożnie pamiętając, że stawy nadgarstków są wyjątkowo delikatne, a leczenie ich kontuzji trwa wiele miesięcy(najczęściej bez możliwości treningu z ciężarami). Dlatego dobieramy mniejszy ciężar i wykonujemy większą ilość powtórzeń.",
                image = getGifAsByteArray(context, R.raw.forearms_1)
            ),

            Exercise(
                name = "UGINANIE NADGARSTKÓW NACHWYTEM W SIADZIE",
                targetMuscle = TargetMuscle.FOREARMS.name,
                description = "\n" +
                        "Ćwiczenie to rozwija wewnętrzną część przedramion i jest podstawowym ćwiczeniem dla rozwoju tej partii mięśni. Dodatkowo wyrabia siłę uścisku dłoni. W siadzie, chwytamy sztangę podchwytem, a rozstaw dłoni wynosi około 15 cm (szerszy chwyt może nadwyrężyć nadgarstki). Opieramy przedramiona o uda w taki sposób, aby poza nogi wystawały tylko nasze dłonie. W ćwiczeniu pracują jedynie nadgarstki. W dolnym położeniu pozwalamy sztandze stoczyć się aż do końcówek palców, po czym ściskamy mocno dłoń i maksymalnie zginamy nadgarstek, unosząc sztangę tak wysoko, jak to możliwe. Ćwiczenie można również wykonywać, zastępując sztangę drążkiem wyciągu dolnego lub sztangielką, trenując jednorącz.\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Ilość powtórzeń w tym ćwiczeniu zazwyczaj jest większa niż w innych, na przykład na mięśnie dwugłowe. Ćwiczymy aż do momentu, gdy poczujemy intensywne palenie w przedramionach, a następnie staramy się wykonać jeszcze kilka dodatkowych powtórzeń. Bardzo ważne jest, aby dobierać ciężar ostrożnie, mając na uwadze, że nadgarstki są delikatnymi stawami, a ich kontuzje mogą trwać wiele miesięcy, często wykluczając możliwość dalszego treningu z ciężarami. Z tego powodu lepiej używać mniejszego ciężaru i wykonywać większą liczbę powtórzeń, aby minimalizować ryzyko kontuzji.",
                image = getGifAsByteArray(context, R.raw.forearms_2)
            ),

            // TRICEPS
            Exercise(
                name = "PROSTOWANIE RAMION NA WYCIĄGU STOJĄC",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Stajemy w lekkim rozkroku przed wyciągiem, chwytamy rączkę nachwytem na szerokość ok.10-20 cm. Kciuki na rączce(tzw. ”małpi” chwyt).Lekko pochylamy się w przód i naciskamy rączkę wyciągu w dół. Ramiona przyciśnięte do tułowia. Ruch wykonują tylko przedramiona(pracują jedynie stawy łokciowe). Ramiona prostujemy do końca-dla lepszego napięcia mięśni. Nie unosimy łokci, gdy rączka wyciągu jest w górnym położeniu-spowodowałoby to zanik napięcia w tricepsach. Najważniejsze jest stałe utrzymanie napięcia w ćwiczonych mięśniach. Unikamy szarpania-ruch powinien być płynny, a faza opuszczania ciężaru wolniejsza, z pełną kontrolą ciężaru.\n" +
                        "\n" +
                        "UWAGI: Bardzo dobre ćwiczenie na rozbudowę masy wszystkich głów tricepsów -pozwala na użycie dużego ciężaru bez obaw(w każdej chwili możemy przerwać ćwiczenie-bez pomocy),ale nie wolno przy doborze obciążenia zapominać o prawidłowej technice, która jest podstawą uzyskania wyników w ćwiczeniu. Najczęstszym błędem jest wypychanie ciężaru całym ciałem, a nie tylko tricepsami. Ćwiczenie można wykonywać z prostą rączką i lekko ugięta-ugięta oszczędza nadgarstki(zmniejsza naprężenia).",
                image = getGifAsByteArray(context, R.raw.triceps_1)
            ),

            Exercise(
                name = "WYCISKANIE „FRANCUSKIE”SZTANGI W SIADZIE",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Ćwiczenie to rozwija wszystkie głowy tricepsów ze szczególnym wskazaniem na głowę długa. Można je wykonywać zarówno w pozycji stojącej, jak i siedzącej. W pozycji siedzącej ponadto można zastosować oparcie(np. na ławce skośnej).Polepsza to stabilność, a co za tym idzie pozwala lepiej skoncentrować się na wykonaniu ćwiczenia. Możemy użyć sztangi prostej i łamanej. Łamana ma tę zaletę, że pozwala na zwiększenie zakresu ruchu i zmniejsza naprężenia powstające w nadgarstkach. Łokcie powinny trwać nieruchomo(jak najbliżej głowy) podczas ćwiczenia. Zakres ruchu:od pełnego wyprostu ramion, do pełnego zgięcia w łokciach. Przy pełnym zgięciu ramion, gdy sztanga znajduje się za głową, nie powinno się tracić nad nią kontroli(swobodne opuszczenie jej przeciąża mocno stawy łokciowe, co może prowadzić do kontuzji).Ćwiczenie można wykonywać również zastępując sztangę drążkiem wyciągu dolnego (prostym lub łamanym, bądź grubym sznurem zakończonym węzłami-stosujemy wtedy uchwyt równoległy). Można również wykonywać nieco inną odmianę ćwiczenia ze sztangielką trzymaną oburącz. Przy tej wersji należy pamiętać, o jednakowym angażowaniu w ćwiczenie obu rąk.\n" +
                        "\n" +
                        "UWAGI: Nie powinno się przesadzać z ciężarem w tym ćwiczeniu-łatwo w nim o kontuzję. Jeśli będziemy wykonywali je poprawnie technicznie, przy pełnym i stałym napięciu mięśni trójgłowych, to nawet przy użyciu mniejszego obciążenia uzyskane efekty będą nas satysfakcjonowały.",
                image = getGifAsByteArray(context, R.raw.triceps_2)
            ),

            Exercise(
                name = "WYCISKANIE “FRANCUSKIE” JEDNORĄCZ SZTANGIELKI W SIADZIE",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Ćwiczenie to rozwija wszystkie głowy tricepsów, z naciskiem na głowę długą. Jest to inna wersja poprzedniego ćwiczenia, które pozwala na lepszą koncentrację na jednym mięśniu naraz – lewym lub prawym tricepsie. Ćwiczenie można wykonywać zarówno w pozycji stojącej, jak i siedzącej, przy czym siedząca pozycja pozwala na lepszą stabilizację i wyeliminowanie dodatkowych ruchów ciała, co ułatwia koncentrację na mięśniach. \n" +
                        "\n" +
                        "Technika ćwiczenia jest podobna do wersji z użyciem sztangi – ruch sztangielki jest w pełni kontrolowany. Opuszczanie ciężaru (faza negatywna) powinno być wolniejsze niż unoszenie. Ruchy są wykonywane od pełnego wyprostu ramienia do pełnego zgięcia w łokciu, przy czym łokieć pozostaje nieruchomy i blisko głowy. Ćwiczenie można także wykonywać z użyciem rączki wyciągu dolnego zamiast hantli.\n" +
                        "\n" +
                        "UWAGI : Podobnie jak w wersji ze sztangą, należy zachować pełną kontrolę nad ciężarem, zwłaszcza podczas fazy negatywnej ruchu. Dodatkowo warto pamiętać o dokręceniu zacisków na hantli przed każdą serią. Wydaje się to oczywiste, ale w praktyce często jest to przeoczane, co może prowadzić do nieprzyjemnych urazów, np. uderzeń w głowę.",
                image = getGifAsByteArray(context, R.raw.triceps_3)
            ),

            Exercise(
                name = "WYCISKANIE „FRANCUSKIE” SZTANGI W LEŻENIU",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Kładziemy się na ławce poziomej, chwytamy sztangę nachwytem(ćwiczenie to można wykonywać również podchwytem-wtedy najbardziej zaangażowane są głowy boczne tricepsów, a najsłabiej głowy przyśrodkowe)na szerokość nieco mniejszą od barków. Prostujemy ręce tak, by gryf sztangi pozostawał wyżej od naszego czoła-ramiona tworzą z podłoga kąt mniejszy niż 90 stopni(do 45 stopni). Możemy użyć sztangi prostej i łamanej. Łamana ma tę zaletę, że zmniejsza naprężenia powstające w nadgarstkach. Ramiona prostujemy do zablokowania łokci. Pracują tylko stawy łokciowe. W ruchu pozostają jedynie przedramiona. Ciężar porusza się po łuku: od czoła do pozycji startowej. Ruch opuszczania wolniejszy od unoszenia. Ćwiczenie to można również wykonywać na ławce skośnej(głową do góry-aktywniej pracują wtedy głowy boczne i przyśrodkowe, i w dół-mocniej zaangażowane są głowy długie mięśni trójgłowych), ale zalecane jest zapoznanie się w pierwszej kolejności z prawidłową techniką na ławce poziomej. W innej wersji tego ćwiczenia sztangę zastępujemy drążkiem wyciągu dolnego umieszczonego za nami.\n" +
                        "\n" +
                        "UWAGI: By ćwiczenie przynosiło oczekiwane efekty, należy bezwzględnie przestrzegać techniki(pełna izolacja pracy mięśni trójgłowych).Należy ostrożnie dobierać ciężar, by pozostawał on pod stałą kontrolą(szczególnie w ostatnich seriach)-można zrobić sobie prawdziwą krzywdę, jeśli nie damy rady w porę zblokować ramion nad czołem.",
                image = getGifAsByteArray(context, R.raw.triceps_4)
            ),

            Exercise(
                name = "WYCISKANIE „FRANCUSKIE” SZTANGIELKI W LEŻENIU",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Uchwyt młotkowy w tym ćwiczeniu pozwala na maksymalne zaangażowanie głów bocznych i przyśrodkowych tricepsów. Technika jest podobna do ćwiczenia ze sztangą, jednak w tej wersji hantla opuszczana jest obok głowy, a nie do czoła. Trzymając hantlę w pozycji młotkowej (dłonie skierowane w stronę siebie), prostujemy ramiona, pracując wyłącznie w stawie łokciowym – przedramiona poruszają się po łuku od boku głowy do pozycji startowej. Ruch opuszczania powinien być wolniejszy od unoszenia, aby zachować pełną kontrolę nad ciężarem.\n" +
                        "\n" +
                        "Ćwiczenie można wykonywać w różnych pozycjach, np. na ławce skośnej, zarówno głową w dół, jak i w górę. W każdej z tych pozycji pracują różne głowy tricepsów, podobnie jak w wersji ze sztangą – w wersji głową w dół mocniej angażowane są głowy długie, a w wersji głową do góry aktywniejsze są głowy boczne i przyśrodkowe.\n" +
                        "\n" +
                        "UWAGI : Jak w przypadku wszystkich ćwiczeń z wolnymi ciężarami, należy pamiętać o dokręceniu zacisków na hantlach przed każdą serią. To proste przypomnienie może uchronić przed nieprzyjemnymi urazami, takimi jak siniaki na twarzy, które mogą powstać w wyniku wysunięcia się ciężaru z hantli.",
                image = getGifAsByteArray(context, R.raw.triceps_5)
            ),

            Exercise(
                name = "PROSTOWNIE RAMIENIA ZE SZTANGIELKĄ W OPADZIE TUŁOWIA",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Przy wyprostowanych plecach pochylamy się do przodu i opieramy jedną ręką o ławkę. W drugiej trzymamy sztangielkę i unosimy ramię nieco ponad linię pleców(nie niżej)-przedramię prostopadle do podłogi. Z tej pozycji pracując tylko tricepsem prostujemy ramię w łokciu unosząc ciężar aż do pełnego wyprostu. W pozycji końcowej zatrzymujemy na moment ruch dla lepszego napięcia mięśni. Musimy zwracać szczególną uwagę na to, by w trakcie ćwiczenia w ruchu było jedynie przedramię(praca w stawie łokciowym).Nie wolno dopuszczać do bujania ramieniem. Ruch opuszczania wolniejszy od unoszenia, z pełną kontrolą ciężaru.\n" +
                        "\n" +
                        "UWAGI: Jest to ćwiczenie typowo izolowane, a o jego skuteczności przesądza właściwa technika wykonania. Unikajmy więc bezwzględnie ruchów oszukanych i ”wspomagania” ruchami stawu barkowego. Dobór ciężaru powinien również być rozsądny-zbyt duży uniemożliwi poprawne wykonanie ćwiczenia. Wielu początkujących nazywa to ćwiczenie „wymachami”, już w nazewnictwie popełniając zasadniczy błąd(do którego niestety stosują się w praktyce)-ćwiczenie to, bowiem nie ma nic wspólnego(oczywiście wykonane poprawnie)z wymachami, lecz polega na kontrolowanym prostowaniu ramion.",
                image = getGifAsByteArray(context, R.raw.triceps_6)
            ),

            Exercise(
                name = "PROSTOWANIE RAMION NA WYCIĄGU W PŁASZCZYŹNE POZIOMEJ STOJĄC",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Stajemy przed wyciągiem górnym(mając go za plecami)w pozycji wykroku(podobnej do „nożyc” ciężarowych),chwytamy rączkę(drążek)nachwytem i prostujemy ramiona w płaszczyźnie poziomej. Ćwiczenie to nie jest polecane osobom początkującym, ze względu na problemy z zachowaniem właściwej techniki. Faza opuszczania wolniejsza niż unoszenia-pełna kontrola ciężaru przez cały czas trwania ćwiczenia.\n" +
                        "\n" +
                        "UWAGI: Ważne jest, by w ruchu były tylko przedramiona. Staw barkowy pozostaje nieruchomy-zaangażowany jedynie staw łokciowy.",
                image = getGifAsByteArray(context, R.raw.triceps_7)
            ),

            Exercise(
                name = "PROSTOWANIE RAMION NA WYCIĄGU W PŁASZCZYŹNIE POZIOMEJ W PODPORZE",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Bardzo dobre ćwiczenie izolowane, podobne do poprzedniego, z tą różnicą, że mamy dodatkowe oparcie dla łokci(w postaci ławeczki).Pozwala ono na lepszą koncentrację i wyizolowanie pracy tricepsów. Chwytamy w pozycji klęcząc, tułów pochylony do przodu, drążek/rączkę wyciągu górnego, umieszczonego za nami. Łokcie opieramy o ławkę i prostujemy ramiona, pracują tylko stawy łokciowe, w ruchu pozostają jedynie przedramiona. Pełen zakres ruchu-od maksymalnego zgięcia, do pełnego wyprostu ramion. Ruch odbywa się w płaszczyźnie poziomej po lekkim łuku.\n" +
                        "\n" +
                        "UWAGI: Unikajmy odrywania łokci od ławki. Ruch opuszczania ciężaru kontrolowany wolniejszy od unoszenia. Świetne ćwiczenie na „dopompowanie” tricepsów.",
                image = getGifAsByteArray(context, R.raw.triceps_8)
            ),

            Exercise(
                name = "POMPKI NA PORĘCZACH",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Rozstaw poręczy powinien tylko nieznacznie przekraczać szerokość barków. Chwytamy poręcze chwytem neutralnym, ręce wyprostowane, klatka piersiowa wypchnięta do przodu, nogi lekko ugięte w kolanach. Opuszczanie i unoszenie tułowia odbywa się poprzez uginanie rąk w stawach łokciowych. Przez cały czas trwania ćwiczenia łokcie powinny znajdować się jak najbliżej tułowia. Tułów wyprostowany w pionie-odchylanie go w przód zmniejsza pracę mięśni trójgłowych, a zwiększa zaangażowanie mięśni klatki piersiowej. Zakres ruchu: od maksymalnego ugięcia do pełnego wyprostu ramion. Tempo umiarkowane, bez zrywów i zbyt szybkiego opuszczania. Ćwiczenie można również wykonywać z użyciem dodatkowego obciążenia, (kiedy dochodzimy do dużej liczby prawidłowo wykonanych powtórzeń w jednej serii), ale ta wersja jest przeznaczona tylko dla bardziej zaawansowanych kulturystów.\n" +
                        "\n" +
                        "UWAGI: Unikajmy dotykania nogami podłogi-to dekoncentruje i „kusi” do wykonania ruchu oszukanego. Należy uważać, by nie opuszczać się zbyt nisko-przeciąża to stawy barkowe.",
                image = getGifAsByteArray(context, R.raw.triceps_9)
            ),

            Exercise(
                name = "POMPKI W PODPORZE TYŁEM",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Wspaniałe ćwiczenie na zakończenie treningu-maksymalnie „dopompowuje” tricepsy. Polega na wykonywaniu ruchów opuszczania i unoszenia tułowia podpierając się o ławkę z tyłu, a nogi pozostają (działając jak zawias)oparte na drugiej ławce(dobrze jest, gdy ławka ta jest nieco wyższa od tej, na której trzymamy ręce).Ramiona prostujemy do końca dla lepszego napięcia mięśni. Ruch opuszczania powolny, unoszenia nieco szybszy. Ćwiczenie to można wykonywać z uchwytem jak na gif`ie, lub ustawiając ławkę wzdłuż chwytamy ją na końcu po bokach(uchwyt równoległy).Dla zwiększenia trudności ćwiczenia można zastosować dodatkowe obciążenie kładąc np. krążek na uda(gif).Można też wykonywać np. kilka-kilkanaście powtórzeń z obciążeniem, po czym partner zdejmuje nam ciężar, a my wykonujemy jeszcze kilka-kilkanaście powtórzeń. Taka metoda dodatkowo pobudzi mięśnie.\n" +
                        "\n" +
                        "UWAGI: Ławki musza być na tyle wysokie, żebyśmy opuszczając się nie dotykali podłogi. Nie powinno się przesadzać z głębokością opuszczania-zbyt głębokie opuszczanie przeciąża stawy barkowe(podobnie, jak przy pompkach na poręczach).",
                image = getGifAsByteArray(context, R.raw.triceps_10)
            ),

            Exercise(
                name = "PROSTOWANIE RAMIENIA PODCHWYTEM NA WYCIĄGU STOJĄC",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Podobne ćwiczenie do prostowania nachwytem. Nieco trudniejsze w wykonaniu, a to za sprawą niełatwego w opanowaniu podchwytu. Łokcie nieruchomo przy tułowiu, pełen zakres ruchu w łokciach. Kontrola ciężaru przez cały czas, opuszczanie wolniejsze od unoszenia. W końcowym momencie(przy wyprostowanym ramieniu) wstrzymujemy na chwilę ruch dla lepszego napięcia mięśni.\n" +
                        "\n" +
                        "UWAGI: Ostrożnie z doborem ciężaru, w podchwycie staw łokciowy jest bardziej narażony na kontuzje. Unikamy szarpania, ruch płynny.",
                image = getGifAsByteArray(context, R.raw.triceps_11)
            ),

            Exercise(
                name = "WYCISKANIE W LEŻENIU NA ŁAWCE POZIOMEJ WĄSKIM UCHWYTEM",
                targetMuscle = TargetMuscle.TRICEPS.name,
                description = "Technika decyduje o tym, że ćwiczenie to ma angażować bardziej mięśnie trójgłowe ramion, a dopiero w drugim rzędzie mięśnie klatki piersiowej. Decyduje o tym nasze nastawienie psychiczne i koncentracja na zaangażowaniu tricepsów, oraz niewielkie (w stosunku do wyciskania wąsko na klatkę)różnice w ułożeniu łokci podczas ćwiczenia. Otóż nie poruszają się one, jak w treningu klatki na boki(prostopadle do tułowia), ale nieco do środka(pod kątem ok.35-45 stopni do tułowia).Reszta techniki podobna, jak w treningu klatki. Uchwyt na szerokość barków(lub nieco węższy).Do ćwiczenia można użyć sztangi z łamanym gryfem-zmniejszy napięcia w nadgarstkach. Pozwala na lepszą koncentrację na pracy samych tricepsów. Nie prostujemy ramion do końca, ale zatrzymujemy ruch przed zblokowaniem łokci. Polepsza to napięcie mięśni trójgłowych. Ćwiczenie to można wykonywać również sztangą o łamanym gryfie. Można użyć również maszyny Smitha.\n" +
                        "\n" +
                        "UWAGI: Zbyt wąski uchwyt przeciąża stawy łokciowe i nadgarstki. Ciężar dobieramy tak, by nie angażować mięśni klatki piersiowej.",
                image = getGifAsByteArray(context, R.raw.triceps_12)
            ),

            // SHOULDERS
            Exercise(
                name = "Wyciskanie sztangi sprzed głowy",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie to angażuje przednie i boczne aktony mięśni naramiennych. Wyciskanie sztangi sprzed głowy można wykonywać zarówno w pozycji siedzącej, jak i stojącej.",
                image = getGifAsByteArray(context, R.raw.shoulders_1)
            ),
            Exercise(
                name = "Wyciskanie sztangi zza głowy",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Angażuje głównie przednie i boczne aktony mięśni naramiennych. Wyciskanie zza głowy wymaga dużej kontroli, aby uniknąć kontuzji.",
                image = getGifAsByteArray(context, R.raw.shoulders_2)
            ),
            Exercise(
                name = "Wyciskanie hantli nad głowę",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Wyciskanie hantli nad głowę pozwala na lepszą izolację mięśni naramiennych. Ćwiczenie to można wykonywać w pozycji siedzącej, aby zwiększyć stabilizację.",
                image = getGifAsByteArray(context, R.raw.shoulders_3)
            ),
            Exercise(
                name = "Arnoldki",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wymyślone przez Arnolda Schwarzeneggera. Ruch wyciskania połączony z rotacją dłoni angażuje wszystkie aktony mięśni naramiennych.",
                image = getGifAsByteArray(context, R.raw.shoulders_4)
            ),
            Exercise(
                name = "Unoszenie sztangielek bokiem w górę",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie izolujące boczne aktony mięśni naramiennych. Wykonujemy je w pozycji stojącej lub siedzącej, unosząc ramiona bokiem.",
                image = getGifAsByteArray(context, R.raw.shoulders_5)
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
