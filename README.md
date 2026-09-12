# ATC Terminal Arrival Manager (AMAN) Simulator

A desktop Java simulation of a **Terminal Arrival Manager (AMAN)** — the system air traffic
controllers use to sequence inbound aircraft onto a single runway safely and efficiently.

The simulator spawns aircraft on a radar display, tracks them in real time, and computes a
landing sequence that respects **wake turbulence separation rules**, so a light aircraft
can't land too soon behind a jumbo jet. Two different scheduling strategies can be swapped
in live, so you can watch the same traffic get resequenced under a different algorithm.

## Features

- **Live radar view** — aircraft move across a top-down radar display in real time, with
  adjustable simulation speed (1x / 2x / 5x).
- **Sequence table** — shows the current landing order and estimated/target landing times,
  updating automatically as aircraft move and get rescheduled.
- **Two interchangeable scheduling strategies**, switchable at runtime:
  - **First-Come, First-Served (FCFS)** — aircraft land in the order they're expected to
    arrive, offset only by the minimum required wake separation.
  - **Wake Turbulence Optimized** — aircraft are grouped by wake category to reduce the
    number of "penalty" gaps (e.g. a light aircraft following a super-heavy), aiming for a
    tighter overall sequence.
  - Full separation minima come from a wake category matrix (Super / Heavy / Medium /
    Light), matching the pattern used in real-world ATC.
- **Spawn traffic on demand** — add new inbound aircraft (A380, A350, B777, A320, C172) at
  random points around the airspace and watch them get folded into the sequence.

## Design & architecture

This project is built around three classic OO design patterns, deliberately, as a way of
practicing them on a non-trivial problem:

| Pattern | Where | Why |
|---|---|---|
| **MVC** | `model/`, `view/`, `controller/` | Keeps simulation state, rendering, and timing/coordination logic independent of each other. |
| **Observer** | `AirspaceListener`, implemented by `RadarPanel` and `SequenceTablePanel` | The two views subscribe to `Airspace` and repaint themselves whenever aircraft state changes — the model never needs to know the views exist. |
| **Strategy** | `SeparationStrategy`, implemented by `FCFSSeparationStrategy` and `WakeOptimizedStrategy` | `ArrivalManager` delegates scheduling to whichever strategy is currently selected, so a new sequencing algorithm can be added without touching any other class. |

### Project structure

```
src/atc/
├── Main.java                     # Application entry point & UI wiring
├── model/
│   ├── Aircraft.java              # Aircraft state: position, speed, fuel, ETO, target landing time
│   ├── AircraftFactory.java       # Builds Aircraft instances for known models
│   ├── Airspace.java               # Holds active aircraft, notifies listeners
│   ├── AirspaceListener.java      # Observer interface
│   ├── ArrivalManager.java        # Delegates to the active SeparationStrategy
│   ├── SeparationStrategy.java    # Strategy interface
│   ├── FCFSSeparationStrategy.java
│   ├── WakeOptimizedStrategy.java
│   ├── WakeCategory.java          # Separation-minima matrix
│   └── FlightState.java
├── controller/
│   ├── FlightController.java      # Advances aircraft position/state each tick
│   └── SimulationClock.java       # Drives the simulation loop at a chosen speed
└── view/
    ├── RadarPanel.java             # Radar display
    └── SequenceTablePanel.java     # Landing sequence table
```

## Running it

No build tool is required — it's plain Java with the standard library (Swing for the UI).

**Requirements:** JDK 17 or later.

```bash
# From the repository root
cd src
javac -d out $(find atc -name "*.java")
java -cp out atc.Main
```

On Windows (PowerShell), replace the `find` command:

```powershell
cd src
javac -d out (Get-ChildItem -Recurse -Filter *.java atc | % FullName)
java -cp out atc.Main
```

A window will open showing the radar on the left and the live sequence table on the right.
Use the toolbar to pause/resume, change simulation speed, switch the AMAN strategy, or spawn
new inbound traffic.

## Possible next steps

- Persist and compare sequencing outcomes (total delay, throughput) between strategies.
- Add runway/weather constraints (e.g. reduced arrival rate in low visibility).
- Unit tests for the two `SeparationStrategy` implementations.

## About

This is my first individual software project, built to practice object-oriented design and
core design patterns (MVC, Observer, Strategy) on a realistic, rule-driven scheduling
problem.
