package com.kk3k.workouttracker.db

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                name = "WYCISKANIE SZTANGI SPRZED GŁOWY",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "W pozycji stojącej: stajemy w rozkroku nieco szerszym, niż barki (inna forma ćwiczenia zakłada wysunięcie jednej nogi nieco w przód, dla lepszej równowagi) - klatka piersiowa wypchnięta ku przodowi, zachowana naturalna krzywizna kręgosłupa-pracują podczas ćwiczenia tylko ramiona i barki (staramy się unikać dodatkowych ruchów tułowia i nóg), uchwyt nieco szerszy, niż rozstaw barków.\n" +
                        "Im węższy uchwyt, tym bardziej pracę w ćwiczeniu przejmują mięśnie trójgłowe ramion.\n" +
                        "\n" +
                        "W pozycji siedzącej: staramy się zwracać dużą uwagę na prawidłową pozycję-przez cały czas należy mieć wypchniętą ku przodowi klatkę piersiową i zachowywać naturalna krzywiznę kręgosłupa. Taka pozycja zabezpiecza (oczywiście nie w pełni) przed urazami dolnego odcinka kręgosłupa. (można zastosować oparcie o ławkę-zmniejsza naciski na dyski międzykręgowe).\n" +
                        "\n" +
                        "UWAGI: Nie poleca się tego ćwiczenia dla początkujących, ponieważ jest ono bardzo kontuzjogenne i najmniejsze błędy w jego wykonaniu mogą spowodować niebezpieczne kontuzje.",
                image = getGifAsByteArray(context, R.raw.shoulders_1)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGI ZZA GŁOWY",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Podczas całego zakresu ruchu sztangi należy kontrolować ciężar-ruch powinien być płynny, sztangę opuszczamy aż do linii barków (druga wersja zakłada opuszczanie sztangi tylko do linii uszu, co wydaje się mniej niebezpieczne dla samych stawów barkowych i pozwala na lepsze napięcie mięśni), po czym wyciskamy sztangę zatrzymując ruch zanim ramiona się wyprostują (aby utrzymać stałe napięcie ćwiczonych mięśni)\n" +
                        "\n" +
                        "UWAGI: Nie poleca się tego ćwiczenia dla początkujących, ponieważ jest ono bardzo kontuzjogenne i najmniejsze błędy w jego wykonaniu mogą spowodować niebezpieczne kontuzje\n" +
                        "\n" +
                        " ",
                image = getGifAsByteArray(context, R.raw.shoulders_2)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGIELEK",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Dłonie ze sztangielkami przez cały czas trzymamy tak, aby ich wewnętrzne części skierowane były do przodu(inna wersja przewiduje uchwyt młotkowy-dłonie zwrócone w czasie całego ruchu palcami w kierunku głowy).Ruch powinien odbywać się  pod pełną kontrolą ciężaru-ważna jest również pozycja podczas ćwiczenia-podobna do pozycji przy wyciskaniu sztangi. (wypchnięta klatka, naturalna krzywizna kręgosłupa)",
                image = getGifAsByteArray(context, R.raw.shoulders_3)
            ),

            Exercise(
                name = "ARNOLDKI",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie można wykonywać zarówno w pozycji stojącej, jak i siedzącej-z łokciami przy tułowiu chwytamy sztangielki i unosimy je na wysokość barków tak, aby palce dłoni były skierowane do nas (podchwyt), równym tempem wypychamy sztangielki ponad głowę do pozycji, w której jeszcze nie są zablokowane ramiona-podczas wyciskania sztangielek obracamy dlonie tak,aby w końcowej  fazie ruchu palce były skierowane do przodu (nachwyt) - w szczytowym punkcie powtórzenia wstrzymujemy ruch na moment, po czym opuszczamy sztangielki do pozycji wyjściowej. Unikajmy blokowania ramion nad głową-dzięki temu utrzymamy stałe napięcie ćwiczonych mięśni.",
                image = getGifAsByteArray(context, R.raw.shoulders_4)
            ),

            Exercise(
                name = "UNOSZENIE SZTANGIELEK BOKIEM W GÓRĘ",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie można wykonywać w pozycji stojącej lub siedzącej, oburącz lub jednorącz. W pozycji wyjściowej tułów lekko pochylony, ręce ze sztangielkami nieco ugięte w łokciach, opuszczone w dół, dłonie wewnętrznymi stronami skierowane do środka. Ruch unoszenia rozpoczynamy przy ugiętych rękach, łokcie w każdej fazie ruchu wyprzedzają dłonie. Sztangielki unosimy powyżej linii barków i bez zatrzymania opuszczamy powoli w dół (lub przytrzymujemy w pozycji szczytowej przez chwilę w celu dodatkowego napięcia mięśni). Ćwiczenie to można również wykonywać jednorącz sztangielką w odchyleniu-chwytamy się poręczy, drabinek lub jakiegoś innego przyrządu i odchylamy tułów w bok (jedna ręką trzymamy się poręczy, a w drugiej trzymamy sztangielkę) i unosimy sztangielkę bokiem w górę do poziomu (w tym punkcie można zatrzymać ruch na chwilę) następnie opuszczamy ją do pozycji wyjściowej.\n" +
                        "\n" +
                        " UWAGI: należy unikać bujania tułowiem i odchylania go do tyłu w trakcie wykonania ćwiczenia-angażowane są wtedy dodatkowe mięsnie",
                image = getGifAsByteArray(context, R.raw.shoulders_5)
            ),

            Exercise(
                name = "UNOSZENIE SZTANGIELEK W OPADZIE TUŁOWIA",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w pozycji siedzącej lub stojącej-W pozycji stojącej: tułów ustawiamy w położeniu zbliżonym do prostopadłego do podłoża i staramy się w trakcie ruchu nie wykonywać nim tzw. bujania-utrzymujemy możliwie sztywno. Z pozycji wyjściowej ruchem kolistym unosimy sztangielki maksymalnie w górę, utrzymując przez cały czas ćwiczone mięśnie w stanie napięcia. Staramy się, aby w ruchu powrotnym sztangielki poruszały się po tym samym torze. Jak w poprzednim ćwiczeniu ,można w pozycji szczytowej przytrzymać przez chwilę sztangielki w celu dodatkowego napięcia mięśni.",
                image = getGifAsByteArray(context, R.raw.shoulders_6)
            ),

            Exercise(
                name = "PODCIĄGANIE SZTANGI WZDŁUŻ TUŁOWIA",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "W pozycji stojącej, rozkrok nieco szerszy od rozstawu barków, uchwyt na szerokość ramion (inna wersja przewiduje szerszy rozstaw dłoni, nawet szerszy niż rozstaw barków-zaangażowane są wtedy bardziej boczne aktony mięsni naramiennych, a ruch kończymy na wysokości klatki piersiowej). Ruchem wolnym i kontrolowanym unosimy sztangę w kierunku brody, starając się aby przemieszczała się możliwie najbliżej tułowia. Łokcie przez cały czas trzymamy powyżej gryfu sztangi. Ruch unoszenia kończymy, gdy sztanga znajdzie się na wysokości klatki piersiowej (lub staramy się podciągnąć sztangę aż do brody), opuszczamy również powoli, unikamy odchylania i bujania tułowia. Koncentrujemy się na unoszeniu łokci, a sztanga podąży za nimi.\n" +
                        "\n",
                image = getGifAsByteArray(context, R.raw.shoulders_7)
            ),

            Exercise(
                name = "PODCIĄGANIE SZTANGIELEK WZDŁUŻ TUŁOWIA",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "W pozycji stojącej przyjmujemy rozkrok nieco szerszy od rozstawu barków. Trzymamy sztangielki po bokach ciała, dłonie skierowane do tułowia. Alternatywnie, możemy przyjąć szerszy rozstaw rąk, co bardziej zaangażuje boczne aktony mięśni naramiennych. Ruch unoszenia wykonujemy powoli i kontrolowanie, unosząc sztangielki w kierunku brody. Staramy się, aby sztangielki przemieszczały się możliwie jak najbliżej tułowia. Łokcie przez cały czas powinny znajdować się powyżej sztangielek.\n" +
                        "\n" +
                        "Ruch unoszenia kończymy, gdy sztangielki znajdą się na wysokości klatki piersiowej (lub opcjonalnie unosimy je aż do brody). Opuszczamy sztangielki również powoli, unikając odchylania i bujania tułowiem. Koncentrujemy się na unoszeniu łokci – sztangielki podążą za nimi.",
                image = getGifAsByteArray(context, R.raw.shoulders_8)
            ),

            Exercise(
                name = "UNOSZENIE RAMION W PRZÓD ZE SZTANGĄ",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w staniu-rozkrok nieco szerzej od barków, uchwyt na szerokość barków-ułożenie dłoni zależy od rodzaju sztangi ,z jaką wykonujemy ćwiczenie: można zastosować nachwyt (przy użyciu sztangi prostej), nachwyt pod kątem (przy użyciu sztangi łamanej), uchwyt młotkowy (przy użyciu „kratownicy”)- rozbudowuje się dodatkowo obszar połączenia m. piersiowych z naramiennymi .Unosimy sztangę miarowym ruchem (bez szarpania) ponad poziom barków i opuszczamy również płynnym ruchem. Staramy się nie bujać tułowiem. W szczytowym położeniu można zatrzymać ruch na chwilę w celu dodatkowego napięcia mięśni. W fazie negatywnej (opuszczanie sztangi) utrzymujemy pełną kontrolę nad ciężarem.\n" +
                        "\n" +
                        "UWAGI: zaleca się w tym ćwiczeniu ostrożne dozowanie ciężaru-ćwiczenie jest kontuzjogenne.",
                image = getGifAsByteArray(context, R.raw.shoulders_9)
            ),

            Exercise(
                name = "UNOSZENIE RAMION W PRZÓD ZE SZTANGIELKAMI",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w pozycji stojącej- można również wykonywać w pozycji siedzącej. Technika-jak wyżej, z ta różnicą, że można wykonywać ruchy na  obiema rękami jednocześnie, lub pojedynczo (jedna ręka w gorę, druga w spoczynku, lub jedna ręka w górę, a druga w tym czasie  porusza się w dół).\n" +
                        "\n" +
                        "Uchwyt (ustawienie nadgarstka) ,jak w ćwiczeniu ze sztangą może być różny-adekwatny do celu ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.shoulders_10)
            ),

            Exercise(
                name = "UNOSZENIE RAMION ZE SZTANGIELKAMI W LEŻENIU",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w pozycji leżącej lub ławeczce. Ruch zaczynamy nieco poniżej poziomu i miarowo unosimy sztangielkę do momentu, kiedy poczujemy brak napięcia w mięśniach barków, czyli mniej więcej do pionu. Można stosować uchwyt jak na Gifie (pracują tylne i boczne aktony mięśni naramiennych), lecz można zastosować chwyt kciukiem do dołu, separujemy wtedy bardziej tylne aktony mięśni naramiennych).",
                image = getGifAsByteArray(context, R.raw.shoulders_11)
            ),

            Exercise(
                name = "UNOSZENIE RAMION W PRZÓD Z LINKAMI WYCIĄGU",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w pozycji leżącej na ławce lub w lekkim skosie. Chwytamy linki wyciągu dolnego, ustawiając je po bokach ciała, nieco poniżej poziomu barków. Ruch zaczynamy od lekkiego rozciągnięcia linek w dół i miarowo unosimy je, aż poczujemy napięcie w mięśniach barków, zatrzymując ruch, gdy ręce znajdują się w pozycji pionowej. Można zastosować chwyt neutralny, co aktywuje tylne i boczne aktony mięśni naramiennych, lub zmienić chwyt na \"kciukiem w dół,\" co bardziej izoluje tylne aktony mięśni naramiennych.",
                image = getGifAsByteArray(context, R.raw.shoulders_12)
            ),

            Exercise(
                name = "UNOSZENIE RAMION BOKIEM W GÓRĘ Z LINKAMI WYCIĄGU",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie można wykonywać w pozycji stojącej lub siedzącej, oburącz lub jednorącz. W pozycji wyjściowej tułów lekko pochylony, ręce trzymają linki wyciągu dolnego, nieco ugięte w łokciach i opuszczone w dół, dłonie skierowane wewnętrznymi stronami do środka. Ruch unoszenia rozpoczynamy przy ugiętych rękach, utrzymując łokcie przed dłońmi przez cały czas. Linki unosimy ponad linię barków i bez zatrzymania opuszczamy powoli w dół (można również przytrzymać je w pozycji szczytowej przez chwilę, aby dodatkowo napiąć mięśnie). Ćwiczenie można wykonywać także jednorącz: chwytamy linkę wyciągu jedną ręką, trzymając się drugą ręką poręczy lub innego stabilnego przyrządu, i odchylamy tułów w bok. W tej pozycji unosimy linkę bokiem w górę do poziomu (w najwyższym punkcie można zatrzymać ruch na chwilę), a następnie opuszczamy ją do pozycji wyjściowej.\n" +
                        "\n" +
                        "UWAGI: Należy unikać bujania tułowiem i odchylania go do tyłu podczas wykonywania ćwiczenia, ponieważ angażuje to dodatkowe mięśnie, co może zmniejszyć efektywność izolacji mięśni naramiennych.\n",
                image = getGifAsByteArray(context, R.raw.shoulders_13)
            ),

            Exercise(
                name = "UNOSZENIE RAMION BOKIEM W GÓRĘ W OPADZIE TUŁOWIA Z LINKAMI WYCIĄGU",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Ćwiczenie wykonujemy w pozycji siedzącej lub stojącej. W pozycji stojącej: tułów ustawiamy blisko prostopadłego do podłoża i staramy się nie wykonywać tzw. bujania – tułów utrzymujemy możliwie sztywno. Z pozycji wyjściowej, ruchem kolistym unosimy linki wyciągu maksymalnie w górę, utrzymując mięśnie w stałym napięciu. W ruchu powrotnym staramy się, aby linki poruszały się po tym samym torze, kontrolując ich powrót. Jak w poprzednim ćwiczeniu, można w pozycji szczytowej przytrzymać linki przez chwilę, aby dodatkowo napiąć mięśnie.\n" +
                        "\n",
                image = getGifAsByteArray(context, R.raw.shoulders_14)
            ),

            Exercise(
                name = "ODWROTNE ROZPIĘTKI NA MASZYNIE",
                targetMuscle = TargetMuscle.SHOULDERS.name,
                description = "Przed rozpoczęciem ćwiczenia ustawiamy wysokość siedzenia, tak aby ręce poruszały się równolegle do podłoża. Stosujemy chwyt neutralny (młotkowy). Chwytamy rączki przyrządu, robimy wdech i wstrzymujemy oddech na czas odwodzenia ramion do tyłu. Dochodzimy do momentu, w którym łokcie znajdą się tuż za plecami. Jeśli nie możemy odciągnąć łokci odpowiednio daleko do tyłu, powinniśmy zmniejszyć obciążenie.",
                image = getGifAsByteArray(context, R.raw.shoulders_15)
            ),

            // CHEST
            Exercise(
                name = "WYCISKANIE SZTANGI W LEŻENIU NA ŁAWCE POZIOMEJ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Kładziemy się na ławce tak, by nogi ugięte były pod kątem prostym i przylegały do podłoża. Uchwyt średni(taki, by po opuszczeniu sztangi na klatkę ramiona tworzyły z przedramionami kąt prosty-kciuk dla bezpieczeństwa powinien obejmować sztangę-choć wielu bardziej doświadczonych kulturystów preferuje raczej tzw. ”małpi chwyt” (kciuk ponad gryfem).Opuszczamy sztangę na klatkę na wysokość ok.1 cm powyżej brodawek. Przy opuszczaniu sztangi wykonujemy głęboki wdech-wydychamy powietrze w trakcie wyciskania. Można okresowo praktykować przytrzymywanie sztangi przez chwilę na klatce przed wyciśnięciem.(szczególnie przydatne, jeżeli mamy w planach ewentualne starty w zawodach w wyciskaniu)-dodatkowo rozbudowuje siłę-pobudza do dodatkowego wysiłku. Łokcie prowadzimy w trakcie całego ruchu po bokach-tak by nie „uciekały”do środka. Ruch wyciskania kończymy(dla lepszego napięcia mięśni)zanim łokcie zostaną zblokowane. Ćwiczenie to można wykonywać również na maszynach lub na suwnicy Smitha\n" +
                        "\n" +
                        "UWAGI: Jest to ćwiczenie stymulujące wzrost całej klatki. Dla wielu kulturystów kluczowe w budowaniu muskulatury tej grupy mięśniowej. Pompuje krew na cały obszar klatki piersiowej. Należy zająć na ławce taką pozycję, która gwarantuje dobrą stabilność całego ciała. Podczas wyciskania powinno się unikać wstrzymywania oddechu oraz „mostkowania”(odrywania bioder od ławki).Ruch powinien być płynny(w fazie opuszczania również)-staramy się przez cały czas kontrolować sztangę i jej ruch, jednocześnie koncentrując się na stałym napięciu mięśni piersiowych. Gryf sztangi ma dotknąć naszej klatki, a nie uderzać w nią.",
                image = getGifAsByteArray(context, R.raw.chest_1)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGIELEK W LEŻENIU NA ŁAWCE POZIOMEJ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Podobnie jak w ćwiczeniu ze sztangą-daje ono jednak dodatkowe możliwości ćwiczącemu- np. chcąc położyć większy nacisk na rozwój wewnętrznej części klatki można w górnym położeniu(podczas fazy wyciskania) zbliżać sztangielki do siebie, co nie jest możliwe przy użyciu sztangi. Również faza opuszczania może mieć nieco odmienny przebieg - przy ćwiczeniu ze sztangą ruch ogranicza nam gryf sztangi, a wykorzystując do tego ćwiczenia sztangielki możemy opuszczać ręce poniżej (głębiej) poziomu klatki, co dodatkowo rozciąga mięsnie( w myśl zasady: im większy zakres ruchu, tym pełniejszy rozwój mięśni). Dodatkowo można w końcowej fazie ruchu obracać nadgarstki tak, by dłonie skierowane były do siebie palcami(pozwala to na dodatkowe napięcie wewnętrznych części mięśni).\n" +
                        "\n" +
                        "\n" +
                        "UWAGI: Jest to ćwiczenie stymulujące wzrost całej klatki. Dla wielu kulturystów kluczowe w budowaniu muskulatury tej grupy mięśniowej. Pompuje krew na cały obszar klatki piersiowej. Należy zająć na ławce taką pozycję, która gwarantuje dobrą stabilność całego ciała. Podczas wyciskania powinno się unikać wstrzymywania oddechu oraz „mostkowania”(odrywania bioder od ławki).Ruch powinien być płynny(w fazie opuszczania również)-staramy się przez cały czas kontrolować sztangielki i ich ruch, jednocześnie koncentrując się na stałym napięciu mięśni piersiowych. Sztangielki mają dotknąć naszej klatki, a nie uderzać w nią. Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków-nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste.",
                image = getGifAsByteArray(context, R.raw.chest_2)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGI W LEŻENIU NA ŁAWCE SKOŚNEJ-GŁOWĄ  W GÓRĘ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ławkę ustawiamy pod kątem 30-45 stopni(większy kąt bardziej angażuje w ćwiczeniu mięśnie naramienne), kładziemy się głową do góry. Uchwyt sztangi, oddychanie, prowadzenie łokci, jak w ćwiczeniu na ławce płaskiej. Chwytamy sztangę i opuszczamy ją na klatkę-ok.10 cm. poniżej szyi. Staramy się skupiać uwagę na angażowaniu w pracę tylko mięsni piersiowych i maksymalnym wyłączeniu z niej mięsni naramiennych. W tym celu można lekko wygiąć grzbiet, jednocześnie wypychając klatkę do przodu. Nie wolno jednak odrywać zbytnio pleców od ławki, a biodra muszą bezwzględnie przylegać do ławki. \n" +
                        "\n" +
                        "UWAGI: Jak w ćwiczeniu na ławce płaskiej. Ponadto powinno się zadbać o odpowiednie ustabilizowanie tułowia-by nie zjeżdżać ze sztangą z ławki w dół(podpórki pod nogi, siedzisko pod pośladki, itp.).Niektórzy kulturyści praktykują opuszczanie sztangi do szyi(w celu większego wyizolowania górnej części mięśni piersiowych), ale jest to niebezpieczne-nie zaleca się tej wersji początkującym.",
                image = getGifAsByteArray(context, R.raw.chest_3)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGIELEK W LEŻENIU NA ŁAWCE SKOŚNEJ-GŁOWĄ W GÓRĘ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ławkę ustawiamy pod kątem 30-45 stopni (większy kąt bardziej angażuje mięśnie naramienne), kładziemy się głową do góry. Hantle chwytamy neutralnie lub lekko pod kątem, tak aby ruch pozostał płynny. Opuszczamy hantle w stronę klatki piersiowej, około 10 cm poniżej szyi, utrzymując pełną kontrolę nad ciężarem. Skupiamy się na angażowaniu mięśni piersiowych i maksymalnym wyłączeniu mięśni naramiennych z ruchu. W tym celu można lekko wygiąć grzbiet, jednocześnie wypychając klatkę do przodu. Unikamy jednak nadmiernego unoszenia pleców – biodra muszą bezwzględnie przylegać do ławki.\n" +
                        "\n" +
                        "UWAGI: Jak w ćwiczeniu na ławce płaskiej. Należy zadbać o odpowiednie ustabilizowanie tułowia, by nie zsuwać się z hantlami z ławki (pomocne mogą być podpórki pod nogi lub siedzisko pod pośladki). Niektórzy opuszczają hantle bliżej szyi, by lepiej wyizolować górną część mięśni piersiowych, ale nie jest to zalecane początkującym ze względu na możliwość kontuzji. Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków – nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste.",
                image = getGifAsByteArray(context, R.raw.chest_4)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGI W LEŻENIU NA ŁAWCE SKOŚNEJ-GŁOWĄ W DÓŁ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ławeczkę ustawiamy pod kątem 30-45 stopni- kładziemy się głową w dół, zapierając nogi o coś dla stabilności,(aby nie zsunąć się w dół podczas wykonywania ćwiczenia).Ruch wygląda tak samo, jak podczas wyciskania na ławce poziomej. Opuszczając sztangę nabieramy głęboko powietrza, wydychamy je wyciskając sztangę w górę. Ćwiczenie można wykonywać również przy pomocy suwnicy Smitha, lub maszyn.\n" +
                        "\n" +
                        "UWAGI: Bardzo ważne jest zabezpieczenie przed osuwaniem się w dół podczas wykonania ćwiczenia-może to być bardzo niebezpieczne(szczególnie, kiedy trenujemy sami). Jest to bardzo przydatne ćwiczenie na rozwój i podkreślenie dolnych części mięśni piersiowych. Nie poleca się tego ćwiczenia osobom mającym kłopoty z ciśnieniem tętniczym. Zamiennie można (z podobnym skutkiem) stosować pompki na poręczach-opis ćwiczenia poniżej.",
                image = getGifAsByteArray(context, R.raw.chest_5)
            ),

            Exercise(
                name = "WYCISKANIE SZTANGIELEK W LEŻENIU NA ŁAWCE SKOŚNEJ-GŁOWĄ W DÓŁ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Pozycja, jak w ćwiczeniu z użyciem sztangi-dodatkowe możliwości: np. chcąc położyć większy nacisk na rozwój wewnętrznej części klatki można w górnym położeniu(podczas fazy wyciskania) zbliżać sztangielki do siebie, co nie jest możliwe przy użyciu sztangi. Również faza opuszczania może mieć nieco odmienny przebieg - przy ćwiczeniu ze sztangą ruch ogranicza nam gryf sztangi, a wykorzystując do tego ćwiczenia sztangielki możemy opuszczać ręce poniżej (głębiej) poziomu klatki, co dodatkowo rozciąga mięsnie( w myśl zasady: im większy zakres ruchu, tym pełniejszy rozwój mięśni). Dodatkowo można w końcowej fazie ruchu obracać nadgarstki tak, by dłonie skierowane były do siebie palcami(pozwala to na dodatkowe napięcie wewnętrznych części mięśni).\n" +
                        "\n" +
                        "UWAGI: Bardzo ważne jest zabezpieczenie przed osuwaniem się w dół podczas wykonania ćwiczenia-może to być bardzo niebezpieczne(szczególnie, kiedy trenujemy sami).Nie poleca się tego ćwiczenia osobom mającym kłopoty z ciśnieniem tętniczym. Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków-nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste.",
                image = getGifAsByteArray(context, R.raw.chest_6)
            ),

            Exercise(
                name = "ROZPIĘTKI ZE SZTANGIELKAMI W LEŻENIU NA ŁAWCE POZIOMEJ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ćwiczenie rozpoczynamy z ramionami wyprostowanymi-prostopadłymi do podłoża( palce dłoni skierowane są do siebie), a w trakcie ruchu lekko uginamy je w łokciach. Nabieramy powietrza, gdy sztangielki są u góry(na początku ruchu) wydychamy je, gdy sztangielki wędrują do góry. W końcowej fazie ruch można zatrzymać na chwilę w celu lepszego napięcia mięśni. Staramy się nie uderzać sztangielkami o siebie, ale zatrzymywać ruch zanim się zetkną. Ważne jest wykonywanie pełnego zakresu ruchu(by dostatecznie rozciągnąć mięśnie)-im większy zakres wykonanego ruchu, tym pełniejszy ogólny rozwój mięśnia. Obciążenia dostosowujemy takie, by  wykonywać ćwiczenie poprawnie technicznie. Ćwiczenie to można również wykonywać przy pomocy linek wyciągów,lub specjalnej maszyny.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie rozwija wewnętrzne i zewnętrzne partie klatki piersiowej. Należy stosować umiarkowane tempo wykonania ćwiczenia-pośpiech najczęściej powoduje błędy w technice-w końcowej fazie ruch przeobraża się w wyciskanie, a tego należy unikać. Podczas wykonywania ćwiczenia za pomocą wyciągów można w końcowej fazie ruchu skrzyżować linki dla lepszego napięcia mięśni i większego zaangażowania wewnętrznych partii klatki piersiowej. Podobne rozwiązanie można otrzymać krzyżując sztangielki w końcowej fazie ruchu(należy jednak pamiętać, by raz lewa ręka była wyżej, raz prawa). Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków-nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste.",
                image = getGifAsByteArray(context, R.raw.chest_7)
            ),

            Exercise(
                name = "ROZPIĘTKI ZE SZTANGIELKAMI W LEŻENIU NA ŁAWCE SKOŚNEJ - GŁOWĄ DO GÓRY",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ławeczkę ustawiamy pod kątem 30-45 stopni, ćwiczenie rozpoczynamy z ramionami wyprostowanymi-prostopadłymi do podłoża( palce dłoni skierowane są do siebie), a w trakcie ruchu lekko uginamy je w łokciach. Nabieramy powietrza, gdy sztangielki są u góry(na początku ruchu) wydychamy je, gdy sztangielki wędrują do góry.\n" +
                        "\n" +
                        "Ważne jest wykonywanie pełnego zakresu ruchu(by dostatecznie rozciągnąć mięśnie)-im większy zakres wykonanego ruchu, tym pełniejszy ogólny rozwój mięśnia. Obciążenia dostosowujemy takie, by wykonywać ćwiczenie poprawnie technicznie. Ćwiczenie również można wykonywać zastępując sztangielki rączkami wyciągów.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie rozwija wewnętrzne i zewnętrzne partie górnej części klatki piersiowej. Należy stosować umiarkowane tempo wykonania ćwiczenia-pośpiech najczęściej powoduje błędy w technice-w końcowej fazie ruch przeobraża się w wyciskanie, a tego należy unikać). Podczas wykonywania ćwiczenia za pomocą wyciągów można w końcowej fazie ruchu skrzyżować linki dla lepszego napięcia mięśni i większego zaangażowania wewnętrznych partii klatki piersiowej. Podobne rozwiązanie można otrzymać krzyżując sztangielki w końcowej fazie ruchu(należy jednak pamiętać, by raz lewa ręka była wyżej, raz prawa). Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków-nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste."
            ),

            Exercise(
                name = "WYCISKANIE SZTANGI W LEŻENIU NA ŁAWCE POZIOMEJ WĄSKIM UCHWYTEM",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Aby ćwiczenie to angażowało głównie mięśnie piersiowe, a dopiero w drugim stopniu trójgłowe ramion, należy prowadzić łokcie możliwie najdalej na boki od tułowia i koncentrować się na pracy mięsni klatki, a nie ramion. Pozycja na ławce i oddychanie, jak przy wyciskaniu w szerokim uchwycie. Uchwyt na szerokość barków lub odrobinę węższy. Ćwiczenie można wykonywać również na suwnicy Smitha.\n" +
                        "\n" +
                        "UWAGI: Zbyt wąski uchwyt przeciąża mocno nadgarstki i stawy łokciowe.",
                image = getGifAsByteArray(context, R.raw.chest_9)
            ),

            Exercise(
                name = "PRZENOSZENIE SZTANGIELKI W LEŻENIU W POPRZEK ŁAWKI POZIOMEJ",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Kładziemy się w poprzek ławki poziomej-w ten sposób, by do jej powierzchni przylegała jedynie część pleców w okolicy łopatek i karku. Chwytamy sztangielkę pod talerzami(gryf sztangielki pomiędzy kciukami i palcami wskazującymi).Ramiona lekko ugięte podczas całego ruchu-ich prostowanie w trakcie ćwiczenia angażuje dodatkowo mięsnie trójgłowe ramion i najszersze grzbietu. Sztangielkę opuszczamy do tyłu w dół do momentu maksymalnego wychylenia, w jakim możemy kontrolować ciężar. Bardzo istotne jest w tym ćwiczeniu oddychanie- opuszczając w tył sztangielkę-nabieramy mocno powietrza( maksymalnie rozszerzając klatkę)-w drodze powrotnej wypuszczamy je. Dla lepszego zaangażowania mięśni zębatych można przy opuszczaniu sztangielki w tył jednocześnie obniżyć biodra, co dodatkowo rozciągnie tułów i powiększy zakres ruchu. Pamiętać należy również o koncentracji na pracy mięśni piersiowych i wyeliminowaniu pracy mięśni grzbietu. Można to ćwiczenie również wykonywać leżąc wzdłuż ławki, ale wtedy zakres ruchu jest mniejszy.\n" +
                        "\n" +
                        "UWAGI: Jest to bardzo dobre ćwiczenie na powiększenie objętości klatki piersiowej. Rozwija dobrze mięśnie zębate-co procentuje szczególnie w pozach na mięsnie brzucha. Ćwiczenie jest efektywniejsze, gdy wykonujemy je nie wzdłuż ławeczki, ale w poprzek. Należy pamiętać o oddychaniu-jest bardzo istotne w poszerzaniu klatki piersiowej. Ponadto banalne przypomnienie o dokładnym dokręceniu zacisków-nikt nie chce mieć twarzy w siniakach, a wbrew pozorom takie niedopatrzenie jest bardzo częste.",
                image = getGifAsByteArray(context, R.raw.chest_10)
            ),

            Exercise(
                name = "POMPKI NA PORĘCZACH",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "W tym ćwiczeniu, podobnie jak przy wyciskaniu wąsko również ważne jest by pracę wykonywały w głównym stopniu mięśnie piersiowe, w mniejszym stopniu chodzi nam o pracę mięśni trójgłowych ramion. Elementem decydującym o większym zaangażowaniu jednych, bądź drugich mięśni jest pozycja tułowia i ułożenie łokci. Należy wypracować takie ułożenie tułowia, przy którym główną pracę będą wykonywały mięśnie piersiowe, a łokcie pracować powinny w pewnym oddaleniu od tułowia. Dla lepszego wyeliminowania pracy tricepsów i lepszego napięcia mięsni piersiowych można również nie prostować ramion do końca.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie to świetnie rozwija dolne rejony klatki piersiowej, jak również siłę do wyciskania w leżeniu. Można je wykonywać jako rozgrzewkę do wyciskań lub użyć, jako ćwiczenie wykańczające trening. W momencie, gdy osiągniemy taki poziom, który pozwoli nam na wykonywanie dużych ilości powtórzeń w tym ćwiczeniu, można zastosować dodatkowe obciążenie w postaci zawieszonego na pasku ciężaru.",
                image = getGifAsByteArray(context, R.raw.chest_11)
            ),

            Exercise(
                name = "ROZPIĘTKI W SIADZIE NA MASZYNIE",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ważne jest zajęcie dobrej pozycji do ćwiczenia(odpowiednia regulacja wysokości siedzenia-ramiona powinny tworzyć z przedramionami kąt prosty),przedramiona na całej długości wraz z łokciami powinny przylegać do poduszek oporowych.Przed rozpoczęciem ruchu robimy wdech ,a powietrze wydychamy podczas zbliżania ramion do siebie.W pozycji końcowej(ramiona najbliżej siebie)można wstrzymać ruch na 1-2 sekundy(dla większego napięcia mięśni).Ruch powrotny powinien odbywać się pod pełna kontrolą.\n" +
                        "\n" +
                        "W drugiej wersji tego ćwiczenia stosujemy nieco odmienną maszynę-różny jest również uchwyt(w tym wypadku drążki maszyny ujmujemy w dłonie) i ułożenie przedramion(pozostają w pozycji równoległej do podłoża, ramiona lekko ugięte w łokciach).\n" +
                        "\n" +
                        "UWAGI:W czasie trwania całego ruchu nie powinno wykonywać się żadnych ruchów tułowiem-plecy powinny dokładnie przylegać do oparcia. Jeżeli nie możemy zbliżyć ramion do siebie-powinniśmy zmniejszyć ciężar. Ćwiczenie to szczególnie polecane w okresie pracy nad definicją umięśnienia, kiedy wplatamy w nasze programy wiele ćwiczeń izolowanych.",
                image = getGifAsByteArray(context, R.raw.chest_12)
            ),

            Exercise(
                name = "KRZYŻOWANIE LINEK WYCIĄGU W STANIU",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Stajemy pomiędzy dwoma górnymi wyciągami( w tzw.”bramie”)-tułów lekko pochylony, co daje lepszą separację mięsni piersiowych. Chwytamy rączki wyciągów i ściągamy je do wewnątrz w dół. Do tego ćwiczenia należy używać umiarkowanych ciężarów i wykonywać pełen zakres ruchu. Wstrzymanie ruchu w końcowej fazie pozwoli otrzymać lepsze napięcie mięśni i poprawi wyrazistość szczegółów umięśnienia.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie to pozwala na uwidocznienie tak efektownych zarysów włókien mięśniowych(szczególnie wewnętrznej części klatki piersiowej)-bardzo dobre ćwiczenie w okresie przygotowań do zawodów. Należy unikać dodatkowych ruchów tułowia- pracują tylko ramiona i klatka.",
                image = getGifAsByteArray(context, R.raw.chest_13)
            ),

            Exercise(
                name = "WYCISKANIA POZIOME W SIADZIE NA MASZYNIE",
                targetMuscle = TargetMuscle.CHEST.name,
                description = "Ćwiczenie to można wykonywać na wiele sposobów, a uzależnione jest to od rodzaju maszyny i ustawienia oparcia.\n" +
                        "\n" +
                        "I tak przykładowo ćwiczenie zaprezentowane na gif-ie 14a. Jest ćwiczeniem bardzo zbliżonym do wyciskania sztangi na ławce poziomej-zaangażowana w nim jest głównie środkowa część mięsni piersiowych, wersja przedstawiona na gif-ie 14b.imituje ruch wyciskania sztangi na ławce skośnej(głową do góry),a z kolei ta na gif-ie 14c. Imituje ruch wyciskania sztangi na ławce pochyłej(głową w dół)-szczególnie polecana dla osób, które pragną rozbudować dolną sekcje mięsni piersiowych, ale nie są przekonane do ćwiczeń w pozycji głową w dół.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenia na maszynach( nie tylko tych do wyciskania w siadzie, ale także do rozpiętek i innych ćwiczeń na klatkę piersiową) nie wymagają takiej kontroli ciężaru, jak sztanga, czy sztangielki-polecane są więc osobom początkującym, które jeszcze nie nabrały wprawy w treningu wolnymi ciężarami, w celu wzmocnienia ścięgien i mięśni przed przystąpieniem do właściwych treningów na wolnych ciężarach. Maszyny również okazują się przydatne w okresie pracy nad definicją umięśnienia, kiedy wplatamy w nasze programy wiele ćwiczeń izolowanych.",
                image = getGifAsByteArray(context, R.raw.chest_14)
            ),

            // BACK
            Exercise(
                name = "PODCIĄGANIE NA DRĄŻKU SZEROKIM UCHWYTEM (NACHWYT)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Nie ma drugiego takiego ćwiczenia pod względem wszechstronności rozwoju mięsni grzbietu. Ćwiczenie to można wykonywać do karku i do brody, lecz wersja do karku jest mniej naturalna dla stawów. Chwytamy drążek nachwytem na szerokość taką, by po podciągnięciu ramiona z przedramionami tworzyły kąt prosty(w przybliżeniu).Nogi ugięte w kolanach(dla lepszej stabilności można je spleść).Łokcie pracują w płaszczyźnie pleców-w jednej linii. Wdech robimy przed rozpoczęciem ruchu podciągania-wydech dopiero, gdy jesteśmy już u góry. Ruch podciągania kończymy w momencie, gdy nasza broda(lub kark) jest na wysokości drążka lub nieco ponad nim. Opuszczamy się wolno i pod pełną kontrolą. Jeśli jesteśmy bardziej zaawansowani i możemy wykonać wiele powtórzeń w tym ćwiczeniu, to można zastosować dodatkowe obciążenie.\n" +
                        "\n" +
                        "UWAGI: Staramy się nie bujać tułowiem w trakcie ćwiczenia. Nogi nie powinny dotykać podłoża w czasie opuszczania-„kusi” to do wykonywania ruchów oszukanych. Koncentrujmy się na pracy mięśni grzbietu, trzeba „czuć” je przez cały czas trwania ćwiczenia. Mięśnie ramion staramy się, w miarę możliwości wyłączyć z pracy.",
                image = getGifAsByteArray(context, R.raw.back_1)
            ),

            Exercise(
                name = "PODCIĄGANIE NA DRĄŻKU W UCHWYCIE NEUTRALNYM",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Chwytamy specjalny uchwyt(może to być rączka trójkątna od wyciągu przerzucona nad drążkiem prostym)-dłonie równolegle do siebie w odległości ok.20-25cm.,palcami skierowane do siebie. Z pełnego zwisu podciągamy się do linii podmostkowej. Nogi zwisają luźno, lekko podkurczone w kolanach. Łokcie staramy się prowadzić wzdłuż tułowia. Opuszczamy się powoli-kontrolując swój ciężar. Jeśli jesteśmy bardziej zaawansowani i możemy wykonać wiele powtórzeń w tym ćwiczeniu, to można zastosować dodatkowe obciążenie.\n" +
                        "\n" +
                        "UWAGI: Staramy się nie bujać tułowiem w trakcie ćwiczenia. Nogi nie powinny dotykać podłoża w czasie opuszczania-„kusi” to do wykonywania ruchów oszukanych.",
                image = getGifAsByteArray(context, R.raw.back_2)
            ),

            Exercise(
                name = "PODCIĄGANIE NA DRĄŻKU PODCHWYTEM",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Chwytamy drążek prosty podchwytem. Nogi zwisają luźno, lekko podkurczone w kolanach. Ze zwisu przechodzimy do podciągania. Kończymy je, gdy nasza broda znajdzie się ponad drążkiem, a nasze ramiona będą w pełni ugięte w łokciach. Łokcie staramy się prowadzić wzdłuż tułowia. Opuszczamy się powoli-kontrolując swój ciężar. Jeśli jesteśmy bardziej zaawansowani i możemy wykonać wiele powtórzeń w tym ćwiczeniu, to można zastosować dodatkowe obciążenie.\n" +
                        "\n" +
                        "UWAGI: Staramy się nie bujać tułowiem w trakcie ćwiczenia. Nogi nie powinny dotykać podłoża w czasie opuszczania-„kusi” to do wykonywania ruchów oszukanych. Ważne jest, by wczuć się w prace mięsni grzbietu i zminimalizować pracę bicepsów.",
                image = getGifAsByteArray(context, R.raw.back_3)
            ),

            Exercise(
                name = "PODCIĄGANIE SZTANGI W OPADZIE(WIOSŁOWANIE)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy nad sztangą w rozkroku na szerokość barków, pochylamy tułów do pozycji prawie równoległej do podłoża, plecy w dolnym odcinku mocno ugięte do środka, nogi lekko ugięte w kolanach przez cały czas trwania ćwiczenia. Wdech bierzemy w momencie rozpoczęcia podciągania-wydech dopiero, gdy sztanga dochodzi do brzucha(lub do klatki). Sztangę chwytamy na szerokość nieco większą od barków i podciągamy ją do brzucha(łokcie prowadzimy na boki).Druga wersja zakłada podciąganie sztangi do klatki piersiowej(jest to ruch odwrotny do wyciskania sztangi na ławce- gif 4b.)-angażowane są mocniej w tej wersji mięśnie czworoboczne grzbietu, obłe większe, mniejsze i tylne aktony mięśni naramiennych. Ćwiczenie to można również wykonywać przy pomocy suwnicy Smitha. Opuszczamy ciężar z pełną kontrolą, wolnym tempem. Jeszcze inna wersja zakłada zastosowanie w tym ćwiczeniu podchwytu(można zastosować wtedy, dla lepszych efektów sztangę łamaną).Ta wersja z kolei mocniej angażuje dolne rejony ćwiczonych mięśni.\n" +
                        "\n" +
                        "UWAGI: Bardzo istotne w tym ćwiczeniu jest ułożenie pleców, a konkretnie ich wygięcie do środka w dolnym odcinku grzbietu. Częstym błędem-prowadzącym do kontuzji, jest tzw. „koci grzbiet”- czyli wyginanie w łuk grzbietu w trakcie ćwiczenia. Ciężar należy dobierać ostrożnie, tak by nie „przeszkadzał” nam w poprawnym wykonaniu ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.back_4)
            ),

            Exercise(
                name = "PODCIĄGANIE SZTANGIELKI W OPADZIE(WIOSŁOWANIE)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Typowo izolowane ćwiczenie na rozbudowę(szczególnie na „grubość” górnej i środkowej części)mięśni najszerszych. Wolną ręką opieramy się o coś stabilnego(może to być nasze kolano, ale lepsza do tego będzie ławka).Tułów w pozycji prawie równoległej do podłogi. W drugą rękę chwytamy sztangielkę-i tutaj dwie wersje ćwiczenia-(gif 5)1:chwyt przez cały czas trwania ćwiczenia równoległy-dłoń zwrócona palcami w kierunku ciała, łokieć pracuje wzdłuż tułowia-pracują mocniej górne i środkowe części mięsni najszerszych; 2: chwyt prostopadły do tułowia, dłoń zwrócona kciukiem w kierunku ciała, łokieć pracuje w bok od tułowia(pod kątem 90 stopni)-pracują mocniej górne części mięsni najszerszych. Podciąganie kończymy, gdy gryf sztangielki znajdzie się na równi z naszym barkiem lub odrobinę wyżej. Opuszczanie ciężaru kontrolowane.\n" +
                        "\n" +
                        "UWAGI: Jest to łatwiejsza(mniej obciążająca dolny odcinek grzbietu) wersja ćwiczenia ze sztangą.\n" +
                        "\n" +
                        "W tym ćwiczeniu (jak we wszystkich izolowanych)decyduje o skuteczności technika wykonania. Musimy przez cały czas wykonania ćwiczenia „czuć” pracę mięsni najszerszych.",
                image = getGifAsByteArray(context, R.raw.back_5)
            ),

            Exercise(
                name = "PODCIĄGANIE KOŃCA SZTANGI W OPADZIE",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy okrakiem nad gryfem sztangi(półsztangi)i chwytamy drążek, tułów z udami tworzą kąt prosty, a z podłogą nieco większy. Nogi lekko ugięte w kolanach. W takiej pozycji podciągamy sztangę do brzucha. Opuszczanie ciężaru kontrolowane. Wdech bierzemy w momencie rozpoczęcia podciągania-wydech dopiero, gdy sztanga dochodzi do brzucha. W zależności od kąta, pod jakim chcemy zaatakować mięśnie używamy w tym ćwiczeniu różnych drążków(chwytamy je różnym uchwytem).I tak np. może to być drążek sztangi typu „T” (gif 6-mocniej zaangażowane górne części mięsni najszerszych i mięśni obłych-łokcie prowadzimy w bok od tułowia.)lub drążek/rączka równoległa(gif 6a.-łokcie prowadzone wzdłuż tułowia-mocniej zaangażowane środkowe części mięsni najszerszych i obłych).Możne też ćwiczenie to wykonywać w leżeniu na ławce skośnej(mniej angażuje mięśnie dolnego odcinka grzbietu, mocniej izoluje mięśnie najszersze).\n" +
                        "\n" +
                        "UWAGI: Podobnie, jak przy wiosłowaniu zwykłą sztangą unikamy bezwzględnie tzw. ”kociego grzbietu ”-czyli wyginania pleców w łuk. Plecy powinny być ugięte do środka w dolnym odcinku, a tułów stabilny podczas trwania ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.back_6)
            ),

            Exercise(
                name = "PRZYCIĄGANIE LINKI WYCIĄGU DOLNEGO W SIADZIE PŁASKIM",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Siadamy płasko przed wyciągiem dolnym nogi zaparte o stabilny punkt oparcia i chwytamy rączkę wyciągu. Przyciągamy ją do brzucha, utrzymując przez cały czas tułów w pozycji pionowej. W końcowej fazie ruchu staramy się ściągnąć łopatki ku sobie. Po czym powoli, kontrolując ruch opuszczamy ciężar. Wdech przed rozpoczęciem przyciągania- wydech, gdy rączka jest przy brzuchu. Rączka może być różna(uchwyt w związku z tym również może być różny. Rączka równoległa(trójkątna-)pozwala na wykonanie ćwiczenia z uchwytem „młotkowym”. Angażuje on mięsnie górnej i środkowej części grzbietu. Rączka/drążek prosta/y pozwala na uchwyt nachwytem lub podchwytem, szeroko lub wąsko, co również angażuje pod różnym kątem mięsnie grzbietu. Nachwyt wąski i szeroki izoluje bardziej górną część mięsni grzbietu(szczególnie najszerszych i obłych), podchwyt wąski i szeroki angażuje mocniej środkową i dolną część tych mięśni. W ćwiczeniach szerokim uchwytem łokcie prowadzone są na boki, we wszystkich innych odmianach ćwiczenia prowadzone są przy tułowiu. Ćwiczenie to można wykonywać również jednorącz.\n" +
                        "\n" +
                        "UWAGI: Należy panować nad ciężarem. Nie wolno pozwolić, by pociągnął nas za sobą w przód-tułów w pozycji pionowej( z ewentualnymi minimalnymi odchyleniami, ale nie „bujaniem”).Opuszczanie ciężaru kontrolowane. Stałe napięcie w ćwiczonych mięśniach i „czucie” ich pracy.",
                image = getGifAsByteArray(context, R.raw.back_7)
            ),

            Exercise(
                name = "PRZYCIĄGANIE LINKI WYCIĄGU GÓRNEGO W SIADZIE",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Siadamy na podwyższeniu (np. ławce) przed wyciągiem górnym, chwytamy rączkę wyciągu i stabilnie zapieramy nogi. Ruch wykonujemy, przyciągając rączkę do klatki piersiowej lub brzucha, jednocześnie utrzymując tułów w pozycji pionowej, bez bujania. W końcowej fazie ruchu staramy się ściągnąć łopatki ku sobie, by dodatkowo zaangażować mięśnie grzbietu. Następnie powoli, kontrolując ruch, opuszczamy ciężar. Wdech wykonujemy przed przyciąganiem, wydech – gdy rączka dotyka klatki piersiowej lub brzucha.\n" +
                        "\n" +
                        "Warianty uchwytów: rączka trójkątna umożliwia chwyt „młotkowy”, angażujący mięśnie górnej i środkowej części grzbietu. Użycie prostego drążka pozwala na uchwyty nachwytem lub podchwytem – szeroko lub wąsko – co zmienia zaangażowanie różnych partii mięśniowych. Nachwyt wąski i szeroki intensywniej angażuje górną część mięśni grzbietu, natomiast podchwyt wąski i szeroki – bardziej środkową i dolną ich część. Ćwiczenie można także wykonać jednorącz.\n" +
                        "\n" +
                        "UWAGI: Należy cały czas panować nad ciężarem i unikać „bujania” tułowiem, zachowując kontrolę w trakcie opuszczania. Stałe napięcie i „czucie” pracy mięśni są kluczowe dla skuteczności ćwiczenia. Dla większego urozmaicenia treningu, można na zmianę stosować wersję na wyciągu dolnym i górnym lub na specjalnej maszynie.",
                image = getGifAsByteArray(context, R.raw.back_8)
            ),

            Exercise(
                name = "ŚCIĄGANIE DRĄŻKA/RĄCZKI WYCIĄGU GÓRNEGO W SIADZIE SZEROKIM UCHWYTEM (NACHWYT)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Siadamy na siodełku pod wyciągiem górnym, chwytamy rączkę/drążek nachwytem na szerokość taką, jak przy podciąganiu na drążku i przyciągamy ją do klatki(gif 9.) lub karku, w zależności od wersji, jaką wykonujemy. Obie wersje angażują te same mięsnie, ale pod nieco innymi kątami. Łopatki ściągamy do siebie, jednocześnie łokcie przywodząc do tyłu. Przy przyciąganiu do klatki tułów nieco odchylony do tyłu, a przy drugiej wersji\n" +
                        "(do karku)-tułów w pionie. W dolnym położeniu przytrzymujemy drążek na chwilę dla lepszego napięcia mięsni. Opuszczanie ciężaru kontrolowane. Stałe napięcie w ćwiczonych mięśniach i „czucie” ich pracy.\n" +
                        "\n" +
                        "UWAGI: Jest to ćwiczenie alternatywne dla podciągania na drążku w szerokim uchwycie, nachwytem. Szczególnie pomocne dla tych, którzy nie są w stanie wykonać ani jednego(lub są w stanie wykonać ich mało)poprawnego podciągnięcia. Wzmacnia mięśnie i rozbudowuje siłę do podciągania na drążku. Bardzo dobre również na koniec treningu grzbietu, dla rozciągnięcia i „dobicia” ćwiczonych mięśni. Bardzo ważne jest, by nie napinać bicepsów w trakcie wykonania ćwiczenia-pracują tylko mięśnie grzbietu.",
                image = getGifAsByteArray(context, R.raw.back_9)
            ),

            Exercise(
                name = "ŚCIĄGANIE DRĄŻKA/RĄCZKI WYCIĄGU GÓRNEGO W SIADZIE PODCHWYTEM",
                targetMuscle = TargetMuscle.BACK.name,
                description = "**Siadamy na siodełku pod wyciągiem górnym**, chwytamy rączkę podchwytem na szerokość podobną jak przy podciąganiu podchwytem. Następnie przyciągamy drążek do klatki (gif 10), skupiając się na angażowaniu dolnej i środkowej części mięśni najszerszych grzbietu oraz mięśni obłych. Ruch wykonujemy z odchyleniem tułowia do tyłu, kontrolując pozycję ciała i przyciągając łokcie do tyłu. W dolnym położeniu przytrzymujemy drążek na moment, aby zwiększyć napięcie mięśni. Opuszczanie ciężaru jest powolne i kontrolowane, utrzymując stałe napięcie na ćwiczonych mięśniach, co sprzyja lepszemu „czuciu” ich pracy.\n" +
                        "\n" +
                        "UWAGI: Jest to alternatywne ćwiczenie dla podciągania na drążku podchwytem, szczególnie przydatne dla osób, które mają trudność z wykonaniem podciągnięć lub mogą zrobić ich niewiele. Regularne wykonywanie ćwiczenia wzmacnia mięśnie grzbietu i buduje siłę potrzebną do podciągania na drążku. Bardzo ważne jest, aby w trakcie wykonywania ćwiczenia nie napinać nadmiernie bicepsów – główną pracę powinny wykonywać mięśnie grzbietu.",
                image = getGifAsByteArray(context, R.raw.back_10)
            ),

            Exercise(
                name = "ŚCIĄGANIE DRĄŻKA/RĄCZKI WYCIĄGU GÓRNEGO W SIADZIE UCHWYT NEUTRALNY",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Ćwiczenie wykonujemy, siedząc na siodełku pod wyciągiem górnym i chwytając drążek młotkowo, czyli tak, aby dłonie były skierowane do siebie. Przyciągamy drążek w kierunku klatki piersiowej, skupiając się na środkowej i dolnej części mięśni najszerszych oraz mięśniach obłych. W dolnej pozycji warto na moment zatrzymać ruch, aby dodatkowo napiąć mięśnie, a następnie powoli i kontrolowanie powrócić do pozycji wyjściowej, utrzymując stałe napięcie. W trakcie ćwiczenia należy pamiętać o zachowaniu prostego tułowia, unikając nadmiernego bujania i pracy innych mięśni.\n" +
                        "\n" +
                        "UWAGI: Jest to alternatywa dla podciągania na drążku przy uchwycie neutralnym/młotkowym, szczególnie pomocna dla osób, które nie mogą wykonać pełnych podciągnięć. Regularne wykonywanie tego ćwiczenia wzmacnia mięśnie grzbietu i przygotowuje do bardziej wymagających wariantów. Ważne, aby nie napinać nadmiernie bicepsów – główną pracę powinny wykonywać mięśnie grzbietu.",
                image = getGifAsByteArray(context, R.raw.back_11)
            ),

            Exercise(
                name = "PRZNOSZENIE SZTANGI W LEŻENIU NA ŁAWCE POZIOMEJ",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Jest to ćwiczenie podobne do przenoszenia sztangielki, jednak zastosowanie sztangi zmienia nieco kąt, pod jakim pracują ramiona, a co za tym idzie lepiej angażuje do pracy mięśnie grzbietu, przy jednoczesnym zmniejszeniu zaangażowania mięsni klatki piersiowej. Jeśli jednak decydujemy się na wykonanie ćwiczenia z użyciem sztangielki, to musimy pamiętać, by wykonywać je na ugiętych i ułożonych równolegle do tułowia(nie na boki, jak w ćwiczeniu na klatkę piersiową) ramionach. Pozwala to na lepsze zaangażowanie mięsni grzbietu. Przy wersji ze sztangą należy samemu zadecydować, jakie ułożenie ciała(wzdłuż, czy w poprzek)jest dla nas najlepsze. Można ćwiczenie to wykonywać z ramionami wyprostowanymi, lub(co zdecydowanie zmniejsza naprężenia w stawach łokciowych)na ugiętych ramionach(podchwytem i nachwytem). Ruch opuszczania jest bardzo istotny i powinien być wykonany z maksymalną koncentracją i pod pełną kontrolą ciężaru. Sztangę opuszczamy do pełnego rozciągnięcia mięsni grzbietu. Unoszenie kończymy, gdy ramiona znajdą się w pozycji pionowej do podłoża. Ćwiczenie można również wykonać zastępując sztangę rączką/drążkiem wyciągu dolnego znajdującego się za naszą głową lub przy pomocy specjalnej maszyny.\n" +
                        "\n" +
                        "UWAGI: Należy unikać „szarpania”- ruch powinien być płynny. Stałe napięcie mięsni grzbietu i „czucie” ich pracy jest podstawą uzyskania efektów w tym ćwiczeniu. Podczas opuszczania sztanga(sztangielka)nie powinna dotykać podłogi, zmniejszy to napięcie mięsni. Gdy jesteśmy wysoko zaawansowani i używamy do tego ćwiczenia sporych ciężarów, przyda się pomoc kogoś w utrzymaniu dolnej części ciała na ławce. Zbyt duże obciążenie użyte w tym ćwiczeniu może powodować niebezpieczne przeciążenia w stawach barkowych. Przy opuszczaniu sztanga nie powinna dotykać podłogi-powoduje to zanik napięcia w mięśniach.",
                image = getGifAsByteArray(context, R.raw.back_12)
            ),

            Exercise(
                name = "PODCIĄGANIE (WIOSŁOWANIE) W LEŻENIU NA ŁAWECZCE POZIOMEJ",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Jest to ćwiczenie podobne do wiosłowania w opadzie tułowia, ale odciąża ono dolny odcinek mięsni grzbietu-szczególnie polecane dla osób, które mają kłopoty z tą właśnie częścią. Technika podobna jak w ćwiczeniu w opadzie. Tułów oparty o ławkę poziomą. Łokcie przy ćwiczeniu ze sztangą(gif 13) prowadzimy w bok od tułowia, a w wersji ze sztangielkami wzdłuż tułowia(zaangażowanie mięśni analogicznie, jak przy ćwiczeniu w opadzie-z wyłączeniem pracy dolnego odcinka grzbietu). Ćwiczenie to można również wykonać na ławce skośnej. Zaangażowane będą te same mięsnie jednak pod innym kątem.\n" +
                        "\n" +
                        "UWAGI: Ruch kontrolowany, bez gwałtownych zrywów. Wysokość ławki powinna pozwalać na wyprostowanie rąk.",
                image = getGifAsByteArray(context, R.raw.back_13)
            ),

            Exercise(
                name = "SKŁONY ZE SZTANGĄ TRZYMANĄ NA KARKU (TZW. „DZIEŃ DOBRY”)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy w rozkroku nieco większym niż szerokość barków. Sztangę kładziemy na górnej części mięsni czworobocznych grzbietu. Głowa lekko wygięta do tyłu, ale bez przesady-zbytnie wyginanie głowy może być przyczyną kontuzji. Tułów wyprostowany, klatka piersiowa wypchnięta ku przodowi, łopatki ściągnięte do siebie. Nogi lekko ugięte w kolanach przez cały czas trwania ćwiczenia. Z takiej pozycji wykonujemy skłon do pozycji zbliżonej do poziomego ułożenia tułowia względem podłogi. Bez zatrzymania, ale nie szarpiąc unosimy tułów do pozycji wyjściowej. Ćwiczenie można wykonywać przy pomocy suwnicy Smitha. Ruch powinien być płynny i kontrolowany.\n" +
                        "\n" +
                        "UWAGI: Bardzo ostrożny dobór obciążenia jest bezwarunkowy w tym ćwiczeniu. Bardzo łatwo w nim o kontuzję, a najczęstszą przyczyną jest właśnie zbyt duże obciążenie. Należy unikać jakichkolwiek gwałtownych ruchów.",
                image = getGifAsByteArray(context, R.raw.back_14)
            ),

            Exercise(
                name = "UNOSZENIE TUŁOWIA Z OPADU",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Do wykonania tego ćwiczenia jest potrzebna specjalna ławka. Kładziemy się twarzą do dołu na ławce, tak by biodra spoczywały na niej, a nogi były zaparte o specjalną poprzeczkę. Ręce krzyżujemy na piersiach-jeśli wykonujemy ćwiczenie bez obciążenia. Jeśli używamy ciężaru-chwytamy sztangę(bądź krążek).Głowa w naturalnej pozycji, przy wyprostowanym karku. Ćwiczenie to można podzielić na trzy fazy-w zależności od pochylenia tułowia. Generalnie im głębszy skłon tym większe rozciągnięcie mięsni grzbietu, ale i większe zaangażowanie mięśni dwugłowych ud i pośladkowych. Ruch powinien być płynny, bez „szarpania” i zamaszystych ruchów, które powodują ominięcie najtrudniejszego punktu ćwiczenia. Ważne jest stałe napięcie mięsni i „czucie” ich pracy. Do wykonania tego ćwiczenia(w nieco innej pozycji) może służyć również specjalna maszyna.\n" +
                        "\n" +
                        "UWAGI: Ważna jest poprawna technika i ostrożność w doborze ciężaru. Dolny odcinek grzbietu jest bardzo podatny na kontuzje, więc zbyt duże ciężary nie są wskazane. Przez długi czas wystarcza jedynie ciężar naszego ciała i ewentualnie gryfu sztangi(nie olimpijski-może być zbyt ciężki).",
                image = getGifAsByteArray(context, R.raw.back_15)
            ),

            Exercise(
                name = "MARTWY CIĄG",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy przodem do sztangi, w rozkroku na szerokość barków lub nieco szerszym, nogi lekko ugięte w kolanach, gryf sztangi nad palcami stóp, sztangę chwytamy nachwytem, nieco szerzej niż barki. Klatka wypchnięta ku przodowi, tułów wyprostowany, głowa lekko zadarta do góry. Ćwiczenie polega na unoszeniu sztangi w górę poprzez prostowanie nóg i wyprost tułowia. Ruch kończymy przy pełnym wyproście tułowia-nie odchylamy go do tyłu-grozi to kontuzją. Nie wolno również dopuszczać do tzw. ”kociego grzbietu”, czyli wygięcia pleców w łuk(szczególnie w dolnym odcinku).Powrót do pozycji wyjściowej zaczynamy od lekkiego ugięcia nóg w kolanach, a następnie pochylamy tułów(oczywiście cały czas jest on wyprostowany)robiąc skłon. Nie pochylamy się jednak zbyt nisko. Ruch opuszczania ciężaru wolny i kontrolowany-sztanga nie uderza o podłogę, ale dotyka jej. Można ćwiczenie to wykonywać przy użyciu suwnicy Smitha. Dla zwiększenia zakresu ruchu stosuje się podkładki pod nogi.\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie raczej nie polecane początkującym. Zanim zacznie się je stosować w swoim treningu należy wzmocnić odpowiednio mięsnie grzbietu, a to wymaga czasu( i oczywiście treningu). Jeśli już decydujemy się na jego wykonanie, nie zapominajmy o wykonaniu 1-2 serii rozgrzewkowych.",
                image = getGifAsByteArray(context, R.raw.back_16)
            ),

            Exercise(
                name = "MARTWY CIĄG NA PROSTYCH NOGACH",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy w rozkroku nieco mniejszym od szerokości barków. Nogi minimalnie ugięte w kolanach-zupełnie proste nogi łatwo ulegają kontuzjom-szczególnie kolana. Uginamy się w pasie, przy jednoczesnym wygięciu do wewnątrz dolnego odcinka grzbietu. Staramy się, by sztanga prowadzona była przez cały czas blisko ciała. Im dalej do przodu wychylona sztanga, tym większe naprężenia w dolnych partiach grzbietu. Zalecane jest używanie pasa treningowego podczas wykonywania tego ćwiczenia. Dla lepszego rozciągnięcia mięsni grzbietu możemy zastosować podkładkę pod nogi, co zwiększa zakres ruchu. Ćwiczenie można wykonać również przy pomocy suwnicy Smitha. Bardzo dobre ćwiczenie dla tych, którzy dolne mięśnie grzbietu trenują na jednej sesji z mięśniami ud(szczególnie dwugłowymi).\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie raczej nie polecane początkującym. Zanim zacznie się je stosować w swoim treningu należy wzmocnić odpowiednio mięsnie grzbietu, a to wymaga czasu( i oczywiście treningu). Jeśli już decydujemy się na jego wykonanie, nie zapominajmy o wykonaniu 1-2 serii rozgrzewkowych.",
                image = getGifAsByteArray(context, R.raw.back_17)
            ),

            Exercise(
                name = "WZNOSY BARKÓW (SZRUGSY)",
                targetMuscle = TargetMuscle.BACK.name,
                description = "Stajemy w rozkroku na szerokość barków lub nieco węższym, nogi wyprostowane, ale nie zblokowane, głowa prosto, ramiona ściągnięte do tyłu, klatka wypięta do przodu. Ćwiczenie można wykonywać zarówno ze sztangą, jak i ze sztangielkami. Trenując sztangą można użyć większego ciężaru, ale ruch z użyciem sztangielek jest bardziej naturalny. Chwytamy sztangielki i unosimy barki możliwie jak najwyżej ściągając je jednocześnie do tyłu. W najwyższym punkcie wstrzymujemy ruch przez chwilę dla lepszego napięcia mięśni i opuszczamy barki do pozycji wyjściowej. Po chwili powtarzamy ruch.. Wdech robimy w momencie rozpoczęcia ruchu, powietrze wydychamy w najwyższym punkcie. Ćwiczenie można również wykonywać za pomocą wyciągu dolnego,na specjalnej maszynie,za pomocą suwnicy Smitha, lub w pozycji siedzącej.\n" +
                        "\n" +
                        "UWAGI: Nie powinno się rozluźniać mięśni pomiędzy powtórzeniami. Zalecana jest ostrożność przy doborze obciążenia-nadmierne może zmniejszyć zakres ruchu, co jest ze szkodą dla efektywności ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.back_18)
            ),

            // LEGS
            Exercise(
                name = "PRZYSIADY ZE SZTANGĄ NA BARKACH",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie to można wykonywać zarówno ze sztanga, jak i sztangielkami,przy pomocy suwnicy Smitha lub maszyny. Wchodzimy pod sztangę stojącą na stojakach, barki opuszczone i odwiedzione w tył, gryf sztangi dotyka naszego karku na mięśniach czworobocznych grzbietu(ich górnej części), dłonie rozstawione w wygodnej i stabilnej pozycji na gryfie, klatka wypchnięta ku przodowi, naturalna krzywizna kręgosłupa-dolny odcinek grzbietu wypchnięty do przodu, rozstaw stóp w zależności od naszego poczucia stabilności(zmiana rozstawu stóp powodować będzie atak na mięśnie pod innym kątem), najlepiej nieco szerzej, niż barki, całe stopy przylegają do podłogi, ciężar ma opierać się na piętach, głowa zadarta nieco do tyłu. Z tej pozycji nabieramy głęboko powietrza i ściągamy sztangę ze stojaków i rozpoczynamy ruch w dół(najlepiej przed tym wypuszczając powietrze po ściąganiu sztangi ze stojaków i nabierając go ponownie). Przez cały czas plecy wygięte w jednakowy sposób, pracują tylko nogi. Najlepiej, gdy przez cały czas biodra i pięty znajdują się w jednej linii, nie wypychamy kolan do przodu(nie powinny wysuwać się dalej niż końce palców stóp). Schodzimy w dół do momentu, gdy zanika kontrola pracy mięsni czworogłowych, a ich rozciągnięcie jest maksymalne. Rozpoczynamy powrót do pozycji wyjściowej, wypychając się piętami podnosimy się w gorę, jednocześnie wypychając biodra w przód, aż do pozycji startowej. Drugą wersją tego ćwiczenia są tzw. ”półprzysiady” lub „płytkie” przysiady, można je wykonać również na suwnicy Smitha(gif 1d). Różnica polega na mniejszym zakresie ruchu-opuszczamy się jedynie do momentu, gdy nasze nogi ugną się pod kątem równym lub nieco mniejszym, niż 90 stopni.\n" +
                        "\n" +
                        "UWAGI: Niezwykle ważne jest stopniowanie obciążenia w tym ćwiczeniu. Poza dokładna rozgrzewką poprzedzającą trening należy wykonać jeszcze 1-2 serie wstępne z mniejszym ciężarem.",
                image = getGifAsByteArray(context, R.raw.legs_1)
            ),

            Exercise(
                name = "PRZYSIADY ZE SZTANGĄ TRZYMANĄ Z PRZODU",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Podobna technika, jak przy zwykłych przysiadach. Mocniej jednak angażowane są mięśnie czworogłowe ud-spowodowane jest to pionową pozycją tułowia związaną z położeniem sztangi z przodu. Gryf spoczywa na przedniej części mięśni naramiennych i górnej części klatki piersiowej. Uchwyt na szerokość barków-jeżeli trzymamy sztangę podchwytem,(co jest może mniej wygodne, ale bezpieczniejsze)lub węższy-jeżeli trzymamy gryf nachwytem-ze skrzyżowanymi ramionami,(co jest mniej męczące dla nadgarstków, ale przy dużych ciężarach mniej bezpieczne)\n" +
                        "\n" +
                        "UWAGI: Niezwykle ważne jest stopniowanie obciążenia w tym ćwiczeniu. Poza dokładna rozgrzewką poprzedzającą trening należy wykonać jeszcze 1-2 serie wstępne z mniejszym ciężarem.",
                image = getGifAsByteArray(context, R.raw.legs_2)
            ),

            Exercise(
                name = "HACK-PRZYSIADY",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie różni się od zwykłych przysiadów ułożeniem sztangi(z tyłu za plecami, pod pośladkami w wyprostowanych rękach)i, co za tym idzie bardziej pionową pozycją tułowia w trakcie wykonania ćwiczenia. Ze względu na technikę wykonania i ułożenie sztangi ciężar, jaki użyjemy w tym ćwiczeniu będzie mniejszy, niż w zwykłych przysiadach. Pozycja wyjściowa to wyprostowany tułów, klatka wypchnięta ku przodowi, nogi w rozkroku na szerokość barków, ramiona wyprostowane wzdłuż tułowia, w dłoniach gryf sztangi(trzymany za plecami). Z tej pozycji wykonujemy przysiad do momentu, gdy nasze nogi będą ugięte pod kątem 90 stopni lub nieco mniejszym. Jednocześnie wypychamy kolana nieco do przodu, nie odrywając jednak stóp od podłoża-ciężar wypychamy z pięt. Ćwiczenie można również wykonać przy pomocy suwnicy Smitha.\n" +
                        "\n" +
                        "UWAGI: Jeżeli nie chcemy, by zmniejszył się zakres ruchu-należy nałożyć na sztangę krążki o małej ś#286acanicy-im większa ś#286acanica krążków-tym szybciej dotkną one podłogi",
                image = getGifAsByteArray(context, R.raw.legs_3)
            ),

            Exercise(
                name = "PRZYSIADY NA SUWNICY SKOŚNEJ(HACK-MASZYNIE)",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Stajemy na platformie suwnicy, plecy prosto, w dolnym odcinku wygięte do przodu, rozkrok na szerokość barków, stopy równolegle, w linii bioder lub nieco wysunięte ku przodowi, ale nie na tyle, by biodra odrywały się od powierzchni oparcia. Ramiona ugięte, dłonie na uchwytach suwnicy. Ważne jest, by ciężar był wypychany z pięt-nie z palców stóp. Z tej pozycji opuszczamy się wykonując przysiad do momenty, gdy nasze nogi będą ugięte pod kątem 90 stopni lub nieco mniejszym, ale nie tak głęboko, by utracić napięcie i kontrolę mięśni czworogłowych.\n" +
                        "\n" +
                        "UWAGI: Zbyt głębokie przysiady mogą być powodem ko0ntuzji-powoduja przeciążenia stawów kolanowych",
                image = getGifAsByteArray(context, R.raw.legs_4)
            ),

            Exercise(
                name = "SYZYFKI",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie to można wykonywać zarówno bez obciążenia, jak i z nim. Chwytamy wtedy w jedną rękę krążek i kładziemy go sobie na klatce, drugą ręką podpieramy się dla zachowania równowagi czegoś stabilnego. Ćwiczenie polega na wykonaniu przysiadu z jednoczesnym mocnym odchyleniem tułowia do tyłu i wspięciem na palce stóp połączonym z wypchnięciem kolan do przodu-dla lepszego rozciągnięcia mięśni górnej części ud i zwiększenia poziomu trudności ćwiczenia.\n" +
                        "\n" +
                        "UWAGI: Zalecany jest ostrożny dobór obciążenia-taki, by nie „przeszkadzał” w poprawnym wykonaniu ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.legs_5)
            ),

            Exercise(
                name = "PROSTOWNIE NÓG W SIADZIE",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Siadamy na siodle maszyny(ławki), dobrze gdy mamy oparcie-zapewnia ono lepszą stabilność tułowia, dłońmi chwytamy za uchwyt(lub krawędź) maszyny. Nogi ugięte w kolanach, zaparte o drążek maszyny na stopami(na wysokości kostek). Z tej pozycji wykonujemy ruch prostowania nóg do pełnego wyprostu w stawach kolanowych. W pozycji wyprostowanej zatrzymujemy ruch przez chwilę dla lepszego napięcia mięśni. Po czym powracamy do pozycji wyjściowej. Powrót w tempie wolnym i pod pełną kontrolą ciężaru. Powietrze nabieramy przed rozpoczęciem prostowania, wypuszczamy je, gdy kończymy prostowanie nóg.\n" +
                        "\n" +
                        "UWAGI: Jest to idealne ćwiczenie rozgrzewające mięśnie górnej części ud przed przysiadami. Należy unikać gwałtownych ruchów i ruchów zamaszystych, powodujących zanik napięcia w mięśniach i oszukanie w ćwiczeniu.",
                image = getGifAsByteArray(context, R.raw.legs_6)
            ),

            Exercise(
                name = "WYPYCHANIE CIĘŻARU NA SUWNICY(MASZYNIE)",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie to można wykonywać na różnego rodzaju suwnicach i maszynach, pod różnymi kątami-zmiana ułożenia ciała w ćwiczeniu powoduje, że atakujemy mięśnie pod różnymi kątami-bardziej wszechstronnie. Bez względu na rodzaj maszyny i kat ułożenia ciała, obowiązują wspólne zasady: siadamy na siedzisku, opierając stopy na platformie maszyny, ich rozstaw ok. szerokości ramion-całe stopy przylegają do platformy, nogi wyprostowane w kolanach,(ale nie zblokowane)-z tej pozycji nabierając powietrza rozpoczynamy powtórzenie. Trenujemy w tempie wolnym lub ś#286acanim, pełna kontrola ruchu i ciężaru przez cały czas, ruch kończymy w momencie, gdy nogi uginają w kolanach się pod kątem 90 stopni lub nieco mniejszym, z tego punktu wypychamy ciężar, jednocześnie wypuszczając powietrze.\n" +
                        "\n" +
                        "UWAGI: Bardzo istotne jest, by nie uginać nóg zbyt mocno, ponieważ grozi to kontuzją-powoduje powstawanie dużych nacisków na kręgi dolnej części grzbietu. Ważnym elementem jest także właściwe oddychanie-stabilizuje ono tułów i pomaga w wykonaniu ćwiczenia-nie powinno się wypuszczać powietrza zbyt szybko-jeszcze przed rozpoczęciem wypychania ciężaru. Nie blokujemy kolan w końcowej fazie ruchu-szkodzi to stawom kolanowym i zmniejsza napięcie mięśni.",
                image = getGifAsByteArray(context, R.raw.legs_7)
            ),

            Exercise(
                name = "UGINANIE NÓG W LEŻENIU",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie to wykonujemy na specjalnej maszynie, lub za pomocą wyciągu i specjalnym opasek na nogi. Kładziemy się na brzuchy tak, by poza ławkę wystawały jedynie podudzia poniżej kolan, nogi wyprostowane w kolanach, zaparte o drążek maszyny na wysokości ścięgien Achillesa(nad piętami), dłonie na uchwytach(lub krawędzi) maszyny. Z tej pozycji wykonujemy ruch maksymalnego uginania nóg w kolanach. W końcowym momencie uginania zatrzymujemy ruch przez chwilę dla lepszego napięcia mięśni. Po czym powracamy do pozycji wyjściowej. Powrót w tempie wolnym i pod pełną kontrolą ciężaru. Biodra(jak i reszta tułowia) przez cały czas trwania ćwiczenia przylegają do powierzchni ławki. Powietrze nabieramy przed rozpoczęciem uginania, wypuszczamy je, gdy kończymy uginanie nóg. Ćwiczenie to można wykonać również w pozycji stojąc(jednonóż)-na maszynie, lub wyciągu.\n" +
                        "\n" +
                        "UWAGI: Ważne jest, by nie odrywać bioder od powierzchni ławki podczas ćwiczenia-skraca to zakres ruchu i powoduje oszukany ruch. Należy unikać gwałtownych ruchów i ruchów zamaszystych, powodujących zanik napięcia w mięśniach i oszukanie w ćwiczeniu.",
                image = getGifAsByteArray(context, R.raw.legs_8)
            ),

            Exercise(
                name = "PRZYSIADY WYKROCZNE",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Pozycja wyjściowa taka, jak przy przysiadach zwykłych. Z tej pozycji stawiamy jedną noga krok do przodu(na tyle duży, by po wykonaniu przysiadu do momentu ugięcia nogi pod kątem 90 stopni-podudzia były prostopadle do podłogi, a uda równoległe), po czym wykonujemy na nodze wykrocznej przysiad, na tyle głęboki, by noga ugięła się pod kątem ok.90 stopni. Po czy wracamy do pozycji wyjściowej i wykonujemy to samo, ale drugą nogą. Noga nie ćwiczona w trakcie przysiadu lekko ugięta w kolanie, a w trakcie, gdy noga wykroczne jest wysunięta do przodu- czasowo przylega do podłogi tylko palcami(noga nie trenowana). Wdech robimy w momencie stawiania kroku, wydech-w trakcie wstawania z przysiadu. Do tego ćwiczenia można użyć zarówno sztangi(trzymanej na karku), jak i sztangielek(trzymanych w opuszczonych luźno obok tułowia rękach). Istnieje również bardzo podobne(pod względem zaangażowania mięśni) ćwiczenie-„PRZYSIAD ROZDZIELNY”- polegające na wykonywaniu przysiadów na nodze wykrocznej w wykroku. Wykonuje się kilka-kilkanaście przysiadów na jednej nodze, po czym zmienia się ćwiczoną nogę.\n" +
                        "\n" +
                        "UWAGI: Należy ostrożnie dobierać ciężar-jest to ćwiczenie angażujące mięsnie, które są podatne na kontuzje(np.przywodziciele). Stopa nogi wykrocznej musi przylegać przez cały czas przysiadu do podłogi całą powierzchnią. Ciężar wypychamy z pięt.",
                image = getGifAsByteArray(context, R.raw.legs_9)
            ),

            Exercise(
                name = "NOŻYCE",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Pozycja wyjściowa podobna do poprzedniego ćwiczenia, z tą różnicą, że wykrok robimy w tył, a przysiad wykonujemy na nodze zakrocznej, czyli tej, która pozostaje w miejscu. Z pozycji stojącej stawiamy jedną nogę krok do tyłu na tyle duży, aby noga zakroczna miała stabilne podparcie. Następnie na nodze zakrocznej wykonujemy przysiad, aż do kąta 90 stopni w kolanie. Noga wykroczna, która cofa się w tył, powinna opierać się o podłoże jedynie palcami, aby utrzymać równowagę, a kolano tej nogi powinno lekko uginać się przy ruchu.\n" +
                        "\n" +
                        "Do tego ćwiczenia można użyć sztangi trzymanej na karku, sztangielek trzymanych w opuszczonych rękach po bokach tułowia, lub wykorzystać suwnicę Smitha, która zapewnia dodatkową stabilność.\n" +
                        "\n" +
                        "UWAGI: Należy ostrożnie dobierać ciężar, ponieważ ćwiczenie angażuje mięśnie podatne na kontuzje (np. przywodziciele). Cała stopa nogi zakrocznej musi przylegać do podłogi przez cały czas przysiadu, a ciężar należy wypychać z pięty tej nogi.",
                image = getGifAsByteArray(context, R.raw.legs_10)
            ),

            Exercise(
                name = "WYSOKI STEP ZA SZTANGĄ/SZTANGIELKAMI",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Do ćwiczenia potrzebna będzie, poza obciążeniem mocna i stabilna ławka(lub wysoki podest).Stajemy w lekkim rozkroku, nieco węższym, niż barki, przed ławka(podestem)- pozycja, jak do przysiadów. Jako ciężar stosujemy sztangę trzymaną na barkach z tyłu, lub sztangielki trzymane w dłoniach, opuszczone luźno obok tułowia. Z tej pozycji wykonujemy wstępowanie na jednej nodze na ławkę, po czym dostawiamy drugą nogę i przez moment stoimy na ławce na wyprostowanych nogach i przy napiętych mięśniach. Powrotny ruch jest odwrotnością wstępowania-zstępowaniem. Powietrza nabieramy przed rozpoczęciem wstępowania na ławkę- wypuszczamy, gdy obie nogi znajdą się na ławce. Ważne jest, by każde powtórzenie zaczynać od innej nogi(raz lewą, raz prawą), gdyż właśnie noga, która wstępujemy na ławkę jako pierwszą, jest trenowana.\n" +
                        "\n" +
                        "UWAGI: Należy ostrożnie dobrać zarówno ławkę(podest), jak i adekwatny do jej budowy ciężar(oczywiście należy brać pod uwagę również ciężar naszego ciała). Co bardzo ważne!!!- nogę na ławce stawiamy całą powierzchnią stopy(nie samymi palcami).",
                image = getGifAsByteArray(context, R.raw.legs_11)
            ),

            Exercise(
                name = "ODWODZENIE NOGI W TYŁ",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Stajemy przed dolnym wyciągiem na taką odległość, by linka wyciągu była napięta już, gdy ćwiczona noga jest wysunięta do przodu, tułów pochylony do przodu, ramiona wyprostowane przed sobą, dłonie trzymają obudowy wyciągu, bądź innego stabilnego punktu(dla zachowania równowagi w ćwiczeniu). Trenowana noga połączona z linką wyciągu za pomocą specjalnej opaski nałożonej na nogę na wysokości kostki. Z tej pozycji wykonujemy ćwiczoną nogą ruch odwodzenia jej maksymalnie do tyłu, utrzymując ją przez cały czas trwania ćwiczenia wyprostowaną w kolanie(bądź minimalnie ugiętą-dla zmniejszenia napięcia w stawie kolanowym). Ruch odbywa się jedynie w stawie biodrowym. W pozycji maksymalnego odchylenia nogi do tyłu zatrzymujemy ruch przez chwilę dla lepszego napięcia mięśni. Po czym powracamy do pozycji wyjściowej. Powrót w tempie wolnym i pod pełną kontrolą ciężaru. Powietrze nabieramy przed rozpoczęciem odwodzenia, wypuszczamy je, gdy kończymy odwodzenie.\n" +
                        "\n" +
                        "UWAGI: Należy unikać gwałtownych ruchów i ruchów zamaszystych, powodujących zanik napięcia w mięśniach i oszukanie w ćwiczeniu.",
                image = getGifAsByteArray(context, R.raw.legs_12)
            ),

            Exercise(
                name = "ŚCIĄGANIE KOLAN W SIADZIE",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Ćwiczenie wykonujemy na specjalnej maszynie siedząc. Plecy oparte o oparcie maszyny, nogi ugięte w kolanach pod kątem prostym, oparte o poduszki maszyny. Z tej pozycji wykonujemy ruch łączenie ściągania nóg do wewnątrz, jak w celu złączenia ich ze sobą, pokonując jednocześnie opór maszyny. W pozycji maksymalnego ściągnięcia nóg zatrzymujemy ruch przez chwilę dla lepszego napięcia mięśni. Po czym powracamy do pozycji wyjściowej. Powrót w tempie wolnym i pod pełną kontrolą ciężaru. Powietrze nabieramy przed rozpoczęciem ściągania, wypuszczamy je, gdy kończymy ściąganie-nogi są w położeniu najbliższym sobie.",
                image = getGifAsByteArray(context, R.raw.legs_13)
            ),

            Exercise(
                name = "PRZYWODZENIE NÓG DO WEWNĄTRZ",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Stajemy przy wyciągu dolnym, zakładamy na nogę(na wysokości kostki)specjalną opaskę połączoną z linką wyciągu. Stajemy w takiej odległości od wyciągu, by ruch zaczynał się w momencie, gdy ćwiczona noga odchylona jest od pionu w kierunku wyciągu. Z tej pozycji wykonujemy przywodzenie nogi przed sobą, aż do momentu, gdy trenowana noga znajdzie się w pozycji odchylonej od pionu w kierunku przeciwnym do wyciągu. W końcowym momencie( maksymalne wychylenie nogi w górę, do wewnątrz) można zatrzymać ruch na moment, po czym wracamy do pozycji wyjściowej.",
                image = getGifAsByteArray(context, R.raw.legs_14)
            ),

            Exercise(
                name = "ODWODZENIE NÓG NA ZEWNĄTRZ",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Stajemy przy wyciągu dolnym, zakładamy na nogę(na wysokości kostki)specjalną opaskę połączoną z linką wyciągu. Stajemy w takiej odległości od wyciągu, by ruch zaczynał się w momencie, gdy ćwiczona noga odchylona jest od pionu w kierunku wyciągu. Z tej pozycji wykonujemy odwodzenie(nie wymachy, jak to ćwiczenie nazywane jest często)nogi w kierunku przeciwnym do wyciągu. W końcowym momencie( maksymalne wychylenie nogi w górę) można zatrzymać ruch na moment, po czym wracamy do pozycji wyjściowej.",
                image = getGifAsByteArray(context, R.raw.legs_15)
            ),

            Exercise(
                name = "MARTWY CIĄG NA PROSTYCH NOGACH",
                targetMuscle = TargetMuscle.LEGS.name,
                description = "Stajemy w rozkroku nieco mniejszym od szerokości barków. Nogi minimalnie ugięte w kolanach-zupełnie proste nogi łatwo ulegają kontuzjom-szczególnie kolana. Uginamy się w pasie, przy jednoczesnym wygięciu do wewnątrz dolnego odcinka grzbietu. Staramy się, by sztanga prowadzona była przez cały czas blisko ciała. Im dalej do przodu wychylona sztanga, tym większe naprężenia w dolnych partiach grzbietu. Zalecane jest używanie pasa treningowego podczas wykonywania tego ćwiczenia. Dla lepszego rozciągnięcia mięsni grzbietu i ud możemy zastosować podkładkę pod nogi, co zwiększa zakres ruchu.Ćwiczenie można wykonać również przy pomocy suwnicy Smitha. Bardzo dobre ćwiczenie dla tych, którzy dolne mięśnie grzbietu trenują na jednej sesji z mięśniami ud(szczególnie dwugłowymi).\n" +
                        "\n" +
                        "UWAGI: Ćwiczenie raczej nie polecane początkującym. Zanim zacznie się je stosować w swoim treningu należy wzmocnić odpowiednio mięsnie grzbietu, a to wymaga czasu( i oczywiście treningu). Jeśli już decydujemy się na jego wykonanie, nie zapominajmy o wykonaniu 1-2 serii rozgrzewkowych",
                image = getGifAsByteArray(context, R.raw.legs_16)
            ),

            // ABS
            Exercise(
                name = "SKŁONY W LEŻENIU PŁASKO",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Kładziemy się na materacu lub ławce. Nogi ugięte, ręce nad głową i unosimy tułów w górę. Pierwsza do góry unosi się głowa, potem barki, a na końcu reszta tułowia. Dla lepszego zaangażowania mięśni skośnych brzucha, w końcowej fazie unoszenia tułowia można wykonywać nim skręty. Jest to jednak wersja trudniejsza i bardziej narażająca na ewentualne kontuzje(mocniej obciąża dolne partie grzbietu).Nabieramy powietrza przed rozpoczęciem ruchu, a wypuszczamy je w trakcie unoszenia tułowia.\n" +
                        "\n" +
                        "UWAGI: Należy pamiętać o pełnym zakresie wykonywanego ruchu-zbytnie skracanie go poprzez nie opuszczanie głowy i barków do pozycji wyjściowej powodować może skracanie mięśni. Ruch płynny-bez szarpania. Opuszczanie wolniejsze lub tym samym tempem, co unoszenie.",
                image = getGifAsByteArray(context, R.raw.abs_1)
            ),

            Exercise(
                name = "SKŁONY W LEŻENIU GŁOWĄ W DÓŁ",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Wykonanie jak w ćwiczeniu poprzednim-płasko, ale pozycja wyjściowa jest głową w dół na ławce skośnej. Dla lepszego zaangażowania mięśni skośnych brzucha w końcowej fazie unoszenia tułowia można wykonywać nim skręty. Jest to jednak wersja trudniejsza i bardziej narażająca na ewentualne kontuzje(mocniej obciąża dolne partie grzbietu).\n" +
                        "\n" +
                        " UWAGI: Należy pamiętać o pełnym zakresie wykonywanego ruchu-zbytnie skracanie go poprzez nie opuszczanie głowy i barków do pozycji wyjściowej powodować może skracanie mięśni. Ruch płynny-bez szarpania. Opuszczanie wolniejsze lub tym samym tempem, co unoszenie.",
                image = getGifAsByteArray(context, R.raw.abs_2)
            ),

            Exercise(
                name = "UNOSZENIE NÓG W LEŻENIU NA SKOŚNEJ ŁAWCE",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Kładziemy się na ławce poziomej lub skośnej-głową do góry-, ramiona za głową(najlepiej jeśli trzymamy jakiś punkt oparcia np. ławkę lub drążek), tułów przylega do podłoża. Z tej pozycji unosimy nogi do klatki jednocześnie zginając je lekko w kolanach. Nabieramy powietrza przed rozpoczęciem ruchu, a wypuszczamy je w trakcie unoszenia nóg. Pod koniec unoszenia można skręcać nieco tułów i biodra dla zaangażowania mięśni skośnych brzucha.\n" +
                        "\n" +
                        "UWAGI: Unikamy gwałtownych i zamaszystych ruchów. Staramy się „wczuć” w pracę mięsni brzucha.",
                image = getGifAsByteArray(context, R.raw.abs_3)
            ),

            Exercise(
                name = "UNOSZENIE NÓG W ZWISIE NA DRĄŻKU",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Chwytamy drążek prosty nachwytem lub podchwytem, jeżeli mamy kłopot z dłuższym utrzymaniem się na drążku, możemy zastosować paski. Unosimy nogi jak najwyżej do brody. Można również w tym ćwiczeniu wprowadzić skręty tułowia w końcowej fazie unoszenia nóg, co mocniej zaangażuje do pracy mięśnie skośne brzucha. Jeszcze inna wersja(mocno angażująca mięśnie skośne)polega na jednoczesnym skręcie bioder wraz z unoszeniem nóg. Osoby zaawansowane mogą w tym ćwiczeniu używać dodatkowego obciążenia zamocowanego do nóg, ale tylko jeżeli czują się na siłach-łatwo „przedobrzyć” i nabawić się bolesnej kontuzji. Tempo ruchu umiarkowane, bez zrywów. Im mniejsze ugięcie nóg w kolanach, tym większy stopień trudności ćwiczenia, większe zaangażowanie mięśni zginaczy bioder(przy zmniejszeniu pracy mięsni brzucha) i większe napięcia w dolnym odcinku grzbietu.\n" +
                        "\n" +
                        "Łatwiejszą wersją tego ćwiczenia jest wykonanie go na drabinkach przyściennych, co daje oparcie dla pleców.\n" +
                        "\n" +
                        "UWAGI: Należy unikać bujania tułowiem i ruchów zamaszystych, zmniejszających napięcie mięśni.",
                image = getGifAsByteArray(context, R.raw.abs_4)
            ),

            Exercise(
                name = "UNOSZENIE NÓG W PODPORZE",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Stajemy plecami do specjalnej podpory, ramiona opieramy na poziomych poprzeczkach podpory, dłońmi chwytamy uchwyty, w tym momencie znajdujemy się już ponad podłogą. Z tej pozycji unosimy nogi w górę, w kierunku klatki piersiowej, jednocześnie uginając je w kolanach. Technika podobna, jak w unoszeniu nóg w zwisie, jednak mięśnie zaangażowane pod innym kątem. W tym ćwiczeniu również należy pamiętać o zachowaniu pełnego zakresu ruchu-zmniejszanie go prowadzi do skracania mięśni. Tempo ruchu umiarkowane, bez zrywów. Im mniejsze ugięcie nóg w kolanach, tym większy stopień trudności ćwiczenia, większe zaangażowanie mięśni zginaczy bioder(przy zmniejszeniu pracy mięsni brzucha) i większe napięcia w dolnym odcinku grzbietu.\n" +
                        "\n" +
                        "UWAGI: Unikamy ruchów zamaszystych-powodują zanik napięcia mięśni i przechodzenie przez najtrudniejszy punkt ćwiczenia ruchem oszukanym.",
                image = getGifAsByteArray(context, R.raw.abs_5)
            ),

            Exercise(
                name = "SPINANIE/UNOSZENIE KOLAN W LEŻENIU PŁASKO",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Kładziemy się na ławce lub materacu płasko, nogi wyprostowane, ramiona uniesione do góry nad głową(dla lepszej stabilizacji można chwycić nimi za jakiś punkt oparcia- np. ławkę) i z tej pozycji podciągamy kolana do klatki piersiowej. Ćwiczenie to można również wykonywać z dodatkowym obciążeniem w postaci linki wyciągu zahaczonej o nogi.\n" +
                        "\n" +
                        "UWAGI: Dodatkowy ciężar stosujemy dopiero wtedy, gdy możemy wykonać dużo powtórzeń bez niego. Zalecana ostrożność w doborze ciężaru.",
                image = getGifAsByteArray(context, R.raw.abs_6)
            ),

            Exercise(
                name = "SKŁONY TUŁOWIA Z LINKĄ WYCIĄGU SIEDZĄC",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Siadamy na ławce, tułów wyprostowany,(najlepiej z podporą pod plecy),za plecami mamy wyciąg górny(zamiast rączki zaczepiona lina z węzłami na końcach),chwytamy koniec liny(w ten sposób, że otacza nam z tyłu kark), z tej pozycji wykonujemy skłony w przód na taka głębokość, by nie odrywać dolnego odcinka pleców od oparcia, starając się przez cały czas utrzymać dolny odcinek grzbietu wyprostowany. Powrotny ruch kontrolowany i w wolnym tempie. Bardzo podobne działanie ma ćwiczenie wykonane na specjalnej maszynie.\n" +
                        "\n" +
                        "UWAGI: Wskazana duża ostrożność przy doborze ciężaru-zbyt duży przeciąża dolny odcinek kręgosłupa. Dłonie w jednakowej pozycji przez cały czas wykonania ćwiczenia. Staramy się przez cały czas „wczuwać” w pracę mięsni brzucha.",
                image = getGifAsByteArray(context, R.raw.abs_7)
            ),

            Exercise(
                name = "SKRĘTY TUŁOWIA",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Ćwiczenie to można wykonać zarówno w pozycji siedzącej, jak i stojącej-na maszynie lub, jeśli takiej nie posiadamy-za pomocą gryfu sztangi(tylko nie „olimpijskiej”- może być zbyt ciężki)zaawansowani mogą pozwolić sobie na użycie pewnego obciążenia, oczywiście z umiarem. Zbyt duże przeciąża dolny odcinek kręgosłupa. W pozycji stojącej- stajemy w rozkroku szerszym niż barki, gryf kładziemy na karku, ramiona oparte szeroko na gryfie. W pozycji siedzącej(na maszynie) chwytamy rączki maszyny, tułów wyprostowany przez cały czas wykonania ćwiczenia, nogi w jednakowej pozycji(ugięte w kolanach i skierowane do przodu-najlepiej, gdy są zaparte- dla lepszej stabilizacji tułowia)w trakcie całego ćwiczenia.\n" +
                        "\n" +
                        "UWAGI: Nie jest zalecane wykonywanie tego ćwiczenia ze zbyt dużym obciążeniem-musimy pamiętać, że jest to ćwiczenie bardziej aerobowe niż siłowe i zbytnie zaangażowanie niewłaściwych włókien mięśniowych może prowadzić do przerostu mięśni i dysproporcji w budowie sylwetki. Poza tym(a może przede wszystkim)zbyt duże ciężary mogą powodować kontuzje dolnego odcinka grzbietu. Bardzo istotne jest, by unikać ruchów zamaszystych-zanika wtedy napięcie mięśni-w związku z tym polecane wolne tempo wykonania ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.abs_8)
            ),

            Exercise(
                name = "SKŁONY TUŁOWIA Z LINKĄ WYCIĄGU KLĘCZĄC",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Do tego ćwiczenia znów potrzebna będzie specjalna lina za węzłami na końcach, zastępująca rączkę wyciągu,(jeśli takiej nie posiadamy można ćwiczenie wykonać trzymając rączkę wyciągu nad głową-zmieni się nieco położenie dłoni w ćwiczeniu na mniej wygodne).Klękamy przed wyciągiem górnym, biodra cofnięte do tyłu, chwytamy końce liny tak, by przebiegała ona nad głową, z tej pozycji wykonujemy skłony tułowia w przód pokonując opór wyciągu, jednocześnie napinając mięśnie brzucha. Bardzo istotne jest, by „czuć” właściwą pracę mięśni brzucha-tylko one wykonują prace. Unikamy ruchów ramionami(angażuje to do pracy mięsnie najszersze)-pozostają one w jednakowym położeniu, dłonie obok głowy(lub nad nią). Powrotny ruch kontrolowany i w wolnym tempie. Zalecany ostrożny dobór obciążenia-zbyt duże przeszkadza w poprawnym wykonaniu ćwiczenia i ponadto przeciąża dolne partie grzbietu.\n" +
                        "\n" +
                        "UWAGI: Unikamy gwałtownych ruchów i szarpania tułowiem. Pracują tylko mięśnie brzucha.",
                image = getGifAsByteArray(context, R.raw.abs_9)
            ),

            Exercise(
                name = "SKŁONY BOCZNE",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Stajemy w lekkim rozkroku(na szerokość barków, lub nieco szerzej),tułów wyprostowany, w jedną rękę chwytamy sztangielkę, drugą zakładamy sobie na głowę(dłonią). Z tej pozycji wykonujemy skłon w kierunku wolnej ręki, napinając mięśnie skośne brzucha. Oddech bierzemy w momencie rozpoczęcia ruchu, wypuszczamy powietrze w momencie maksymalnego skłonu. Powrót do pozycji wyjściowej może być z przekroczeniem linii pionu(tułowia)-zwiększa to napięcie mięśni skośnych. Ruch powinien być płynny i w wolnym tempie, bez gwałtownych szarpnięć. Ćwiczenie to można również wykonać przy pomocy wyciągu dolnego-zastępując nim sztangielkę.\n" +
                        "\n" +
                        "UWAGI: Zalecany ostrożny dobór obciążenia -musimy pamiętać, że jest to ćwiczenie bardziej aerobowe niż siłowe i zbytnie zaangażowanie niewłaściwych włókien mięśniowych może prowadzić do przerostu mięśni i dysproporcji w budowie sylwetki. Poza tym(a może przede wszystkim)zbyt duże ciężary mogą powodować kontuzje dolnego odcinka grzbietu. Bardzo istotne jest, by unikać ruchów zamaszystych-zanika wtedy napięcie mięśni-w związku z tym polecane wolne tempo wykonania ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.abs_10)
            ),

            Exercise(
                name = "SKŁONY BOCZNE NA ŁAWCE",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Do ćwiczenia będzie potrzebna ławka(płaska lub skośna-ta druga wydaje się być odpowiedniejsza) z drążkiem do zablokowania nóg. Kładziemy się bokiem na ławce tak, by od pasa w dół całe ciało przylegało do niej, a nogi blokujemy o specjalny drążek. Ramiona skrzyżowane na klatce, tułów w pozycji równoległej do podłoża. Z tej pozycji rozpoczynamy skłony tułowia do góry. Ruch kończymy w momencie maksymalnego wychylenia tułowia w górę. W pozycji końcowej wstrzymujemy na chwilę ruch-wzmaga to napięcie mięśni. Ruch powinien być płynny i w wolnym tempie, bez gwałtownych szarpnięć.\n" +
                        "\n" +
                        "UWAGI: Dobrze jest upewnić się przed wykonaniem ćwiczenia, czy powierzchnia ławki i nasze ubrania wzajemnie nie powodują ślizgania po powierzchni ławki-mogłoby to utrudnić wykonanie ćwiczenia i przyczynić się do powstania kontuzji. Pamiętajmy, by nie opuszczać się zbyt nisko(poniżej równoległego ułożenia tułowia względem podłogi)-przeciąża to dolny odcinek grzbietu. Niezmiernie ważna jest dbałość o płynność ruchu i unikanie wszelkich gwałtowności w ćwiczeniu- przeciąża to dolny odcinek grzbietu i grozi poważną kontuzją.",
                image = getGifAsByteArray(context, R.raw.abs_11)
            ),

            Exercise(
                name = "SKRĘTY TUŁOWIA W LEŻENIU",
                targetMuscle = TargetMuscle.ABS.name,
                description = "Kładziemy się na ławce skośnej, lub poziomej(bądź materacu-jeżeli wykonujemy ćwiczenie w poziomie), plecy i biodra przylegają do podłoża, ręce splecione na karku lub na klatce(gif).Z tej pozycji lekko unosimy barki i głowę, odrywając plecy od podłoża, jednocześnie skręcając tułów raz w jedną stronę, raz w drugą. Oddychanie, jak przy skłonach\n" +
                        "\n" +
                        "UWAGI: Stałe napięcie mięsni skośnych-„czucie” ich pracy-to podstawa efektywności tego ćwiczenia.",
                image = getGifAsByteArray(context, R.raw.abs_12)
            ),

            // CALVES
            Exercise(
                name = "WSPIECIA NA PALCE W STANIU",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Ćwiczenie to można wykonywać zarówno przy pomocy sztangi, suwnicy Smitha lub specjalnej maszyny. Można wykonywać je również bez obciążenia, a także jednonóż. Sztangę można również zastąpić sztangielką trzymaną w dłoni(po tej samej stronie, co ćwiczona noga: lewa noga- lewa ręka, prawa noga- prawa ręka). Istotnym elementem w tym ćwiczeniu jest użycie grubej podkładki pod palce stóp, która pozwala zwiększyć znacznie zakres ruchu, a co za tym idzie-poprawić efektywność ćwiczenia. Pozycja wyjściowa, to wyprostowany tułów i plecy, nogi wyprostowane w kolanach, rozkrok 25-30 cm, palce stóp(wraz ze stawami łączącymi je ze śródstopiem) na podkładce-mięsnie łydek rozciągnięte maksymalnie. Z takiej pozycji rozpoczynamy wspięcia. Ruch powinien być wolny i dokładny, ze stałym „czuciem” pracy mięśni. Należy unikać odbijania się pięt od podłogi.\n" +
                        "\n" +
                        "UWAGI: Przy użyciu sztangi zalecany jest ostrożny dobór obciążenia-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_1)
            ),

            Exercise(
                name = "WSPIĘCIA NA PALCE W SIADZIE",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Ćwiczenie to można wykonać za pomocą specjalnej maszyny, jak i przy użyciu sztangi. Są trzy wersje ćwiczenia-ze względu na ułożenie stóp-każda atakuje mięsnie pod innym katem:\n" +
                        "\n" +
                        "-         stopy ułożone równolegle,\n" +
                        "-         stopy ułożone palcami na zewnątrz,\n" +
                        "-         stopy ułożone palcami do wewnątrz,\n" +
                        "\n" +
                        "Podobnie, jak w ćwiczeniu stojąc-wskazane jest użycie grubej podkładki pod palce-dla zwiększenia zakresu ruchu. Pozycja siedząca, plecy wyprostowane, ramiona trzymają ciężar, który umiejscowiony jest na kolanach, rozstaw stóp 25-30 cm - mięśnie łydek rozciągnięte maksymalnie. Z takiej pozycji wykonujemy wspiecia. Ruch powinien być wolny i dokładny, ze stałym „czuciem” pracy mięśni. Należy unikać odbijania się pięt od podłogi.\n" +
                        "\n" +
                        "UWAGI: Zalecany jest rozsądny dobór ciężaru-taki, by nie „przeszkadzał” w poprawnym technicznie wykonaniu ćwiczenia, ani nie stał się powodem kontuzji-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_2)
            ),

            Exercise(
                name = "OŚLE WSPIĘCIA",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Ćwiczenie to wykonujemy przy pomocy specjalnej maszyny, lub z pomocą partnera. Pozycja wyjściowa to pochylony tułów równolegle do podłogi, wyprostowane w kolanach nogi, rozkrok 25-30 cm ramiona oparte z przodu o stabilny punkt-dla zachowania równowagi. Pod palce nóg gruba podkładka- mięśnie łydek rozciągnięte maksymalnie. Z tej pozycji wykonujemy wspięcia. Ruch powinien być wolny i dokładny, ze stałym „czuciem” pracy mięśni. Należy unikać odbijania się pięt od podłogi.\n" +
                        "\n" +
                        "UWAGI: Ważne jest, by ciężar był rozłożony w jednej linii ze stopami(jako przedłużenie nóg).Zalecany jest rozsądny dobór ciężaru-taki, by nie „przeszkadzał” w poprawnym technicznie wykonaniu ćwiczenia, ani nie stał się powodem kontuzji-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_3)
            ),

            Exercise(
                name = "WSPIĘCIA NA PALCE NA HACK-MASZYNIE",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Ćwiczenie to można wykonywać w pozycji tyłem do maszyny, jak również przodem do maszyny(o ile oczywiście dysponujemy maszyną ze specjalnymi oparciami na barki). Wskazane jest, jak w pozostałych ćwiczeniach na mięsnie łydek, grubej podkładki pod palce.\n" +
                        "\n" +
                        "UWAGI: Zalecany jest rozsądny dobór ciężaru-taki, by nie „przeszkadzał” w poprawnym technicznie wykonaniu ćwiczenia, ani nie stał się powodem kontuzji-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_4)
            ),

            Exercise(
                name = "WYPYCHANIE CIĘŻARU NA MASZYNIE/SUWNICY PALCAMI NÓG",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Jest to, jakby odwrotna wersja wspięć na Hack-maszynie- odwrotna jest pozycja-głowa znajduje się niżej nóg. Pozycja wyjściowa to siad na siedzisku maszyny/suwnicy, plecy oparte, nogi wyprostowane w kolanach, stopy dotykają do płaszczyzny maszyny/suwnicy tylko palcami i stawami łączącymi je ze sródstopiem, mięśnie łydek rozciągnięte maksymalnie. Z tej pozycji wypychamy ciężar siłą mięsni łydek.\n" +
                        "\n" +
                        "UWAGI: Zalecany jest rozsądny dobór ciężaru-taki, by nie „przeszkadzał” w poprawnym technicznie wykonaniu ćwiczenia, ani nie stał się powodem kontuzji-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_5)
            ),

            Exercise(
                name = "ODWROTNE WSPIĘCIA W STANIU",
                targetMuscle = TargetMuscle.CALVES.name,
                description = "Ćwiczenie podobne do wspięć na palce- różnica jest taka, że podkładki są pod piętami, a unosimy nie pięty, lecz śródstopia nóg. Pozycja taka, jak przy wspięciach na palce, ale nie rozciągamy mięśni łydek w początkowej fazie-tylko mięśnie piszczelowe. Ćwiczenie to można wykonywać ze sztangą, sztangielką(gif 6a)lub na specjalnej maszynie/\n" +
                        "\n" +
                        "UWAGI: Przy użyciu sztangi/sztangielki zalecany jest ostrożny dobór obciążenia-stawy skokowe są bardzo delikatne i podatne na kontuzje-zatem błędy w technice wykonania ćwiczenia skumulowane z dużym obciążeniem, mogą być nader niebezpieczne.",
                image = getGifAsByteArray(context, R.raw.calves_6)
            ),
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
