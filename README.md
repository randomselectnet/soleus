# Soleus — Ofis Hareket Hatırlatıcısı

Soleus, masa başında çalışanlar için kısa ofis egzersizleri sunan ve düzenli
hareket hatırlatmaları gönderen bir Android uygulamasıdır. 11 hareketlik Türkçe
içerik, Lottie animasyonları, günlük seri (streak) takibi ve mesai saatlerine
uyumlu saatlik bildirimlerle birlikte gelir.

## Özellikler

- **11 Türkçe ofis egzersizi:** her biri adım adım anlatım, süre, fayda ve dikkat
  notu içerir (`app/src/main/assets/content/exercises_tr.json`).
- **Lottie animasyonları:** her harekete özel, el yapımı ve döngülü vektör
  animasyonlar (`app/src/main/assets/lottie/`).
- **Saatlik hatırlatmalar:** WorkManager tabanlı bildirimler; mesai başlangıç/bitiş
  ve 30/45/60/90 dk sıklık ayarlarına uyar, mesai dışında sessiz kalır.
- **Derin bağlantı:** bildirime dokununca ilgili hareketin detay ekranı açılır
  (`soleus://detail/{id}`).
- **Günlük seri ve istatistik:** tamamlanan seanslar Room'da saklanır; streak ve
  son 7 günün bar grafiği gösterilir.
- **Erişilebilirlik:** 48 dp dokunma hedefleri, içerik açıklamaları, sistem
  serif/sans tipografisi.

## Ekranlar

| Rota | Açıklama |
|---|---|
| Karşılama (`onboarding`) | İlk açılışta bildirim izni ve tanıtım |
| Ana sayfa (`home`) | Sıradaki hareket + streak özeti |
| Liste (`list`) | 11 hareketin listesi |
| Detay (`detail/{id}`) | Animasyon, adımlar, süre; "tamamla" kaydı |
| İstatistik (`stats`) | Streak sayısı + son 7 gün bar grafiği |
| Ayarlar (`settings`) | Mesai saatleri + hatırlatma sıklığı |

## Mimari

- **UI:** Jetpack Compose + Material3, Navigation Compose.
- **Durum yönetimi (MVVM):** `ExerciseViewModel` (liste + streak),
  `StatsViewModel` (streak + haftalık sayılar), `SettingsViewModel`
  (ayar durumu + kaydetme). Ekranlar saf UI'dır; veri okuma/yazma
  ViewModel içindedir.
- **Veri:** Room (`session_log`, `reminder_settings`), `ContentLoader`
  ile asset'ten JSON içerik yükleme.
- **Arka plan:** `HourlyReminderWorker` (WorkManager) + `NotificationHelper`
  (bildirim kanalı ve gönderimi), `BootReceiver` (yeniden başlatmada
  zamanlamayı kurar).
- **Tema:** kağıt zemin (`#FAF6EF`), mürekkep metin (`#1A1E1B`), turuncu
  aksan (`#FF5C1A`) — Compose (`ui/theme/Theme.kt`) ve `res/values/colors.xml`
  (bildirim rengi) tarafında aynı palet.

## Derleme

Gereksinim: JDK 17, Android SDK (compile/target 34, min 26).

Hata ayıklama APK'sı:

```bat
.\gradlew.bat :app:assembleDebug
```

Sürüm APK'sı (küçültme/kaynak kırpma V1 kararıyla kapalıdır):

```bat
.\gradlew.bat :app:assembleRelease
```

## Test

```bat
.\gradlew.bat :app:testDebugUnitTest
```

Birim testleri; içerik bütünlüğü, alan hesapları, worker mantığı, ayar
doğrulama ve paket adı kontrollerini kapsar.

## Lisans

Tüm hakları saklıdır.
