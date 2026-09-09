# Wave C2 Raporu — Gerçek Lottie Animasyonları, Parça 2 (Üst-Vücut + Göz/Nefes)

- Commit: `feat: real lottie animations part 2 upper body` (master, öncül `b925f6c`)
- Kapsam: 6 yeni Lottie dosyası + `AssetTest` genişletme (3 yeni test, Parça 1 testleri aynen korundu)

## Dosya adı eşleşmesi
Görevdeki açıklamalar `exercises_tr.json`'daki `animationAsset` yollarıyla birebir örtüşüyordu;
JSON'a dokunulmadı, dosyalar yollarla aynı adla yazıldı (`app/src/main/assets/lottie/` altında):

| Dosya (`animationAsset` ile birebir) | Egzersiz id |
|---|---|
| `lottie/neck-side-stretch.json` | `neck-side-stretch` |
| `lottie/shoulder-shrug-roll.json` | `shoulder-shrug-roll` |
| `lottie/scapular-squeeze.json` | `scapular-squeeze` |
| `lottie/seated-trunk-rotation.json` | `seated-trunk-rotation` |
| `lottie/wrist-forearm-stretch.json` | `wrist-forearm-stretch` |
| `lottie/eye-202020-breath-444.json` | `eye-202020-breath-444` |

## Animasyonlar (hepsi: v 5.7.4, 200x200, 30fps, op 60 = 2 sn seamless loop, ease in-out, mürekkep #1A1E1B + aksan #FF5C1A, isimli katmanlar, `parent` katman YOK)
1. **neck-side-stretch.json** (5 katman, 4.7KB): `Bas` katmanı boyun pivotunda (anchor 100,124) r −16°→+16°→−16° sağa-sola yatma; omuz/boyun statik, `Gerilim Cizgileri` opaklık nabzı.
2. **shoulder-shrug-roll.json** (6 katman, 5.3KB): `Omuz Bari` + `Omuz Noktalari` aynı dairesel yörüngede (t0 alt → t15 sağ → t30 üst/shrug → t45 sol → t60 alt); baş/boyun/gövde statik, `Roll Yayi` opaklık nabzı.
3. **scapular-squeeze.json** (5 katman, 5.5KB): `Sol/Sag Kurek` x 70→88 / 130→112 (t30'da birleşme) → geri; `Gogus Yay` scale 100→112, `Birlesme Vurgusu` + içe bakan oklar opaklık nabzı.
4. **seated-trunk-rotation.json** (6 katman, 6.6KB): `Govde` kalça pivotunda r −12°→+12° + derinlik hissi için scaleX 84↔100; `Kol Cizgisi` (kavuşmuş kollar) r −22°→+22°, `Bas` ters yönde ±8°, `Sol/Sag Ok` alterne opaklık.
5. **wrist-forearm-stretch.json** (5 katman, 4.6KB): `El` bilek pivotunda r −30° (yukarı) → +30° (aşağı) → −30°; `Parmak Nabzi` eli yay üzerinde takip eder (p) + yumruk aç-kapa scale nabzı (100↔65, döngüde 2 kez).
6. **eye-202020-breath-444.json** (5 katman, 6.1KB): `Nefes Halkasi` scale 80→118 + fade (nefes), `Ic Nefes Halkasi` ters faz; `Goz` katmanında beyaz + aksan bebek (bebek x 93→107 uzak bakış, shape-level animasyon) ve scaleY ile döngüde 2 kırpma (t27, t57'de 8%); `Uzak Odak Noktasi` + `Bakis Yonu` 20-20-20'yi anlatır.

## Doğrulama
- JSON parse + şema denetimi (Python): 10/10 dosya OK (v, layers, fr=30, op=60, 200x200).
- Seamless-loop denetimi (tüm animasyonlu keyframe listelerinde ilk `s` == son `s`): 10/10 OK.
- `parent` katman denetimi: 6/6 yeni dosya temiz (Parça 1'deki `standup-mini-set.json`'da `parent` var — bilinçli, C1'de belgelendi).
- Boyut: en büyük yeni dosya 6.6KB (< 100KB sınırının çok altı).
- `.\gradlew.bat :app:testDebugUnitTest` → BUILD SUCCESSFUL; 8 suite, 25 test, 0 failure/error (AssetTest 7/7: 4 Parça-1 + 3 yeni Parça-2).
- `.\gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL.
- `AssetTest` yeni testleri: `lottie_part2_upperBody_exists`, `lottie_part2_validStructure` (`"v"` + `layers:[{` + <100KB), `lottie_part2_referencedByContent` (6 dosyanın `exercises_tr.json`'da referanslı olduğu).

## Concerns / sonraki dalgaya notlar
- Tüm 11 egzersizin `animationAsset` dosyası artık mevcut; `ExerciseDetailScreen`'de 11 animasyonun emülatör/cihazda görsel onayı hâlâ yapılmadı — sonraki dalga ekran görüntüleriyle onaylamalı.
- Ara düzeltme notu: yazım sırasında animasyonlu prop'un `ks`'yi kapattığı yerlerde bir fazla `}` üretilmiş; Python parse denetimiyle yakalanıp düzeltildi. El ile Lottie yazan sonraki dalga aynı denetimi (`ConvertFrom-Json` veya yukarıdaki seamless/`parent` betiği) uygulamalı.
- `AssetTest` düzenlemesi ilk denemede `lottie_part1_referencedByContent` testini yanlışlıkla silmiş; test sayısı 24'e düşünce fark edilip geri eklendi (final 25). Parça-1 testleri finalde aynen mevcut.
