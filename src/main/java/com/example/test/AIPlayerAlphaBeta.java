package com.example.test;

import com.example.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AIPlayerAlphaBeta extends Hero{
    private int maxDepth;
    private int nodesExplored = 0;
    private int nodesPruned = 0;

    public AIPlayerAlphaBeta(String name, int maxHP, int maxMP, Point position, int attack, int defense, int maxDepth) {
        super(name, maxHP, maxMP, position, attack, defense);
        this.maxDepth = maxDepth;
    }

    public String chooseBestActionAlphaBeta(int currentTurn, Hero opponent) {
        nodesExplored = 0;
        nodesPruned = 0;

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTurn);
        List<GameState> children = generateSuccessors(root, true);

        if (children.isEmpty()) {
            return "Basic Attack";
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (GameState child : children) {
            int score = alphaBeta(child, maxDepth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        // System.out.println("[Alpha-Beta] Nodes explored: " + nodesExplored + ", Pruned: " + nodesPruned);
        return bestMove != null ? bestMove.getName() : "Basic Attack";
    }

    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        nodesExplored++;

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
                if (beta <= alpha) {
                    nodesPruned++;
                    break; // PRUNING!
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    nodesPruned++;
                    break; // PRUNING!
                }
            }
            return minEval;
        }
    }

    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> successors = new ArrayList<>();
        Hero current = maxPlayer ? state.aiHero : state.playerHero;
        Hero target = maxPlayer ? state.playerHero : state.aiHero;
        int nextTurn = state.turn + 1;

        Hero aiBase = deepCopy(state.aiHero);
        Hero plBase = deepCopy(state.playerHero);

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

                    if (user.useSkillDeterministic(skill.getName(), state.turn, targ, expectedDamage)) {
                        successors.add(new GameState(aiCopy, plCopy, nextTurn,
                                new Move(skill.getName(), expectedDamage, nextTurn)));
                    }
                }
            }
        }

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

        return successors;
    }

    private int evaluate(GameState s) {
        Hero ai = s.aiHero;
        Hero pl = s.playerHero;

        if (ai.getHp() <= 0) return -1000000;
        if (pl.getHp() <= 0) return 1000000;

        int score = 0;
        double dist = ai.getPosition().distanceTo(pl.getPosition());

        score += ai.getHp() * 50;
        score -= pl.getHp() * 50;
        score += ai.getMp() * 15;
        score -= pl.getMp() * 12;

        if (ai.isDefending()) score += 2000;
        if (pl.isDefending()) score -= 1500;

        if (dist <= 3.0) score -= 800;
        if (dist > 8.0) score -= 400;

        return score;
    }

    private Hero deepCopy(Hero original) {
        HeroType type = getHeroType(original);
        Hero copy = Hero.getHero(type, original.getName(),
                new Point(original.getPosition().getX(), original.getPosition().getY()));

        copy.setHp(original.getHp());
        copy.setMp(original.getMp());
        copy.setDefense(original.getDefense());
        if (original.isDefending()) copy.setDefending(true);
        copy.setAttackRange(original.getAttackRange());

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
}
