# spec.md – Secret Hitler (WebApp)

## 1. Projektziel
Ziel ist die Umsetzung des Gesellschaftsspiels Secret Hitler als browserbasierte
WebApp für 5–10 Spieler. Die Anwendung wird im Hotseat Modus gespielt: Da die 
umsetzung der Spiellogik und nicht spielspass im Zentrum steht, werden alle
Spieler sich ein einziges Gerät teilen und übergeben es einander, wenn sie an der
Reihe sind. Die spielregeln entsprechen strikt dem offiziellen regelwerk (Secret_Hitler_Rules.pdf, im folgenden abteil zusammengefasst).

---

## 2. Spielregeln (Siehe Secret_Hitler_Rules.pdf)

### 2.1 Rollen und Teams
| Spieleranzahl | Liberale | Faschisten | 
|:---:|:---:|:---:|
| 5 | 3 | 1 |
| 6 | 4 | 1 | 
| 7 | 4 | 2 | 
| 8 | 5 | 2 | 
| 9 | 5 | 3 | 
| 10 | 6 | 3 | 

*immer nur 1 Hitler

- Jeder Spieler erhält genau eine geheime Rolle -> Liberal, Fascist oder Hitler.
- Hitler spielt für das faschistische Team, kennt allerdings aber die anderen Faschisten nicht
  (ab 7 Spielern. Bei 5 oder 6 kennen sich alle Faschisten gegenseitig).
- Faschisten kennen Hitler (ab 7 Spielern nur durch das Daumen Signal während der
  Augen-zu Phase).
- Jeder Spieler erhält zusätzlich eine Partei-Mitgliedskarte (Liberal oder
  Fascist), die bei der "Investigate Loyalty" Aktion gezeigt wird. Hitlers Karte zeigt
  „Fascist", verrät aber nicht die Sonderrolle.

### 2.2 Siegbedingungen
**Liberale gewinnen, wenn:**
- 5 liberale Policies verabschiedet werden oder
- Hitler erfolgreich abgesetzt (laut spielregeln "assasiniert", das klingt aber etwas makaber) wurde.

**Faschisten gewinnen, wenn:**
- 6 faschistische Policies verabschiedet wurden, oder
- Hitler nach der dritten fasichistischen Policy als Kanzler gewählt wird.

### 2.3 Spielmaterial (digital)
- 17 Policy Kacheln: 6 Liberal, 11 Fascist
- Rollen und Partei Mitgliedskarten (je nach Spieleranzahl)
- Wahlkarten: Ja / Nein 
- Election Tracker 
- Fascist Board (angepasst an Spieleranzahl) + Liberal Board
- Präsident und Kanzler Placard

### 2.4 Spielablauf pro Runde
Jede Runde besteht aus drei Phasen:

#### Phase 1 – Election
1. **Präsidentschaft weitergeben:** Das Präsidenten Placard wandert im Uhrzeigersinn
   zum nächsten Spieler.
2. **Kanzler nominieren:** Der Presidential Candidate wählt einen Chancellor Candidate
   aus allen berechtigten Spielern.
3. **Wahlberechtigung (Eligibility):**
    - Der zuletzt gewählte Präsident und der zuletzt gewählte Kanzler sind für das
      Kanzleramt gesperrt.
    - Bei < 5 verbleibenden Spielern ist nur der zuletzt gewählte Kanzler gesperrt.
    - Term Limits beziehen sich auf die zuletzt gewählten (nicht nur nominierten)
      Amtsträger.
    - Jeder kann Präsident werden, auch wer gerade noch Kanzler war.
4. **Abstimmung:** Alle Spieler (inklusive Kandidaten) stimmen gleichzeitig mit Ja/Nein
   ab. Ergebnis ist öffentlich.
    - **Mehrheit Ja → Regierung gewählt.** Bei >= 3 fasichistischen Policies: Prüfung,
      ob der neue Kanzler Hitler ist (falls ja: Faschisten gewinnen).
    - **Gleichstand oder Mehrheit Nein → Wahl gescheitert.** Election Tracker +1.
5. **Election Tracker:** Erreicht er 3 (drei gescheiterte Wahlen in Folge), wird die
   oberste Policy des Decks sofort aufgedeckt und in Kraft gesetzt (kein
   Presidential Power, Term Limits werden zurückgesetzt und alle Spieler wieder
   wahlberechtigt). Der Tracker setzt zurück, sobald irgendeine Policy in Kraft tritt

#### Phase 2 – Legislative Session
1. Der Präsident zieht verdeckt 3 Policy-Kacheln.
2. Er wirft 1 Kachel verdeckt ab und gibt die restlichen 2 verdeckt an den
   Kanzler.
3. Der Kanzler wirft eine Kachel verdeckt ab und spielt die verbleibende 1 Kachel
   offen aus.
4. Kommunikationsverbot: Präsident und Kanzler dürfen während der Legislative
   Session weder verbal noch nonverbal kommunizieren. Kacheln dürfen nicht zufällig
   oder taktisch uneindeutig ausgewählt werden.
5. Abgeworfene Kacheln werden nicht öffentlich gezeigt. Spieler dürfen über den
   Inhalt ihrer Hand lügen.
6. Falls nach der Legislative Session weniger als 3 Kacheln im Deck verbleiben, werden
   Deck und Ablagestapel neu gemischt.

