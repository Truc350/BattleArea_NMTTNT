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
//    public  String chooseBestAction(long currentTime, Hero opponent){
//        int alpha = Integer.MIN_VALUE;// giá trị tối đa ai có thể  tạo được
//        int beta = Integer.MAX_VALUE;// giá trị tối thiểu Player có thể ép AI
//        String bestAction = "Wait";
//        int bestValue = Integer.MIN_VALUE;
//
//        List<Skill> avalableSkills = new ArrayList<>();
//        for(Skill skill: skills){
//            if (skill.canUse(currentTime, getMp())){
//                // tam tinh trang thái sau khi dùng skill
//                int value  = minimax(currentTime, opponent, 0, true, alpha, beta, skill.getName());
//            }
//        }
//
//    }
//
//    private int minimax(long currentTime, Hero opponent, int depth, boolean isMaximizing, int alpha, int beta, String action) {
//        if (depth >= maxDepth || opponent.getHp() <= 0 || getHp() <= 0){
//            return evaluateState(opponent, this);
//        }
//    }

}
