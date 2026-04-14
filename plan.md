# plan.md – Secret Hitler (WebApp)

## 1. Architektur

Die anwendung folgt einer klassischen MVC-Struktur als Java-Webapplikation.
Das Frontend ist eine Single-Page-Anwendung (HTML/CSS/JS), die über eine REST-API
mit dem Backend kommuniziert. Der gesamte Spielzustand lebt serverseitig im Speicher
(kein persistenter Storage nötig, da ein Spiel nur eine Session dauert).

- **Frontend:** Vanilla HTML/CSS/JS, kein Framework. Rendert den aktuellen View
  basierend auf der JSON-Antwort der API. Kümmert sich um den Hotseat-Übergabeflow
- **Backend:** Spring Boot als HTTP-Server. REST-Controller nehmen Spieleraktionen
  entgegen und delegieren an die GameEngine. Hält genau eine `Game`-Instanz
  pro laufendem Spiel (als Spring-Bean im Application Context).
- **Gam-engine:** Reine Java-Klassen, die den Spielzustand und die Regeln abbilden.
  Keinerlei HTTP-Abhängigkeit – testbar ohne Server.

## 2. Programmiersprache und Libraries

**Sprache:** Java 21

**Funktionale Patterns und Immutability:**

- **Records** (seit Java 16) für alle Datenklassen, die den Spielzustand beschreiben
  (`PlayerInfo`, `PolicyHand`, `ElectionResult`, etc.). Records sind implizit final.
- **Streams** statt klassischer for-Schleifen wo sinnvoll (filtern, mappen, sammeln).

**Dependencies:**

| Library | Zweck |
|---|---|
| Spring Boot 3.x (https://spring.io/projects/spring-boot) | HTTP-Server, REST-Controller, DI, JSON via Jackson (mitgeliefert) |
| JUnit 5 + AssertJ | Unit-Tests für GameEngine |

**Build:** Gradle (Kotlin DSL).

## 3. Undo-Mechanismus

Anforderung: Jeder Spielzug kann rückgängig gemacht werden.

Umsetzung: Da der gesamte GameState aus immutable Records besteht, speichern wir
nach jedem Spielzug den vorherigen Zustand in einer `List<GameState> history`.
Undo heisst einfach: letzten Eintrag aus der History nehmen und als aktuellen
State setzen. Kein Diffing, kein Reverse-Engineering von Aktionen nötig.

## 4. Zeitplan

Wir sind zu zweit (Armin und Julian). spec.md und plan.md sind erledigt.

- **Bis 11.05 (PoC):** Armin baut die GameEngine (Rollen, Election, Legislative Session, Deck-Logik). Julian baut Frontend + REST-Controller + Hotseat-Flow. Ziel: ein Spiel kann durchgespielt werden, ohne Spezialfälle.
- **Bis 18.05 (Abgabe):** Presidential Powers, Veto, Undo, Edge-Cases, Bugfixes, Demo vorbereiten. Beide zusammen.

## 5. Game-State und Transitions

### 5.1 GameState (Record)

```java
record GameState(
    List<Player> players,
    int liberalPolicies,
    int fascistPolicies,
    List<Policy> drawPile,
    List<Policy> discardPile,
    int electionTracker,
    int presidentIndex,
    Integer lastElectedPresident,
    Integer lastElectedChancellor,
    Phase phase,
    boolean vetoUnlocked
)
```

### 5.2 Phasen und Übergänge

```
NominateChancellor  -->  Vote  -->  VoteResult
    │                                    │
    │            ┌───── Ja ──────────────┘
    │            │           Nein/Gleichstand ──> Tracker+1 ──> NominateChancellor
    │            v                                  (bei 3: top-deck Policy)
    │     PresidentDiscard  -->  ChancellorDiscard  -->  Policy enacted
    │            │                      │                      │
    │            │               (Veto möglich ab 5F)    ExecutiveAction?
    │            │                                             │
    └────────────┴─────────────────────────────────────────────┘
                                                          oder GameOver
```

Jede Transition ist eine Methode in der GameEngine: nimmt den aktuellen
GameState + Spieleraktion, gibt einen neuen GameState zurück. Ungültige
Aktionen werfen eine Exception.

### 5.3 Akzeptanzkriterien

| AK | Wie enforced |
|---|---|
| Spiel kann mit 5–10 Spielern gestartet werden | Lobby validiert Spieleranzahl, Rollen werden korrekt verteilt. Unit-Test prüft Anzahl pro Team. |
| Hotseat-Übergabe funktioniert | Kein Spieler sieht fremde Informationen. Übergabebildschirm vor jeder privaten Ansicht. |
| Komplette Spielrunde durchspielbar | Election → Legislative Session → nächste Runde. Alle Phasen-Transitions getestet. |
| Siegbedingungen werden erkannt | `checkWinCondition()` nach jeder Policy und Execution. Spiel endet korrekt. |
| Undo funktioniert | Jeder Spielzug kann rückgängig gemacht werden via GameState-History. |

## 6. Hotseat-Übergabe

1. API liefert `{ phase, activePlayer, ... }`.
2. Frontend zeigt Übergabebildschirm: "Bitte Gerät an [Name] übergeben."
3. Spieler drückt "Ich bin [Name] – Anzeigen".
4. Private Ansicht wird gezeigt (z.B. Ja/Nein, oder 3 Policy-Kacheln).
5. Spieler trifft Wahl, Frontend sendet POST an API.
6. Nächster Übergabebildschirm oder öffentliche Ansicht.

Für die Abstimmung: Jeder Spieler bekommt nacheinander den Übergabebildschirm,
stimmt ab, und erst wenn alle fertig sind, werden alle Stimmen aufgedeckt.