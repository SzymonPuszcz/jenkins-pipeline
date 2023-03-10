# Jenkins CI


## Konfiguracja Jenkinsa
1. Uruchom Jenkinsa za pomocą komendy `docker run -p 9090:8080 -p 50000:50000 --name jenkins --restart=on-failure jenkins/jenkins:lts-jdk11`
2. Skopiuj hasło z konsoli
3. Wejdź na http://localhost:9090 i wklej hasło
4. Zainstaluj sugerowane wtyczki
5. Stwórz użytkownika admin

## Instalacja pluginów

### Instalacja pluginu LOCALE
1. Wejdź na stronę główną Jenkinsa i przejdź do zakładki _Zarządzaj Jenkinsem_ w lewym bocznym pasku.x
2. W sekcji _Konfiguracja systemu_ przejdź do _Zarządzaj wtyczkami_.
3. Zmień zakładkę na _Available plugins_, znajdź plugin `Locale`, zaznacz go i naciśnij przycisk _Pobierz teraz i zainstaluj po restarcie_.
4. Poczekaj na pobranie i zaznacz checkbox _Uruchom ponownie Jenkinsa, gdy instalacja się zakończy i żadne zadanie nie będzie wykonywane_
5. Zaczekaj, aż Jenkins zainstaluje plugin i wykona restart.
6. Wejdź ponownie w zakładkę _Zarządzaj Jenkinsem_, następnie w _Skonfiguruj system_ i odnajdź sekcję z nagłówkiem **Locale**. Zaznacz checkbox _Ignore browser preference and force this language to all users_ i naciśnij przycisk _Zapisz._ Po wszystkim Jenkins powinien być w języku angielskim.

### Instalacja pluginu JDK Parameter Plugin
W analogiczny sposób do powyższego zainstaluj plugin `JDK Parameter Plugin`. Będzie on nam potrzebny do ustawienia konkretnej wersji JDK podczas budowania aplikacji.

