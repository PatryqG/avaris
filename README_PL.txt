AVARIS MANAGER - PROJEKT ANDROID

To jest projekt źródłowy aplikacji Android. Interfejs działa offline w WebView,
a dane są zapisywane lokalnie na urządzeniu. Dostępny jest eksport/import kopii JSON.

NOWOŚĆ: POWIADOMIENIA
- Możesz ustawić przypomnienia przy materiałach, wpisach kalendarza, eventach i zadaniach.
- Dostępne terminy: dzień wcześniej, godzinę wcześniej, w dniu terminu albo brak.
- Jeśli wpis w kalendarzu nie ma godziny, domyślną godziną terminu jest 09:00.
- Przypomnienia są natywnymi powiadomieniami Androida i mogą pojawić się po zamknięciu aplikacji.
- Harmonogram przypomnień jest odtwarzany po ponownym uruchomieniu telefonu.
- W zakładce Opcje kliknij „Włącz powiadomienia” i zezwól Androidowi na ich wysyłanie.
- Android/tryby oszczędzania baterii mogą opóźniać powiadomienia. Nie jest wymagane specjalne
  uprawnienie do alarmów dokładnych; system może więc dostarczyć przypomnienie z niewielkim opóźnieniem.

BUDOWANIE APK PRZEZ GITHUB (MOŻNA Z TELEFONU)
1. Utwórz repozytorium na GitHubie.
2. Wgraj CAŁĄ zawartość folderu AVARIS_Manager, razem z ukrytym folderem .github.
3. Otwórz zakładkę Actions w repozytorium.
4. Wybierz „Build AVARIS Manager APK” i kliknij „Run workflow”.
5. Po zakończeniu otwórz zadanie i pobierz plik z sekcji Artifacts: AVARIS-Manager-APK.
6. Rozpakuj pobrane archiwum i zainstaluj app-debug.apk na telefonie.

Workflow GitHub Actions jest już dołączony w .github/workflows/build.yml.
Nie musisz tworzyć go ręcznie.

BUDOWANIE LOKALNE (KOMPUTER)
1. Zainstaluj Android Studio, JDK 17 i Android SDK Platform 35.
2. Otwórz folder AVARIS_Manager w Android Studio.
3. Poczekaj na synchronizację Gradle.
4. Wybierz Build > Build Bundle(s) / APK(s) > Build APK(s).
5. APK znajdziesz w app/build/outputs/apk/debug/app-debug.apk.

Wymagania: Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17, Android SDK 35.


WERSJA 1.2 - LOGO I INTRO
- Logo AVARIS z dostarczonego pliku jest używane w nagłówku aplikacji i jako ikona launchera.
- Przy uruchomieniu pojawia się animowane intro: logo na czarnym tle, metaliczne czerwone wrota rozsuwają się, logo pozostaje widoczne, a następnie wrota zamykają się jak mechaniczne drzwi.
- Intro trwa około 3,7 sekundy i automatycznie odsłania panel aplikacji.
