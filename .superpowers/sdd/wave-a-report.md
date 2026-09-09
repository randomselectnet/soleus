# Dalga A Raporu — ViewModel katmanı, nav back-stack, a11y, cleanups

## Commit
- `refactor: viewmodel layer, nav back-stack, a11y and cleanups` (HEAD, master üstüne)

## Test
- Komut: `.\gradlew.bat :app:testDebugUnitTest` → **BUILD SUCCESSFUL** (23 sn)
  - 8 test sınıfı, 19 test, 0 failure / 0 error
  - `DomainTest` 6 test (yeni `rotation_emptyList_throws` dahil, geçti)
- Komut: `.\gradlew.bat :app:assembleDebug` → **BUILD SUCCESSFUL**

## Kararlar
- **dueExercise boş-liste davranışı:** `require(exercises.isNotEmpty())` ile fail-fast
  `IllegalArgumentException` ("dueExercise: egzersiz listesi boş olamaz").
  Gerekçe: sessiz `""` döndürmek, çağıran tarafta (`HourlyReminderWorker`) sahte ID ile
  bildirim gösterme riski taşır; worker zaten boş listede erken döndüğü için `require`
  üretim akışını bozmaz. Test: `DomainTest.rotation_emptyList_throws`
  (`expected = IllegalArgumentException::class`).
- **ViewModel API** (`ui/ExerciseViewModel.kt`, `AndroidViewModel`):
  - `exercises: StateFlow<List<Exercise>>` — `ContentLoader.load` init'te bir kez
    (`viewModelScope` + `Dispatchers.IO`, `runCatching` ile boş-listeye düşer).
  - `streak: StateFlow<Int>` — `LogDao.recent(365)` + `streakFromLogs`.
  - `logCompletion(exerciseId, durationSec)` — `SessionLog` insert + streak refresh.
  - `refresh()` — ekrana dönüşte streak tazeleme (NavGraph ANA_SAYFA'dan çağrılır).
  - `AndroidViewModel` seçildi çünkü `ContentLoader`/`AppDb` için `Context` gerekli;
    saf `ViewModel` + factory ekstra kalabalık getirirdi.
  - NavGraph seviyesinde tek `viewModel()` örneği (Activity-scoped) tüm
    destination'lar arasında paylaşılır; Home/List/Detail'deki 3 ayrı
    `ContentLoader.load` + `LaunchedEffect` DAO okumaları + Detail `onDone` insert'i kalktı.
  - Yeni bağımlılık: `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6`.
- **Back-stack:** Home→KARSILAMA yönlendirmesine
  `popUpTo(ANA_SAYFA){inclusive=true}` eklendi (onboarding'e düşünce arkada
  sahipsiz home kalmaz, geri tuşu uygulamadan çıkar). KARSILAMA→ANA_SAYFA
  `popUpTo` korundu.
- **A11y:** Liste kartı `clickable`'ına `role = Role.Button` + açık
  `interactionSource`/`LocalIndication.current` (ripple + TalkBack "düğme" anonsu).
- **Clean:** `Exercise` → `data/model/Exercise.kt` (gerçek taşıma; `Entities.kt`'te
  sadece Room entity'leri kaldı). Import'lar güncellendi
  (ContentLoader, ExerciseListScreen, ExerciseDetailScreen).
  `SettingsDao.get(key: String = "main")` parametreli oldu; mevcut `get()` çağrıları
  (NavGraph, BootReceiver) varsayılan argümanla derlenmeye devam ediyor.

## Kapsam dışı / notlar
- Stats/Settings ekranlarındaki `LaunchedEffect` DAO okumaları bilerek bırakıldı
  (görev ViewModel'e taşınacak diye saymadı; istatistik 30-günlük pencere,
  ayarlar tekil okuma — ayrı ViewModel'ler Dalga B adayı).
