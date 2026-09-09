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

## 7. UI / Design System — Serene Habit (2026-09-09 revizyonu)
Referans: Stitch SimpleHealth (reminders, today_s_nudge, manage_habits, your_journey) + çizgi-figür GIF stili.
Felsefe: "Digital Quiet" — yumuşak minimalizm, keskinlik yok, davetkâr etkileşimler.

Renk (Material3 lightColorScheme):
- background #F7FAF8 (surface), onBackground #181C1C (yumuşak kömür, saf siyah yok)
- surface #FFFFFF (kartlar), primary #4A654F (adaçayı), onPrimary #FFFFFF
- primary-fixed #CCEACF (ikon balonları), tertiary #8C4E35 (terracotta, streak/vurgu), tertiaryContainer #DC9073
- outline #737972, outlineVariant #C2C8C0, surfaceContainerLow #F1F4F2
Tipografi: başlıklar Quicksand 600 (indirilebilir font), gövde/etiket Inter 400/500/600. Ağır (Black) ağırlık yok.
Şekiller: kart 32dp radius + bordürsüz + yumuşak gölge; buton tam hap; toggle hap; progress 12dp kalın yuvarlak.
Yerleşim: tek sütun, kenar 24dp, bölümler arası geniş boşluk; alt navigasyon 4 sekme: Bugün / Hareketler / Geçmiş / Ayarlar.
Erişilebilirlik: min 48dp dokunma, reduce-motion'a saygı, dinamik yazı.

Ekranlar:
- Bugün (today_s_nudge): saate göre selamlama ("Günaydın/Tünaydın/İyi akşamlar. Bu saatin odağı."), ikon balonlu (açık yeşil daire) odak kartı + hap "YAPILDI" butonu, "BUGÜNÜN İLERLEMESİ x/y + streak + kalın progress bar" kartı.
- Hareketler (manage_habits): 11 kart (ikon balonu + ad + alt açıklama + toggle). Toggle = hatırlatma rotasyonuna dahil. Tercih Room'da saklanır (exercise_prefs).
- Geçmiş (your_journey): aylık özet kartı ("Bu ay X nazik hatırlatma"), gün-daireli ay takvimi (yeşil=tamam, terracotta=kısmi, gri=dinlenme), "nazik hatırlatma" alıntı kartı.
- Ayarlar (reminders): "Hatırlatma ritmi" kartı (saatlik / 2 saatte bir / özel aralık seçenekleri), "Sessiz saatler" kartı (açma-kapama + başlangıç-bitiş hap seçiciler), mesai saatleri korunur.
- Detay: GIF stili sahne (krem zemin, mürekkep çizgi figür, terracotta obje, adaçayı zemin şeridi) + adımlar + sayaç + Bitir.

Animasyon stili (11 Lottie, el yapımı, v5.7.4, 200x200, 30fps, 2sn loop):
- Zemin #F7FAF8 (şeffaf; ekran kartı beyazı üstünde sahne şeridi olarak krem panel çizilir), figür çizgileri #181C1C, objeler (sandalye/koltuk) #8C4E35 dolgu, zemin şeridi #8DAA91, vurgu #DC9073. Beyaz dolgu sadece göz akı gibi çerçeveli detaylarda.
- Eski geometrik set tamamen değiştirilir; dosya adları (animationAsset) aynı kalır.
- Yakın plan sahnelerde (göz/bilek/ayak bileği) zemin şeridi aranmaz; figür dili ve palet korunur.

## 7b. Davranış ekleri (revizyon)
- Egzersiz tercihi: exercise_prefs(exerciseId PK, enabled) — kapalı hareket rotasyona girmez; tümü kapalıysa rotasyon tüm listeye düşer (fail-safe).
- Ritmik seçenekler: interval 60 / 120 / özel (30/45/60/90/120).

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
