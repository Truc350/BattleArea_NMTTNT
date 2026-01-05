package com.example.model;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AIPlayer extends Hero {
    private static final int MAX_DEPTH = 7;

    private static class TranspositionEntry {
        int score;
        int depth;
        TranspositionEntry(int score, int depth) {
            this.score = score;
            this.depth = depth;
        }
    }
    private HashMap<String, TranspositionEntry> transpositionTable = new HashMap<>();

    private boolean usedHealThisTurn = false;
    private boolean usedDefendThisTurn = false;

    public AIPlayer(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    public String chooseBestAction(int currentTurn, Hero opponent, Game game) {
        double distance = this.getPosition().distanceTo(opponent.getPosition());
        double myRange = this.getAttackRange();

        if (shouldHeal() && !usedHealThisTurn) {
            usedHealThisTurn = true;
            return "Mana Regen";
        }

        if (shouldDefend(opponent, distance) && !usedDefendThisTurn) {
            usedDefendThisTurn = true;
            this.setDefending(true);
            return "Defend";
        }

        if (distance > myRange) {
            return chooseMovementStrategy(opponent, distance);
        }

        transpositionTable.clear();

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTurn);
        List<GameState> children = generateSuccessors(root, true);

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (GameState child : children) {
            int score = alphaBeta(child, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        return bestMove.getName();
    }

    private boolean shouldHeal() {
        boolean lowHP = this.getHp() < (this.getMaxHP() * 0.4);
        boolean enoughMP = this.getMp() >= 0;
        boolean hasHeal = false;

        for (Skill skill : this.getSkills()) {
            if (skill.getName().equals("Mana Regen")) {
                hasHeal = skill.canUse(0, this.getMp());
                break;
            }
        }

        return lowHP && enoughMP && hasHeal;
    }

    private boolean shouldDefend(Hero opponent, double distance) {
        boolean inDanger = distance <= opponent.getAttackRange();
        boolean mediumHP = this.getHp() < (this.getMaxHP() * 0.6);
        boolean opponentHasMana = opponent.getMp() > 15;

        return inDanger && mediumHP && opponentHasMana && !this.isDefending();
    }

    private String chooseMovementStrategy(Hero opponent, double distance) {
        double hpPercent = (double) this.getHp() / this.getMaxHP();
        double mpPercent = (double) this.getMp() / this.getMaxMP();

        if (hpPercent < 0.3) {
            return "Jump Up";
        }

        if (mpPercent < 0.2) {
            return "Jump Up";
        }

        if (distance > 10.0) {
            return "Move Closer";
        }

        if (opponent.getHp() < 30 && this.getMp() > 15) {
            return "Move Closer";
        }

        return "Move Closer";
    }

    public void resetTurnState() {
        usedHealThisTurn = false;
        usedDefendThisTurn = false;
        this.resetDefense();
    }

    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        String stateKey = getStateKey(state);
        TranspositionEntry cached = transpositionTable.get(stateKey);
        if (cached != null && cached.depth >= depth) {
            return cached.score;
        }

        if (depth == 0 || state.isTerminal()) {
            int score = evaluate(state);
            transpositionTable.put(stateKey, new TranspositionEntry(score, depth));
            return score;
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
            transpositionTable.put(stateKey, new TranspositionEntry(maxEval, depth));
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) break;
            }
            transpositionTable.put(stateKey, new TranspositionEntry(minEval, depth));
            return minEval;
        }
    }

    private String getStateKey(GameState state) {
        return String.format("%d_%d_%d_%d_%.1f_%b",
                state.aiHero.getHp(), state.aiHero.getMp(),
                state.playerHero.getHp(), state.playerHero.getMp(),
                state.aiHero.getPosition().distanceTo(state.playerHero.getPosition()),
                state.aiHero.isDefending());
    }

    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> successors = new ArrayList<>();
        Hero current = maxPlayer ? state.aiHero : state.playerHero;
        Hero target = maxPlayer ? state.playerHero : state.aiHero;
        int nextTurn = state.turn + 1;

        Hero aiBase = deepCopy(state.aiHero);
        Hero plBase = deepCopy(state.playerHero);

        for (Skill skill : current.getSkills()) {
            if (skill.getName().equals("Mana Regen") && skill.canUse(state.turn, current.getMp())) {
                Hero aiCopy = deepCopy(aiBase);
                Hero plCopy = deepCopy(plBase);
                Hero user = maxPlayer ? aiCopy : plCopy;

                user.setHp(Math.min(user.getMaxHP(), user.getHp() + skill.getHealHP()));
                user.setMp(Math.min(user.getMaxMP(), user.getMp() + skill.getHealMP()));

                successors.add(new GameState(aiCopy, plCopy, nextTurn,
                        new Move("Mana Regen", 0, nextTurn)));
            }
        }

        if (!current.isDefending()) {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;

            user.setDefending(true);

            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Defend", 0, nextTurn)));
        }

        double distance = current.getPosition().distanceTo(target.getPosition());
        boolean inRange = distance <= current.getAttackRange();

        if (inRange) {
            for (Skill skill : current.getSkills()) {
                if (!skill.getName().equals("Mana Regen") &&
                        skill.canUse(state.turn, current.getMp())) {

                    Hero aiCopy = deepCopy(aiBase);
                    Hero plCopy = deepCopy(plBase);
                    Hero user = maxPlayer ? aiCopy : plCopy;
                    Hero targ = maxPlayer ? plCopy : aiCopy;

                    int expectedDamage = skill.getDamage();

                    if (user instanceof Marksman) {
                        expectedDamage = (int)(expectedDamage * 1.3);
                    }

                    if (user.useSkillDeterministic(skill.getName(), state.turn, targ, expectedDamage)) {
                        successors.add(new GameState(aiCopy, plCopy, nextTurn,
                                new Move(skill.getName(), expectedDamage, nextTurn)));
                    }
                }
            }
        }

        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            Hero targ = maxPlayer ? plCopy : aiCopy;
            user.getPosition().moveToward(targ.getPosition(), Point.MOVE_SPEED);
            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Move Closer", 0, nextTurn)));
        }

        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            Hero targ = maxPlayer ? plCopy : aiCopy;
            user.moveAway(targ, Point.MOVE_SPEED);
            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Move Away", 0, nextTurn)));
        }

        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            Hero targ = maxPlayer ? plCopy : aiCopy;
            user.moveAway(targ, Point.MOVE_SPEED * 2);
            if (user.getMp() < 20) user.setMp(Math.min(100, user.getMp() + 5));
            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Jump Up", 0, nextTurn)));
        }

        if (maxPlayer) {
            successors.sort((a, b) -> Integer.compare(b.getMove().getDamage(),
                    a.getMove().getDamage()));
        }

        return successors;
    }

    private int evaluate(GameState s) {
        Hero ai = s.aiHero;
        Hero pl = s.playerHero;

        if (ai.getHp() <= 0) return -1000000;
        if (pl.getHp() <= 0) return 1000000;

        int score = 0;
        double dist = ai.getPosition().distanceTo(pl.getPosition());

        score += ai.getHp() * 40;
        score -= pl.getHp() * 40;

        score += ai.getMp() * 10;
        score -= pl.getMp() * 8;

        if (ai.isDefending()) {
            score += 1500;
        }
        if (pl.isDefending()) {
            score -= 1000;
        }

        if (pl.getHp() <= 40) score += 2000;
        if (pl.getHp() <= 25) score += 3000;
        if (pl.getHp() <= 15) score += 5000;

        if (ai.getHp() <= 40) score -= 2000;
        if (ai.getHp() <= 25) score -= 3000;
        if (ai.getHp() <= 15) score -= 5000;

        if (ai.getMp() >= 22) score += 800;
        if (ai.getMp() >= 30) score += 1200;

        if ((ai.getHp() <= 50 && ai.getMp() < 30) || ai.getMp() <= 15) {
            score += 1500;
        }

        if (dist <= 1.2) {
            score -= 1500;
        } else if (dist <= 2.0) {
            score += 500;
        } else if (dist <= 3.5) {
            score += 800;
        } else {
            score -= 300;
        }

        if (ai.getHp() <= 40 && dist > 3.0) {
            score += 1000;
        }

        if (pl.getMp() >= 25) score -= 500;
        if (pl.getMp() >= 40) score -= 1000;

        return score;
    }

    private Hero deepCopy(Hero original) {
        Hero copy = Hero.getHero(getHeroType(original), original.getName(),
                new Point(original.getPosition().getX(), original.getPosition().getY()));

        copy.setHp(original.getHp());
        copy.setMp(original.getMp());

        copy.setDefense(original.getDefense());
        if (original.isDefending()){
            copy.setDefending(true);
        }
        copy.setAttackRange(original.getAttackRange());
//        copy.setDefending(original.isDefending());

        copy.getSkills().clear();

        for (Skill sk : original.getSkills()) {
            Skill newSkill = new Skill(sk.getName(), sk.getMpCost(), sk.getCooldownTurns(),
                    sk.getDamage(), sk.getHealHP(), sk.getHealMP());
            newSkill.setLastUsedTurn(sk.getLastUsedTurn());
            copy.getSkills().add(newSkill);
        }

        return copy;
    }

    private HeroType getHeroType(Hero hero) {
        if (hero instanceof Fighter) return HeroType.FIGHTER;
        if (hero instanceof Marksman) return HeroType.MARKSMAN;
        if (hero instanceof Mage) return HeroType.MAGE;
        if (hero instanceof Support) return HeroType.SUPPORT;
        return HeroType.FIGHTER;
    }

    public void executeMovement(String moveName, Hero opponent) {
        if ("Move Closer".equals(moveName)) {
            this.getPosition().moveToward(opponent.getPosition(), Point.MOVE_SPEED);
        } else if ("Move Away".equals(moveName)) {
            this.moveAway(opponent, Point.MOVE_SPEED);
        } else if ("Jump Up".equals(moveName)) {
            this.moveAway(opponent, Point.MOVE_SPEED * 2);
            if (this.getMp() < 20) {
                this.setMp(Math.min(100, this.getMp() + 5));
            }
        }
    }

}