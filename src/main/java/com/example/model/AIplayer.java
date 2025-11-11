package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class AIplayer extends Hero {
    private int maxDepth = 2;

    public AIplayer(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Rage Strike", 15, 8, attack + 10, 0, 0));
        skills.add(new Skill("Fury Burst", 20, 12, attack + 20, 0, 0));
        skills.add(new Skill("Ultimate Rage", 30, 20, attack * 2, 0, 0));
    }

    public String chooseBestAction(long currentTime, Hero opponent) {
        int alpha = Integer.MIN_VALUE; // Giá trị tối đa AI có thể đạt
        int beta = Integer.MAX_VALUE;  // Giá trị tối thiểu Player có thể ép
        String bestAction = "Wait";
        int bestValue = Integer.MIN_VALUE;

        List<Skill> availableSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.canUse(currentTime, getMp())) availableSkills.add(skill);
        }

        for (Skill skill : availableSkills) {
            // Tạm tính trạng thái sau khi dùng skill
            int value = minimax(currentTime, opponent, 0, true, alpha, beta, skill.getName());
            if (value > bestValue) {
                bestValue = value;
                bestAction = skill.getName();
            }
            alpha = Math.max(alpha, bestValue); // Cập nhật alpha
            if (beta <= alpha) break; // Cắt tỉa
        }

        if (!bestAction.equals("Wait")) {
            useSkill(bestAction, currentTime, opponent);
            System.out.println(name + " chọn " + bestAction);
        }
        return bestAction;
    }

    private int minimax(long currentTime, Hero opponent, int depth, boolean isMaximizing, int alpha, int beta, String action) {
        if (depth >= maxDepth || opponent.getHp() <= 0 || getHp() <= 0) {
            return evaluateState(opponent, this);
        }
        int value;
        if (isMaximizing) {
            value = Integer.MIN_VALUE;
            for (Skill skill : skills) {
                if (skill.canUse(currentTime, getMp())) {
                    Hero aiCopy = copyHero(this);
                    Hero oopCopy = copyHero(opponent);
                    aiCopy.useSkill(skill.getName(), currentTime, oopCopy);
                    value = Math.max(value, minimax(currentTime, oopCopy, depth + 1, false, alpha, beta, skill.getName()));
                    alpha = Math.max(alpha, beta);
                    if (alpha >= beta) break;
                }
            }
            return value;
        } else {
            value = Integer.MAX_VALUE;
            for (Skill skill : opponent.skills) {
                if (skill.canUse(currentTime, opponent.getMp())) {
                    Hero aiCopy = copyHero(this);
                    Hero oopCopy = copyHero(opponent);
                    oopCopy.useSkill(skill.getName(), currentTime, aiCopy);
                    value = Math.min(value, minimax(currentTime, oopCopy, depth + 1, true, alpha, beta, skill.getName()));
                    beta = Math.min(beta, value);
                    if (alpha >= beta) break;
                }
            }
            return value;
        }
    }

    private Hero copyHero(Hero original) {
        Hero copy = new Fighter(original.getName(), original.getMaxHP(), original.getMaxMP(), original.position, original.attack, original.defense);
        copy.hp = original.hp;
        copy.mp = original.mp;
        copy.skills = new ArrayList<>(original.skills);
        return copy;
    }

    private int evaluateState(Hero opponent, AIplayer aIplayer) {
        // đánh giá trạng thái : ƯU tiên HP AI cao , MP đủ để dùng chiêu
        int score = 0;
        score += aIplayer.getHp() * 10;
        score -= opponent.getHp() * 10;
        score += aIplayer.getMp() * 2;
        return score;
    }



}