## Konfiguracja mavena i JDK
1. Wejdź w _Manage Jenkins_ i w sekcji _System Configuration_ przejdź do _Global Tool Configuration_.
2. Znajdź sekcję **JDK**. 
   1. Następnie naciśnij przycisk **Add JDK**. W pole **Name** wpisz _17_, zaznacz checkbox _Install automatically_.
   2. Wybierz z combo boxa _Add Installer_ wartość **Extract \*.zip/*.tar.gz.**
   3. Wpisz url `https://download.oracle.com/java/17/archive/jdk-17.0.6_linux-x64_bin.tar.gz`.
   4. Wpisz w polu _Subdirectory of extracted archive_ wartość `jdk-17.0.6`. Ze względu na tą konkretną paczkę JDK niezbędne jest podanie folderu, do którego zostały spakowane wszystkie pliki, stąd ta wartość.
3. Naciśnij przycisk **Save** znajdujący się na dole.
4. Wejdź ponownie w _Global Tool Configuration_ i znajdź sekcję **Maven** na samym dole strony.
    * Naciśnij _Add Maven_.
    * W pole _Name_ wpisz `3.8.7`.
    * Z combo boxa znajdującego się poniżej wybierz wersję `3.8.7`.
5. Naciśnij przycisk **Save** znajdujący się na dole.


## Konfiguracja CI projektu
1. Wejdź na stronę główną Jenkinsa.
2. Naciśnij **New Item**.
3. Wprowadź nazwę `bookstore`.
4. Z poniższych opcji wybierz _Multibranch Pipeline_ i naciśnij **OK**.
5. Jesteś teraz na stronie konfiguracji projektu. Wpisz ponownie nazwę `bookstore`.
6. W kolejnej sekcji **Branch Sources** wybierz `Git` z combo boxa. Repozytorium, którego będziemy korzystać nie jest publiczne stąd niezbędna jest autoryzacja podczas jego pobierania. 
   1. Naciśnij przycisk **Add** i wybierz **Jenkins**.
   2. W nowo otwartym dialogu wpisz swój login do githuba w polu _Username_ i hasło w polu _Password_.
   3. Naciśnij `Add` na dole dialogu.
7. Wpisz adres repozytorium `https://github.com/infoshareacademy/jjdzr7-materialy-jenkins` w polu _Repository HTTPS URL_.
8. W celu uniknięcia budowania wszystkich branchów (nasze maszyny mogłyby trochę dostać w kość przy budowie 10 projektów na raz :) ) zastosujemy pewien trik. W następnych zadaniach każdy z was będzie pracować na osobnym branchu, który będzie zawierał wasze imię i pierwszą literę nazwiska. 
   1. Pod sekcją _Credentials_ z poprzedniego punktu znajduje się sekcja _Behaviours_. Naciśnij przycisk **Add** i wybierz `Filter by name (with regular expression)`.
   2. W polu _Regular expression_ wpisz `(main|.*{twoje imię}{pierwsza litera nazwiska}.*)`, czyli w moim przypadku byłoby to `(main|.*szymonp.*)`. Takie wyrażenie regularne ogranicza Jenkinsa do branchów: main oraz tych zawierających twoje imię i pierwszą literę nazwiska
9. Przejdź do dalszej sekcji _Build Configuration_. Jest tutaj wskazane, w jaki sposób będzie skonfigurowany cały pipeline. Konfiguracja wskazuje na plik `Jenkinsfile`, który znajduje się w repozytorium. Możesz teraz obejrzeć ten plik, a następnie wrócić do Jenkinsa.
10. Następną sekcją jest _Scan Multibranch Pipeline Triggers_. Ze względu na operowanie na lokalnym środowisku, Github nie jest w stanie informować o nowych commitach tak jak dzieje się to w produkcyjnym środowisku. Z tego powodu zaznacz checkbox _Periodically if not otherwise run_ i ustaw interwał na 2 minuty. Ta opcja uruchamia okresowe sprawdzanie przez Jenkinsa czy pojawiły się nowe branche lub nowe commity i w ten sposób niejako obejdziemy brak komunikacji Github-->nasze lokalne środowisko.
11. Naciśnij przycisk **Save**.
12. Projekt został poprawnie skonfigurowany. Teraz Jenkins dokonuje jego skanowania w celu ustalenia, jakie branche znajdują się w repozytorium. W poprawnie skonfigurowanym projekcie powinien pojawić się jedynie branch `main`.

## Modyfikacja Jenkinsfile
Znajdujesz się na branchu `main`, na którym Jenkinsfile wygląda następująco:
```
pipeline {
    agent any
    tools {
        maven "3.8.7"
        jdk "17"
    }

    stages {
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
    }
}
```
* `agent any` oznacza, że pipeline może być wykonywany na dowolnym agencie. W zależności od projektu, dla którego jest konfigurowane środowisko CI, może istnieć potrzeba wykonywania pipeline'u na konkretnej maszynie, np. aplikacja mobilna na platformę iOS może zostać skompilowana tylko w systemie macOS. W takich przypadkach konfiguruje się oddzielne maszyny, które są agentami Jenkinsa.
* Sekcja `tools` specyfikuje jakich narzędzi oraz w jakich wersjach należy użyć podczas wykonywania tego pipeline'u. Jak możesz zauważyć, są to wersje, które wcześniej zostały przez Ciebie zainstalowane podczas kroku _Konfiguracja mavena i JDK_.
* `stages` to najważniejsza sekcja tego pliku. Specyfikuje, z jakich etapów składa się pipeline i jakie komendy są w nich wykonywane. Jak widzisz znajduje się tylko jeden stage o nazwie _Compile_, podczas którego wykonywana jest kompilacja projektu za pomocą narzędzia maven.

Zmień branch na `feature/test_stage`, a następnie stwórz nowy branch o nazwie feature `feature/test_stage_{twoje imię}{pierwsza litera naziwska}`, czyli w moim przypadku `feature/test_stage_szymonp`. Jest to bardzo ważne, abyście nie nadpisywali swoich zmian oraz widzieli w Jenkinsie czy wasze zmiany działają, stąd każdy musi pracować na oddzielnym branchu :)

### Stage test

Twoim zadaniem będzie dodanie stage'a `Test` do pliku `Jenkinsfile`, który będzie realizował testowanie projektu. Powinien on wyglądać analogicznie jak `Compile`.

### Raport z testów

Do projektu został już dodany plugin `maven-surefire-plugin`, możesz go znaleźć w pliku `pom.xml`. Jest to jeden z najpopularniejszych pluginów związanych z generowaniem raportów z testów. W produkcyjnych projektach z reguły znajduje się od kilkuset testów do kilku tysięcy testów. Z tego powodu przydatne są raporty, które agregują ile testów nie przeszło i ułatwiają analizowanie wyników.

Twoim zadaniem będzie dodanie do pipeline'u publikowania raportu z testów.
Zacznij od lokalnego uruchomienia komendy do testowania projektu i spróbuj znaleźć w wygenerowanym folderze `target` te raporty. Jenkins posiada konkretną komendę `junit`, która odpowiada za zapisanie raportów. Akcje takie jak ta są wykonywane po danym stage'u niezależnie od jego wyniku (success lub failure), stąd definiuje się je inaczej w pliku `Jenkinsfile`:
```
...
stage('Test') {
   steps {
       ...
   }
   post {
       always {
           junit 'uzupełnij ścieżką do raportów'
       }
   }
}
```
Wydaje mi się, że jest to bardzo deklaratywne i nie wymaga szczegółowego opisu. 
Zwróć uwagę, że `maven-surefire-plugin` tworzy oddzielny plik dla każdej testowej klasy. Z tego powodu ścieżka do raportów powinna kończyć się `*.xml`, dzięki czemu plugin `junit` skorzysta z wyrażenia regularnego i zaimportuje wszystkie raporty.

Po zacommitowaniu zmian wejdź na swój barnch w jenkinsie i zobacz czy coś się zmieniło. Powinien być widoczny wykres oraz link `Latest Test Result` 


## Nieprzechodzący test

Zmień branch na `feature/failing_test`, a następnie stwórz nowy branch o nazwie feature `feature/failing_test_{twoje imię}{pierwsza litera naziwska}`. W klasie `BookServiceIntegrationTest` znajduje się nowy test, który nie przechodzi. Zpushuj nowy branch, sprawdź w raporcie co powoduje błąd i popraw kod w klasie `BookService`.

## Powiadomienia email

Pozostań na tym samym branchu i wykonaj poniższe zadanie

1. W celu wysyłki maila z powiadomieniem o buildzie należy najpierw wskazać serwer SMTP, który będzie odpowiedzialny za wysyłanie maili. Wejdź na stronę https://www.wpoven.com/tools/free-smtp-server-for-testing, a następnie wpisz dowolny adres email i naciśnij **Access Inbox** (najlepiej aby te adresy nie powtarzały się w pomiędzy wami, więc niech to będzie coś bardziej indywidualnego niż test@test.pl).
2. Skopiuj adres serwera SMTP z tej strony, zapamiętaj numer portu i przejdź do Jenkinsa.
3. Tak jak podczas konfiguracji Jenkinsa przejdź do _Manage Jenkins_, a następnie _Configure System_.
4. Znajdź sekcję _E-mail notification_ i wpisz skopiowany wcześniej adres serwera. Następnie naciśnij **Advanced** i wpisz numer portu. Na koniec naciśnij **Save**.
5. Przejdź do pliku Jenkinsfile. Analogicznie jak w przypadku publikowania raportu z testami użyj sekcji `post`, z tą różnicą, że powinna ona znaleźć się po klamrze kończącej sekcję `stages`. Emaile chcemy wysyłać zawsze i nie są one związane z żadnym stagem pipeline'u, z tego powodu sekcja ta znajduje się w innym miejscu. Tak jak przy publikowaniu raportu używaliśmy pluginu `junit`, tak dla wysyłki maili użyjemy komendy `mail`, przykład poniżej:
```
mail to: "adres z punktu 1.",
      subject: "temat",
      body: "treść"
```
Dodatkowo możemy użyć w definiowaniu tematu czy treści wielu zmiennych związanych z pipelinem np. wynik builda `${currentBuild.currentResult}`. Przykładowy temat wiadomości można skonfigurować następująco `subject: "Build ${currentBuild.currentResult}"`, który w wysłanej wiadomości będzie zawierał `Build SUCCESS` albo `Build FAILURE`. Takich zmiennych jest o wiele więcej oto kilka z nich:
   * `${env.JOB_NAME}`
   * `${currentBuild.currentResult}`
   * `${env.BUILD_URL}`
6. Stwórz sparametryzowany temat i wiadomość, a następnie zpushuj zmiany i zobacz czy po zakończeniu pipeline'u mail przyszedł na skrzynkę.