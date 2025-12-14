package server.achievements;

import server.ClientData;
import server.achievements.concreteAchievements.JumpAchievement;
import server.achievements.concreteAchievements.MetaAchievement;
import server.achievements.concreteAchievements.ScoreAchievement;
import server.chat.ChatMediator;
import server.chat.GameChatMediator;

public class PlayerAchievements {
    private final AchievementContext context;
    private final AchievementComponent root;

    public PlayerAchievements(ClientData player, ChatMediator mediator) {
        this.context = new AchievementContext(player, mediator);
        this.root = buildTree();
    }

    private AchievementComponent buildTree() {
        AchievementComposite root = new AchievementComposite("root");

        // ---- Scoring chain: 1 → 5 → 10 ----
        ScoreAchievement score1  = new ScoreAchievement(
                "score_1",
                "First Goal",
                "You scored your first goal!",
                1,
                null
        );

        ScoreAchievement score5  = new ScoreAchievement(
                "score_5",
                "Goal Machine",
                "You scored 5 goals!",
                5,
                score1
        );

        ScoreAchievement score10 = new ScoreAchievement(
                "score_10",
                "Goal Legend",
                "You scored 10 goals!",
                10,
                score5
        );

        AchievementComposite scoreGroup = new AchievementComposite("ScoreGroup");
        scoreGroup.addChild(score1);
        scoreGroup.addChild(score5);
        scoreGroup.addChild(score10);

        // ---- Jump chain: 1 → 5 → 10 ----
        JumpAchievement jump1 = new JumpAchievement(
                "jump_1",
                "First Jump",
                "You jumped for the first time!",
                1,
                null
        );

        JumpAchievement jump5 = new JumpAchievement(
                "jump_5",
                "Bunny Hopper",
                "You jumped 5 times!",
                5,
                jump1
        );

        JumpAchievement jump10 = new JumpAchievement(
                "jump_10",
                "Air Acrobat",
                "You jumped 10 times!",
                10,
                jump5
        );

        AchievementComposite movementGroup = new AchievementComposite("JumpGroup");
        movementGroup.addChild(jump1);
        movementGroup.addChild(jump5);
        movementGroup.addChild(jump10);

        // root has two subtrees
        root.addChild(scoreGroup);
        root.addChild(movementGroup);

        MetaAchievement meta = new MetaAchievement(
                "meta_all",
                "Match Master",
                "You unlocked every achievement in this match!",
                java.util.List.of(scoreGroup, movementGroup)
        );

        root.addChild(meta);


        return root;
    }

    // Called from game logic when events happen

    public void onPlayerScored() {
        context.incrementScore();
        root.onEvent(AchievementEventType.SCORE, context);
    }

    public void onPlayerJumped() {
        context.incrementJump();
        root.onEvent(AchievementEventType.JUMP, context);
    }
}
