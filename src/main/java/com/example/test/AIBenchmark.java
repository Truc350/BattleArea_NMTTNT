package com.example.test;

import com.example.model.*;

public class AIBenchmark {
    private static final int WARMUP_RUNS = 3;
    private static final int TEST_ITERATIONS = 3; // Chạy nhiều lần và lấy trung bình

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        AI PERFORMANCE BENCHMARK V2 - MINIMAX VS ALPHA-BETA                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════╝\n");

        // Warm up JVM
        System.out.println("Warming up JVM...");
        for (int i = 0; i < WARMUP_RUNS; i++) {
            testAlphaBeta(3);
        }
        System.out.println("Warm up completed.\n");

        // Print table header
        System.out.println("┌───────┬──────────────────────────────────┬──────────────────────────────────┐");
        System.out.println("│ Depth │           Minimax                │          Alpha-Beta              │");
        System.out.println("├───────┼──────────────────────────────────┼──────────────────────────────────┤");

        // Test depths 1-7
        for (int depth = 1; depth <= 7; depth++) {
            System.out.printf("Testing depth %d (avg of %d runs)...%n", depth, TEST_ITERATIONS);

            // Chạy nhiều lần và lấy trung bình
            long totalMinimaxMemory = 0;
            long totalMinimaxTime = 0;
            long totalAlphaBetaMemory = 0;
            long totalAlphaBetaTime = 0;

            for (int i = 0; i < TEST_ITERATIONS; i++) {
                BenchmarkResult minimax = testMinimax(depth);
                totalMinimaxMemory += minimax.memoryKB;
                totalMinimaxTime += minimax.timeMs;

                BenchmarkResult alphabeta = testAlphaBeta(depth);
                totalAlphaBetaMemory += alphabeta.memoryKB;
                totalAlphaBetaTime += alphabeta.timeMs;
            }

            long avgMinimaxMemory = totalMinimaxMemory / TEST_ITERATIONS;
            long avgMinimaxTime = totalMinimaxTime / TEST_ITERATIONS;
            long avgAlphaBetaMemory = totalAlphaBetaMemory / TEST_ITERATIONS;
            long avgAlphaBetaTime = totalAlphaBetaTime / TEST_ITERATIONS;

            // Print results
            System.out.printf("│   %d   │ Memory used: %-8d kilobytes │ Memory used: %-8d kilobytes │%n",
                    depth, avgMinimaxMemory, avgAlphaBetaMemory);
            System.out.printf("│       │ Executed Time: %-8d ms      │ Executed Time: %-8d ms      │%n",
                    avgMinimaxTime, avgAlphaBetaTime);
            System.out.println("├───────┼──────────────────────────────────┼──────────────────────────────────┤");

            // Stop if minimax takes too long
            if (avgMinimaxTime > 60000) {
                System.out.printf("│  >%d   │ TIMEOUT (>60 seconds)            │ (Continued testing Alpha-Beta)   │%n", depth);
                System.out.println("├───────┼──────────────────────────────────┼──────────────────────────────────┤");

                // Continue testing alpha-beta for remaining depths
                for (int d = depth + 1; d <= 7; d++) {
                    System.out.printf("Testing depth %d (Alpha-Beta only)...%n", d);
                    BenchmarkResult ab = testAlphaBeta(d);
                    System.out.printf("│   %d   │ -                                │ Memory: %-8d kilobytes       │%n",
                            d, ab.memoryKB);
                    System.out.printf("│       │ -                                │ Time: %-8d ms                │%n",
                            ab.timeMs);
                    System.out.println("├───────┼──────────────────────────────────┼──────────────────────────────────┤");
                }
                break;
            }
        }

        System.out.println("└───────┴──────────────────────────────────┴──────────────────────────────────┘");
        System.out.println("\n✅ Benchmark completed!");
        System.out.println("\nℹ️  Note: Results averaged over " + TEST_ITERATIONS + " iterations");
    }

    private static BenchmarkResult testMinimax(int depth) {
        Point playerPos = new Point(15.0, 0.0);
        Point aiPos = new Point(5.0, 0.0);

        Hero player = Hero.getHero(HeroType.MAGE, "Player", playerPos);
        AIPlayerMinimax ai = new AIPlayerMinimax("AI", 100, 100, aiPos, 15, 10);
        setupComplexSkills(ai);
        setupComplexSkills(player);

        // Set HP/MP để tạo nhiều state hơn
        player.setHp(80);
        player.setMp(60);
        ai.setHp(85);
        ai.setMp(55);

        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        long startTime = System.nanoTime();

        String action = ai.chooseBestActionMinimax(1, player, depth);

        long endTime = System.nanoTime();

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long memoryUsed = Math.max(0, (memoryAfter - memoryBefore) / 1024);
        long timeMs = (endTime - startTime) / 1_000_000; // Convert to ms

        return new BenchmarkResult(memoryUsed, timeMs);
    }

    private static BenchmarkResult testAlphaBeta(int depth) {
        Point playerPos = new Point(15.0, 0.0);
        Point aiPos = new Point(5.0, 0.0);

        Hero player = Hero.getHero(HeroType.MAGE, "Player", playerPos);
        AIPlayerAlphaBeta ai = new AIPlayerAlphaBeta("AI", 100, 100, aiPos, 15, 10, depth);
        setupComplexSkills(ai);
        setupComplexSkills(player);

        player.setHp(80);
        player.setMp(60);
        ai.setHp(85);
        ai.setMp(55);

        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        long startTime = System.nanoTime();

        String action = ai.chooseBestActionAlphaBeta(1, player);

        long endTime = System.nanoTime();

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long memoryUsed = Math.max(0, (memoryAfter - memoryBefore) / 1024);
        long timeMs = (endTime - startTime) / 1_000_000;

        return new BenchmarkResult(memoryUsed, timeMs);
    }

    private static void setupComplexSkills(Hero hero) {
        hero.getSkills().clear();
        // Nhiều skills hơn = nhiều branches hơn
        hero.getSkills().add(new Skill("Basic Attack", 0, 0, 15, 0, 0));
        hero.getSkills().add(new Skill("Mana Regen", 0, 3, 0, 10, 15));
        hero.getSkills().add(new Skill("Quick Strike", 8, 1, 20, 0, 0));
        hero.getSkills().add(new Skill("Power Strike", 12, 2, 28, 0, 0));
        hero.getSkills().add(new Skill("Heavy Blow", 18, 3, 35, 0, 0));
        hero.getSkills().add(new Skill("Ultimate Strike", 25, 4, 45, 0, 0));
    }

    static class BenchmarkResult {
        long memoryKB;
        long timeMs;

        BenchmarkResult(long memoryKB, long timeMs) {
            this.memoryKB = memoryKB;
            this.timeMs = timeMs;
        }
    }
}
