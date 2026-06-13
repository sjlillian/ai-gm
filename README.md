# ai-gm: AI Game Master with Temporal Workflows

An innovative Java project that combines game design principles with Temporal workflow orchestration to create a durable, AI-powered game master system.

## Overview

**ai-gm** is a game master engine inspired by tabletop RPG mechanics (particularly Blades in the Dark). It uses Temporal's workflow orchestration to manage complex game state, turn resolution, and narrative consistency across game sessions.

Think of it as a referee that tracks what happened, enforces the rules, responds to player actions, and maintains persistent game state—all with built-in durability and automatic recovery.

## Key Features

- **Durable Game State**: Temporal ensures game progress is never lost, even if the system crashes
- **Turn-Based Gameplay**: Handle player actions with proper state transitions and consequences
- **Complex Mechanics**: Support for stress clocks, harm tracking, trauma, crew standings, and more
- **Activity-Based Actions**: Separate action resolution from game flow for clean, testable code
- **Scalable Architecture**: Worker-based design allows multiple games to run in parallel

## Architecture

The project follows Temporal's fundamental patterns:

```
GameStarter
    ↓ (initiates workflow)
Temporal Service
    ↓ (orchestrates)
GameWorker
    ├─ GameWorkflowImpl (game rules & flow)
    └─ GameActivitiesImpl (action resolution)
```

### Core Components

- **GameWorkflow/GameWorkflowImpl**: Defines game flow and rules
- **GameActivities/GameActivitiesImpl**: Executes game actions with side effects
- **GameWorker**: Long-running process that executes workflows and activities
- **GameStarter**: Entry point for initiating new game sessions
- **Game State Models**: Player, Crew, Clock, Harm, Trauma, Heat, CrewStanding

## Getting Started

### Prerequisites

- Java 21+
- Maven
- Temporal Server (can run locally)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/sjlillian/ai-gm.git
cd ai-gm
```

2. Build with Maven:
```bash
cd ai-gm
mvn clean install
```

3. Start a Temporal Server (if not already running):
```bash
# Install Temporal CLI (https://temporal.io/docs/cli)
temporal server start-dev
```

4. Run the worker in one terminal:
```bash
mvn exec:java -Dexec.mainClass="aigm.workers.GameWorker"
```

5. Run the game starter in another terminal:
```bash
mvn exec:java -Dexec.mainClass="aigm.App"
```

## Project Structure

```
ai-gm/
├── pom.xml
└── src/main/java/aigm/
    ├── App.java                          # Main entry point
    ├── activities/
    │   ├── GameActivities.java           # Activity interface
    │   └── GameActivitiesImpl.java        # Action resolution logic
    ├── gamestate/
    │   ├── GameWorkflow.java             # Workflow interface
    │   ├── GameWorkflowImpl.java          # Game flow and rules
    │   ├── Player.java                   # Player state (skills, harm, resources)
    │   ├── Crew.java                     # Crew/faction state
    │   ├── Clock.java                    # Progress clock mechanics
    │   ├── Harm.java                     # Injury tracking
    │   ├── Heat.java                     # Law enforcement heat
    │   ├── Truama.java                   # Psychological trauma
    │   ├── CrewStanding.java             # Faction relationships
    │   ├── dto/                          # Data transfer objects
    │   └── enums/                        # Game enums (Action, Phase, etc.)
    ├── starters/
    │   └── GameStarter.java              # Workflow initialization
    └── workers/
        └── GameWorker.java               # Temporal worker process
```

## Game Design Concepts

This project implements mechanics inspired by Blades in the Dark:

- **Players**: Have action ratings (skills), stress clocks, harm, and resources
- **Clocks**: A mechanic where segments fill until consequences trigger
- **Stress & Trauma**: Psychological state tracking
- **Crews**: Player groups with standings with other factions
- **Heat**: Law enforcement attention level

## Dependencies

- **Temporal SDK** (1.35.0): Workflow orchestration and durability
- **Jackson** (2.17.0): JSON serialization for game state
- **OkHttp3** (4.12.0): HTTP client for external integrations
- **SLF4J** (2.0.9): Logging
- **JUnit 5** (5.10.0): Testing

## Current Status

This is an early-stage prototype. Key features in development:

- [ ] Complete turn-based game loop
- [ ] Dice rolling and action resolution
- [ ] Dynamic narrative generation
- [ ] Discord bot integration
- [ ] Web UI for gameplay
- [ ] Persistent game storage
- [ ] Multi-player support

## Why Temporal for Games?

Temporal's characteristics make it ideal for game engines:

1. **Durability**: Game state persists across failures
2. **Determinism**: Ensures reproducible, fair gameplay
3. **Activity Pattern**: Maps naturally to game actions
4. **Scalability**: Run multiple games concurrently
5. **Auditability**: Complete event history of every game

Read the [full blog post]([https://github.com/sjlillian/ai-gm](https://spencerjl.com/blog/Building%20an%20AI%20Game%20Master%20with%20Temporal)) for an in-depth discussion of game design lessons and Temporal architecture.

## Learning Resources

- [Temporal Documentation](https://temporal.io/docs/)
- [Blades in the Dark - Free Edition](https://bladesinthedark.com/)
- [Procedural Game Design](https://www.sciencedirect.com/topics/computer-science/procedural-game-generation)

## Contributing

This is a personal learning project, but feel free to open issues or discussions about game design and workflow orchestration!

## License

MIT License - feel free to use this as a reference for your own projects.

## Contact

Questions? Ideas? Reach out on GitHub or check out my other projects at [@sjlillian](https://github.com/sjlillian).

---

**Last Updated**: June 2026  
**Java Version**: 21  
**Temporal SDK**: 1.35.0
