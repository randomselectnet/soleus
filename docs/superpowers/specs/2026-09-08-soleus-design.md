# Soleus / Ofis Egzersiz Reminder - Tasarım Spec
Tarih: 2026-09-08
Durum: Taslak - kullanıcı onayı bekliyor
Kapsam: MVP = Takip ve motivasyon, Platform = Android önce (Native)

## 1. Amaç ve Başarı Kriteri
Ofiste sürekli oturanların saat başı kısa, sessiz, oturarak yapılabilir hareketler yapmasını sağlamak.
Başarı: kullanıcı mesai saatleri içinde saatlik bildirimi alır, 60-120 sn'lik hareketi yapar, "yapıldı" işaretler, streak korunur.

## 2. Kapsam (MVP)
Var:
- 11 egzersiz kartı (TR anlatım + süre/tekrar + fayda + dikkat notu + Lottie/Rive animasyonu)
- Saatlik reminder (mesai saatleri içinde, ayarlanabilir)
- Yapıldı takibi, günlük hedef, streak, haftalık basit istatistik
- Offline-first, gömülü içerik JSON
Yok (V2):
- iOS, hesap/bulut senkron, sosyal, video-stream, akıllı sensör algılama

## 3. Egzersiz İçeriği (V1)
1. Soleus push-up - topuk kaldır-bırak, 2 dk / tükenene kadar
2. Ayak bileği çevirme + calf raise - 10 tur/yon + 15 tekrar
3. Kalça sıkma - 5 sn x10
4. Oturarak diz uzatma - 10/side, 3 sn tut
5. Boyun lateral esnetme - 20 sn/side
6. Omuz silkme + roll - 10 + 10 daire
7. Scapular squeeze / göğüs açma - 10 sn x5
8. Oturarak gövde rotasyonu - 20 sn/side
9. Bilek + önkol esnetme - 20 sn/yon + 15 yumruk aç-kapa
10. 20-20-20 göz + 4-4-4 nefes - 60 sn
11. Kalkınca mini set - 10 squat veya masa push-up + 1 dk yürüme
Her kayıt: id, trName, howToSteps[], durationSec, benefit, caution, animationAsset.

## 4. Yaklaşım Kararı
A - Native Android (Kotlin + Jetpack Compose) seçildi.
Neden: WorkManager bildirim güvenilirliği, düşük APK, Android-önce hedefiyle uyumlu.
Reddedilen: B-Flutter (sonra iOS bedava ama bildirim kırılganlığı), C-PWA (arka plan reminder çalışmaz).

## 5. Mimari
Single-Activity Compose, MVVM + Clean, manuel DI.
- data: Room (Exercise, SessionLog, ReminderSettings) + ContentJson loader
- domain: GetDueExerciseUseCase (rotasyon), LogCompletionUseCase (streak), ShouldRemindUseCase
- ui: Home, ExerciseList, ExerciseDetail, Stats, Settings, Onboarding(izin)
- worker: HourlyReminderWorker (WorkManager periodic) + NotificationHelper
- nav: Navigation-Compose

## 6. Veri Akışı
ReminderSettings(workStart=09:00, workEnd=18:00, intervalMin=60, quietEnabled) -> WorkManager -> rotasyondan o saatin egzersizini seç -> Notification ("Soleus push-up zamanı - 2 dk") -> tap -> Detail -> Başlat(60 sn timer + animasyon) -> Bitir -> SessionLog(timestamp, exerciseId, durationDone) -> streak/günlük hedef güncellenir.

## 7. UI / Frontend-Design Yönü
Ton: editorial calm + kinetic playful. Klinik mavi/beyaz yok.
Renk: kağıt #FAF6EF, mürekkep #1A1E1B, aksan soleus turuncusu #FF5C1A.
Tipografi: display serif (örn. Fraunces) + body humanist sans (örn. Instrument Sans). Inter/Roboto/Space Grotesk yok.
İmza detayı: nefesle büyüyen hareket halkası timer + staggered kart girişi.
Ekranlar: Bugün / Hareketler / İstatistik / Ayarlar.
Erişilebilirlik: min 44dp dokunma, dinamik yazı boyutu, animasyonu azalt ayarına saygı.

## 8. Bildirim ve İzin Hataları
- POST_NOTIFICATIONS izni onboarding'de istenir, reddedilirse in-app banner + Ayarlar deep-link.
- Pil optimizasyonu / Doze: WorkManager + exact olmayan tetik kabul edilir, kaçan hatırlatma bir sonraki saate sarkmaz, atlanır.
- Cihaz yeniden başlatma: BOOT_COMPLETED receiver ile worker yeniden planlanır.

## 9. Test Planı
- Unit (JUnit): rotasyon sırası, streak hesabı (gün atlama, aynı gün çoklu log), mesai-dışı bastırma.
- Instrumented: Room migration, Worker tetik + bildirim içeriği, izin reddi akışı.
- Manuel: 1 günlük mesai simülasyonu, Türkçe metin kontrolü, animasyon boyut/perf kontrolü (<2MB/animasyon hedefi).

## 10. Dosya / Modül Taslağı
app/src/main/java/com.soleus.office/
- MainActivity.kt, SoleusApp.kt (manuel DI)
- data/db/{Entities, Dao, AppDb}, data/ContentLoader.kt
- domain/{GetDueExercise, LogCompletion, StreakCalc}.kt
- ui/{home, list, detail, stats, settings, onboarding}
- worker/{HourlyReminderWorker, NotificationHelper, BootReceiver}
- assets/lottie/*.json, assets/content/exercises_tr.json

## 11. Açık Sorular (V1'i bloklamaz)
- Animasyon lisansları: hazır Lottie mi, özel Rive mi?
- Interval sabit 60 mı, kullanıcı 30/45/60/90 seçsin mi? (Öneri: seçsin, varsayılan 60)
- Uygulama adı ve ikon: Soleus mu?

## 12. Sonraki Adım
writing-plans skill ile implementasyon planı çıkarılacak. Kod yazımı plan onayından sonra.
