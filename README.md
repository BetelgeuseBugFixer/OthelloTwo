# OthelloTwo: A High-Performance Othello AI
OthelloTwo is a high-performance Othello (Reversi) engine built in Java. Born as a university project in my third semester, it has steadily grown into a comprehensive testbed for advanced AI algorithms, search optimizations, and machine learning techniques.

The core of the engine is a highly optimized Bitboard representation, powering a deep Min-Max search with Alpha-Beta pruning, a Monte Carlo Tree Search (MCTS), and a Genetic Algorithm designed to tune the heuristic evaluation function.

# Core Architecture & Features

1. **The Bitboard Engine (`Othello.java`)**
   To achieve the speed necessary for deep search trees, the board state is not represented as a 2D array, but via Bitboards.
   - The entire board state (black discs, white discs) is encoded in two 64-bit long integers
   - Move generation, flip calculation, and edge evaluations are heavily optimized using fast bitwise operations (shifts, masks, XORs), eliminating the need for slow iterative loops.
   - Symmetry Optimization: The board supports fast vertical, horizontal, and diagonal mirroring to drastically reduce the state space during tree searches.
2. **Search Algorithms (`AaronFish.java` & `MonteCarloTreeSearch.java`)**
   The AI supports two distinct search paradigms:
    - **Alpha-Beta Pruning (Min-Max)**: The primary engine (AaronFish.java) uses a highly optimized Min-Max algorithm with Alpha-Beta pruning to evaluate future states. It dynamically adjusts its search depth based on remaining time and game phase.
    - **Monte Carlo Tree Search (MCTS)**: An alternative, probability-based search algorithm (MonteCarloTreeSearch.java) that balances exploration and exploitation using the UCT formula.
3. **Transposition Tables & State Caching (`MirroredHashTree.java`)**
    - **Transposition Tables** (`HashTree`): Caches previously evaluated board states and their scores.
    - **Symmetry-Aware Caching** (`MirroredHashTree`): Taking full advantage of the Bitboard engine, this tree implementation checks if a board state is functionally identical to a cached state via vertical, horizontal, or diagonal mirroring, effectively cutting the state space by a massive factor.
4. **Advanced Heuristic Evaluation (`BetterGrader.java`)**
   While deep search is crucial, the evaluation of leaf nodes dictates the AI's actual playing strength. The `BetterGrader` does not just count discs; it evaluates dozens of positional features:
    - **Mobility & Parity**: Calculates the number of legal moves and the parity of the board (who gets the last move in a region).
    - **Edge & Corner Mechanics**: Highly detailed edge evaluation, detecting wedges, balanced/unbalanced edges, uneven gaps, and safely flippable discs.
    - **Stability**: Calculates unconditionally stable discs originating from corners and filled edges.
    - Note: The evaluation weights change dynamically based on the game phase (early/mid vs. late game).
5. **Genetic Algorithm for Weight Tuning (`GeneticAlgorithm.java`)**
   Determining the exact numerical weights for the dozens of features in `BetterGrader` manually is impossible. Therefore, the project includes a Genetic Algorithm to evolve the optimal heuristic weights over time.
   - **Evolutionary Process**: Initializes a population of AI agents with random weights. Agents play against each other and against static benchmarks (Monte Carlo, Handcrafted, etc.).
   - **Recombination & Mutation**: The best-performing agents are selected as parents. Their weights are recombined and mutated using a Gaussian distribution (`NormalDistribution`) to create the next generation.
   - **Multi-Threading**: Matchmaking and fitness evaluation run concurrently via ExecutorService to maximize CPU utilization during training.
6. **Opening Library (`OpeningLibraryCreator.java`)**
   To save precious time on the clock during the early game, the engine utilizes a pre-calculated Opening Library. It uses a deep search to map out optimal responses for the first moves, saving them directly into a fast-access Hash Map.
7. **JavaFX GUI (Runner.java)**
   The project includes a custom-built Graphical User Interface using JavaFX. It allows users to play against the AI, set up AI vs. AI matches, and visually track the game state and the engine's calculation times.
