package com.example.test;

import com.example.model.*;

import java.util.ArrayList;
import java.util.List;

public class AIPlayerMinimax extends Hero {

    // Counter để đếm số nodes được explore
    private int nodesExplored = 0;

    public AIPlayerMinimax(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    public String chooseBestActionMinimax(int currentTurn, Hero opponent, int maxDepth) {
        nodesExplored = 0;

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTurn);
        List<GameState> children = generateSuccessors(root, true);

        if (children.isEmpty()) {
            return "Basic Attack";
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (GameState child : children) {
            int score = minimax(child, maxDepth - 1, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        // System.out.println("[Minimax] Nodes explored: " + nodesExplored);
        return bestMove != null ? bestMove.getName() : "Basic Attack";
    }

    private int minimax(GameState state, int depth, boolean maximizingPlayer) {
        nodesExplored++;

        if (depth == 0 || state.isTerminal()) {
            return evaluate(state);
        }

        List<GameState> children = generateSuccessors(state, maximizingPlayer);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (GameState child : children) {
                int eval = minimax(child, depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = minimax(child, depth - 1, true);
                minEval = Math.min(minEval, eval);
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

        // All attack skills
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

        // Heal
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

        // Defend
        if (!current.isDefending()) {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            user.setDefending(true);

            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Defend", 0, nextTurn)));
        }

        // Move actions
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
