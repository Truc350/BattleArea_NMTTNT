package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AIPlayer extends Hero {
    private static final int MAX_DEPTH = 7;

    // Transposition table để cache các state đã tính
    private static class TranspositionEntry {
        int score;
        int depth;
        TranspositionEntry(int score, int depth) {
            this.score = score;
            this.depth = depth;
        }
    }
    private HashMap<String, TranspositionEntry> transpositionTable = new HashMap<>();

    public AIPlayer(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    /**
     * Chọn action tốt nhất cho lượt hiện tại
     * @param currentTurn Số lượt hiện tại
     * @param opponent Đối thủ (Player)
     * @param game Game instance
     * @return Tên action được chọn
     */
    public String chooseBestAction(int currentTurn, Hero opponent, Game game) {
        double distance = this.getPosition().distanceTo(opponent.getPosition());
        double myRange = this.getAttackRange();
        System.out.println("   [AI] Khoảng cách đến player: " + distance);
        System.out.println("   [AI] Tầm đánh: 6.0");
        System.out.println("   [AI] Trong tầm? " + (distance <= 6.0));

        // Nếu không trong tầm, di chuyển lại gần
        if (distance > myRange) {
            System.out.println("   [AI] NGOÀI tầm đánh, đang di chuyển lại gần");
            return "Move Closer";
        }

        System.out.println("   [AI] ✓ Trong tầm đánh, đang tính toán hành động tốt nhất...");

        // Clear cache mỗi lượt mới
        transpositionTable.clear();

        // Tạo root state
        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTurn);
        List<GameState> children = generateSuccessors(root, true);

        System.out.println("   [AI] Generated " + children.size() + " possible actions");

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // Tìm move tốt nhất
        for (GameState child : children) {
            int score = alphaBeta(child, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            System.out.println("   [AI]   → " + child.getMove().getName() + " = " + score);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        System.out.println("🤖 AI chọn: " + bestMove.getName() + " | Điểm dự đoán: " + bestScore);

        // ✅ CHỈ TRẢ VỀ TÊN - KHÔNG EXECUTE
        return bestMove.getName();
    }

    /**
     * Thuật toán Minimax với Alpha-Beta pruning
     */
    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        // Check transposition table
        String stateKey = getStateKey(state);
        TranspositionEntry cached = transpositionTable.get(stateKey);
        if (cached != null && cached.depth >= depth) {
            return cached.score;
        }

        // Terminal conditions
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
                if (beta <= alpha) break; // Alpha-Beta cutoff
            }
            transpositionTable.put(stateKey, new TranspositionEntry(maxEval, depth));
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) break; // Alpha-Beta cutoff
            }
            transpositionTable.put(stateKey, new TranspositionEntry(minEval, depth));
            return minEval;
        }
    }

    /**
     * Tạo key cho state để cache
     */
    private String getStateKey(GameState state) {
        return String.format("%d_%d_%d_%d_%.1f",
                state.aiHero.getHp(), state.aiHero.getMp(),
                state.playerHero.getHp(), state.playerHero.getMp(),
                state.aiHero.getPosition().distanceTo(state.playerHero.getPosition()));
    }

    /**
     * Tạo tất cả state con có thể từ state hiện tại
     */
    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> successors = new ArrayList<>();
        Hero current = maxPlayer ? state.aiHero : state.playerHero;
        Hero target = maxPlayer ? state.playerHero : state.aiHero;
        int nextTurn = state.turn + 1; // ✓ Tăng lượt

        // Base copies
        Hero aiBase = deepCopy(state.aiHero);
        Hero plBase = deepCopy(state.playerHero);

        // 1. TẤT CẢ SKILLS
        for (Skill skill : current.getSkills()) {
            if (skill.canUse(state.turn, current.getMp())) {
                Hero aiCopy = deepCopy(aiBase);
                Hero plCopy = deepCopy(plBase);
                Hero user = maxPlayer ? aiCopy : plCopy;
                Hero targ = maxPlayer ? plCopy : aiCopy;

                // Tính damage kỳ vọng (không random)
                int expectedDamage = skill.getDamage();

                // Nếu là Marksman, tính damage với crit rate
                if (user instanceof Marksman) {
                    expectedDamage = (int)(expectedDamage * 1.3); // 30% crit → avg 1.3x
                }

                if (user.useSkillDeterministic(skill.getName(), state.turn, targ, expectedDamage)) {
                    successors.add(new GameState(aiCopy, plCopy, nextTurn,
                            new Move(skill.getName(), expectedDamage, nextTurn)));
                }
            }
        }

        // 2. MOVE CLOSER
        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            Hero targ = maxPlayer ? plCopy : aiCopy;
            user.getPosition().moveToward(targ.getPosition(), Point.MOVE_SPEED);
            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Move Closer", 0, nextTurn)));
        }

        // 3. MOVE AWAY
        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(plBase);
            Hero user = maxPlayer ? aiCopy : plCopy;
            Hero targ = maxPlayer ? plCopy : aiCopy;
            user.moveAway(targ, Point.MOVE_SPEED);
            successors.add(new GameState(aiCopy, plCopy, nextTurn,
                    new Move("Move Away", 0, nextTurn)));
        }

        // 4. JUMP UP (lùi xa gấp đôi + bonus MP)
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

        // Move ordering cho maxPlayer (damage cao → ưu tiên)
        if (maxPlayer) {
            successors.sort((a, b) -> Integer.compare(b.getMove().getDamage(), a.getMove().getDamage()));
        }

        return successors;
    }

    /**
     * Hàm đánh giá state - Càng cao càng tốt cho AI
     */
    private int evaluate(GameState s) {
        Hero ai = s.aiHero;
        Hero pl = s.playerHero;

        // Terminal states
        if (ai.getHp() <= 0) return -1000000;
        if (pl.getHp() <= 0) return 1000000;

        int score = 0;
        double dist = ai.getPosition().distanceTo(pl.getPosition());

        // === 1. HP - Quan trọng nhất (scale: ±4000) ===
        score += ai.getHp() * 40;
        score -= pl.getHp() * 40;

        // === 2. Mana - Quan trọng thứ 2 (scale: ±1000) ===
        score += ai.getMp() * 10;
        score -= pl.getMp() * 8;

        // === 3. Cơ hội giết (scale: 0-5000) ===
        if (pl.getHp() <= 40) score += 2000;
        if (pl.getHp() <= 25) score += 3000;
        if (pl.getHp() <= 15) score += 5000;

        // === 4. Nguy cơ chết (scale: 0-5000) ===
        if (ai.getHp() <= 40) score -= 2000;
        if (ai.getHp() <= 25) score -= 3000;
        if (ai.getHp() <= 15) score -= 5000;

        // === 5. Ưu tiên ultimate khi có mana (scale: 0-2000) ===
        if (ai.getMp() >= 22) score += 800;
        if (ai.getMp() >= 30) score += 1200;

        // === 6. Ưu tiên regen khi yếu (scale: 0-1500) ===
        if ((ai.getHp() <= 50 && ai.getMp() < 30) || ai.getMp() <= 15) {
            score += 1500;
        }

        // === 7. Khoảng cách chiến thuật (scale: ±1500) ===
        if (dist <= 1.2) {
            score -= 1500; // Quá gần = nguy hiểm
        } else if (dist <= 2.0) {
            score += 500;  // Tầm đánh = tốt
        } else if (dist <= 3.5) {
            score += 800;  // An toàn
        } else {
            score -= 300;  // Quá xa = lãng phí
        }

        // === 8. Bonus khi kiting với low HP ===
        if (ai.getHp() <= 40 && dist > 3.0) {
            score += 1000;
        }

        // === 9. Penalty khi player có nhiều MP (nguy hiểm) ===
        if (pl.getMp() >= 25) score -= 500;
        if (pl.getMp() >= 40) score -= 1000;

        return score;
    }

    /**
     * Deep copy một Hero để tạo state mới
     */
    private Hero deepCopy(Hero original) {
        Hero copy = Hero.getHero(getHeroType(original), original.getName(),
                new Point(original.getPosition().getX(), original.getPosition().getY()));

        copy.setHp(original.getHp());
        copy.setMp(original.getMp());
        copy.setDefense(original.getDefense());
        copy.setDefending(original.isDefending());


        copy.getSkills().clear();

        for (Skill sk : original.getSkills()) {
            Skill newSkill = new Skill(sk.getName(), sk.getMpCost(), sk.getCooldownTurns(),
                    sk.getDamage(), sk.getHealHP(), sk.getHealMP());
            newSkill.setLastUsedTurn(sk.getLastUsedTurn());
            copy.getSkills().add(newSkill);
        }

        return copy;
    }

    /**
     * Lấy HeroType từ instance
     */
    private HeroType getHeroType(Hero hero) {
        if (hero instanceof Fighter) return HeroType.FIGHTER;
        if (hero instanceof Marksman) return HeroType.MARKSMAN;
        if (hero instanceof Mage) return HeroType.MAGE;
        if (hero instanceof Support) return HeroType.SUPPORT;
        return HeroType.FIGHTER;
    }

    /**
     * Thực hiện DI CHUYỂN trong game (không dùng cho skill)
     * Chỉ gọi từ BattleController khi AI chọn Move/Jump
     */
    public void executeMovement(String moveName, Hero opponent) {
        if ("Move Closer".equals(moveName)) {
            this.getPosition().moveToward(opponent.getPosition(), Point.MOVE_SPEED);
            System.out.println("   → AI tiến lại gần");
        } else if ("Move Away".equals(moveName)) {
            this.moveAway(opponent, Point.MOVE_SPEED);
            System.out.println("   → AI lùi xa");
        } else if ("Jump Up".equals(moveName)) {
            this.moveAway(opponent, Point.MOVE_SPEED * 2);
            if (this.getMp() < 20) {
                this.setMp(Math.min(100, this.getMp() + 5));
            }
            System.out.println("   → AI nhảy lùi (x2 distance)");
        }
    }
}