#### Phase 3 – Executive Action
- Wurde eine faschistische Policy mit Presidential Power gespielt, muss der Präsident
  diese Power sofort einsetzen (Spiel pausiert bis zur Ausführung).
- Powers werden nur einmal genutzt und stapeln sich nicht.

### 2.5 Presidential Powers 
| Power | Beschreibung |
|---|---|
| **Investigate Loyalty** | Präsident wählt einen Spieler und sieht dessen Partei-Mitgliedskarte (nicht die Rolle). Ergebnis kann verschwiegen oder gelogen werden. Kein Spieler darf zweimal untersucht werden. |
| **Call Special Election** | Präsident übergibt das Placard an einen beliebigen anderen Spieler für die nächste Wahl. Danach kehrt die normale Reihenfolge zurück (links vom auslösenden Präsidenten). |
| **Policy Peek** | Präsident sieht die obersten 3 Kacheln des Decks, ohne deren Reihenfolge zu ändern. |
| **Execution** | Präsident eliminiert einen Spieler. Ist es Hitler → Liberale gewinnen. Andernfalls bleibt die Rolle des Eliminierten geheim. Eliminierte Spieler scheiden aus (kein Reden, kein Wählen, kein Kandidieren). |

### 2.6 Veto Power
- Tritt in Kraft, sobald 5 faschistische Policies verabschiedet wurden.
- Ablauf: Kanzler kann nach Erhalt der 2 Kacheln „Veto" vorschlagen. Stimmt der
  Präsident zu, werden beide Kacheln abgeworfen und der Election Tracker wird um 1
  erhöht. Stimmt der Präsident nicht zu, muss der Kanzler normal spielen.

### 2.7 Lügen und Wahrheitspflicht
- Spieler dürfen grundsätzlich über alle verdeckten Informationen lügen.
- Ausnahmen (Wahrheitspflicht):
    - Ein Spieler, der Hitler ist, muss dies bei Assassination oder nach Wahl als
      Kanzler (≥ 3 faschistische Policies) bestätigen, wenn er direkt gefragt wird.

---

## 4. Use Cases

### UC 1: Spiel starten
- **Akteur:** Alle Spieler gemeinsam
- **Vorbedingung:** Mindestens 5, maximal 10 Spieler
- **Ablauf:** Spieleranzahl und Namen eingeben → Rollen werden zufällig verteilt →
  Geheime Rollenzuweisung → Augen zu Phase → spiel beginnt

### UC 2: Kanzler nominieren
- **Akteur:** Aktueller Presidential Candidate
- **Ablauf:** App zeigt berechtigte Spieler → Kandidat wählt einen aus

### UC 3: Abstimmen
- **Akteur:** Jeder Spieler 
- **Ablauf:** Spieler erhält Gerät, wählt verdeckt Ja/Nein → Gerät geht weiter →
  Nach letzter Stimme zeigt App alle Stimmen gleichzeitig und das Ergebnis

### UC 4: Legislative Session durchführen
- **Akteur:** Präsident, dann Kanzler 
- **Ablauf:** Präsident sieht 3 Kacheln, wählt eine zum Abwerfen → Gerät geht an
  Kanzler → Kanzler sieht 2 Kacheln, wählt eine zum Abwerfen → Policy wird gespielt

### UC 5: Presidential Power ausführen
- **Akteur:** Präsident
- **Ablauf:** App zeigt verfügbare Power → Präsident führt sie aus → Ergebnis wird dem Präsidenten angezeigt
---

## 5. Grundlegende Interaktion (Hotseat-Modell)

- ** Gerät, browser-Tab** für alle spieler.
- Das Gerät wird aktiv weitergegeben. Die Ap gibt jederzeit klar an welcher
  Spieler gerade am Zug ist und was er tun sollte.
- **Private Ansichten** werden hinter
  einem Weiter Button versteckt, damit der aktive Spieler das Gerät
  übernehmen kann bevor geschützte Informationen angezeigt werden.
- **Übergabe-Bildschirm:** Vor jeder privaten Anzeige erscheint ein neutraler
  Übergabebildschirm mit dem Namen des nächsten Spielers und einem „Ich bin [Name]
   Anzeigen"Button.
- Kein Spieler darf die verdeckten Informationen eines anderen sehen. Die App
  unterstützt dies technisch durch den Übergabeablauf, ein zentraler Teil der Implementierten Logik.

---

## 6. Akzeptanzkriterien

### 6.1 Spiellogik

| ID | Kriterium                                                                                                                                   |
|---|---------------------------------------------------------------------------------------------------------------------------------------------|
| AK-01 | Rollen werden korrekt und zufällig verteilt                                                                                                 |
| AK-02 | Termlimits wird korrekt durchgesetzt.                                                                                                       |
| AK-03 | Der election tracker wird korrekt erhöht und bei Policy-Erlass zurückgesetzt.                                                               |
| AK-04 | Bei 3 aufeinanderfolgenden gescheiterten Wahlen wird die oberste Policy sofort ausgespielt. |
| AK-05 | Presidential Powers werden nur ausgelöst, wenn die korrekte faschistische Policy Felder belegt sind.            |
| AK-06 | Veto Power steht erst nach der 5 faichistischen Policy zur Verfügung.                                                                       |
| AK-07 | Alle Siegbedingungen werden erkannt und das Spiel beendet.                                                                       |
---

## 7. Constraints

- **Plattform:** Moderne Webbrowser
- **Spieleranzahl:** 5–10 Spieler
- **Sprache:** Java
