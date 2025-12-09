package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Hero {
    private static final int MAX_DEPTH = 5;
    private boolean useAlphaBeta = true;

    public AIPlayer(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Rage Strike", 15, 8, attack + 10, 0, 0));
        skills.add(new Skill("Fury Burst", 20, 12, attack + 20, 0, 0));
        skills.add(new Skill("Ultimate Rage", 30, 20, attack * 2, 0, 0));
    }

    public String chooseBestAction(long currentTime, Hero opponent, Game game) {
        if (!game.isRange()) {
            System.out.println("ai dang di chuyển lại gần đối thủ");
            return "Move Closer";
        }

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTime);
        List<GameState> children = generateSuccessors(root, true);

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = new Move("Basic Attack", attack, (int) currentTime);

        for (GameState child : children) {
            int score;
            if (useAlphaBeta) {
                score = alphaBeta(child, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                score = minimax(false, child, MAX_DEPTH - 1);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        System.out.println("AI Chọn: " + bestMove.name +
                " (điểm: " + bestScore + ", thuật toán: " +
                (useAlphaBeta ? "Alpha-Beta" : "Minimax") + ")");

        if (!bestMove.name.equals("Basic Attack")) {
            useSkill(bestMove.name, currentTime, opponent);
        } else {
            opponent.takeDamage(attack);
        }
        return bestMove.name;
    }

    private int minimax(boolean maxPlayer, GameState state, int depth) {
        if (depth == 0 || state.isTerminal()) {
            return evaluate(state);
        }

        if (maxPlayer) {
            int bestValue = Integer.MIN_VALUE;
            for (GameState newState : generateSuccessors(state, true)) {
                int value = minimax(false, newState, depth - 1);
                bestValue = Math.max(bestValue, value);
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (GameState newState : generateSuccessors(state, false)) {
                int value = minimax(true, newState, depth - 1);
                bestValue = Math.min(bestValue, value);
            }
            return bestValue;
        }
    }

    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0 || state.isTerminal()) {
            return evaluate(state);
        }

        List<GameState> children = generateSuccessors(state, maximizingPlayer);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> list = new ArrayList<>();

        Hero aiCopy, playerCopy;
        Hero attacker, target;

        // BASIC ATTACK
        aiCopy = deepCopy(state.aiHero);
        playerCopy = deepCopy(state.playerHero);

        attacker = maxPlayer ? aiCopy : playerCopy;
        target = maxPlayer ? playerCopy : aiCopy;

        target.takeDamage(attacker.attack);

        Move move = new Move("Basic Attack", attacker.attack, (int) (state.time + 1000));

        list.add(new GameState(aiCopy, playerCopy, state.time + 1000, move));

        // SKILLS
        Hero current = maxPlayer ? state.aiHero : state.playerHero;

        for (Skill skill : current.skills) {
            if (skill.canUse(state.time, current.mp)) {

                aiCopy = deepCopy(state.aiHero);
                playerCopy = deepCopy(state.playerHero);

                attacker = maxPlayer ? aiCopy : playerCopy;
                target = maxPlayer ? playerCopy : aiCopy;

                if (attacker.useSkill(skill.getName(), state.time, target)) {
                    Move mv = new Move(skill.getName(), skill.getDamage(), (int) (state.time + 1000));

                    list.add(new GameState(aiCopy, playerCopy, state.time + 1000, mv));
                }
            }
        }
        return list;
    }

    private int evaluate(GameState state) {
        if (state.aiHero.hp <= 0) return -999999;
        if (state.playerHero.hp <= 0) return 999999;

        int score = 0;
        score += state.aiHero.hp * 15;
        score -= state.playerHero.hp * 18;
        score += state.aiHero.mp * 4;
        score -= state.playerHero.mp * 2;

        if (state.playerHero.hp <= 25) score += 400;
        if (state.playerHero.hp <= 12) score += 800;
        if (state.aiHero.hp <= 20) score -= 600;

        if (state.aiHero instanceof Fighter && state.aiHero.mp >= 22) score += 300;
        if (state.aiHero instanceof Mage && state.aiHero.mp >= 25 && state.playerHero.hp <= 40) score += 500;
        if (state.aiHero instanceof Marksman && state.playerHero.hp <= 35) score += 700;
        if (state.aiHero instanceof Support && state.aiHero.hp <= 30) score += 400;

        return score;
    }

    private Hero deepCopy(Hero original) {
        Hero copy;

        if (original instanceof Fighter) {
            copy = new Fighter(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else if (original instanceof Mage) {
            copy = new Mage(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else if (original instanceof Marksman) {
            copy = new Marksman(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
            ((Marksman) copy).critRate = ((Marksman) original).critRate;
        } else if (original instanceof Support) {
            copy = new Support(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else {
            copy = new Fighter(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        }

        copy.hp = original.hp;
        copy.mp = original.mp;

        copy.skills = new ArrayList<>();
        for (Skill s : original.skills) {
            Skill newSkill = new Skill(s.getName(), s.getMpCost(), s.getCooldown(),
                    s.getDamage(), s.getHealHP(), s.getHealMP());
            newSkill.setLastUsedTime(s.getLastUsedTime());
            copy.skills.add(newSkill);
        }

        return copy;
    }

    public void setUseAlphaBeta(boolean use) {
        this.useAlphaBeta = use;
    }
    // Hàm test cho phép gọi từ bên ngoài
    public List<GameState> generateSuccessorsForTest(GameState s, boolean max) {
        return generateSuccessors(s, max);
    }

}
