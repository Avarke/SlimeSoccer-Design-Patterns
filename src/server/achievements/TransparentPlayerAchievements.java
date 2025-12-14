package server.achievements;

import server.ClientData;

import server.achievements.concreteAchievementsTransparent.TransparentJumpAchievement;
import server.achievements.concreteAchievementsTransparent.TransparentMetaAchievement;
import server.achievements.concreteAchievementsTransparent.TransparentScoreAchievement;
import server.chat.ChatMediator;

import java.util.List;

public class TransparentPlayerAchievements {

    private final AchievementContext context;
    private final TransparentAchievementComponent root;

    public TransparentPlayerAchievements(ClientData player, ChatMediator mediator) {
        this.context = new AchievementContext(player, mediator);
        this.root = buildTree();
    }

    private TransparentAchievementComponent buildTree() {
        // root (transparent composite)
        TransparentAchievementComposite root = new TransparentAchievementComposite("root");

        // -------------------------
        // SCORE subtree: 1 -> 5 -> 10
        // -------------------------
        TransparentScoreAchievement score1 = new TransparentScoreAchievement(
                "score_1",
                "First Goal",
                "You scored your first goal!",
                1,
                null
        );

        TransparentScoreAchievement score5 = new TransparentScoreAchievement(
                "score_5",
                "Goal Machine",
                "You scored 5 goals!",
                5,
                score1
        );

        TransparentScoreAchievement score10 = new TransparentScoreAchievement(
                "score_10",
                "Goal Legend",
                "You scored 10 goals!",
                10,
                score5
        );

        TransparentAchievementComposite scoreGroup = new TransparentAchievementComposite("ScoreGroup");
        scoreGroup.add(score1);
        scoreGroup.add(score5);
        scoreGroup.add(score10);

        // -------------------------
        // MOVEMENT subtree (jump): 1 -> 5 -> 10
        // -------------------------
        TransparentJumpAchievement jump1 = new TransparentJumpAchievement(
                "jump_1",
                "First Jump",
                "You jumped for the first time!",
                1,
                null
        );

        TransparentJumpAchievement jump5 = new TransparentJumpAchievement(
                "jump_5",
                "Bunny Hopper",
                "You jumped 5 times!",
                5,
                jump1
        );

        TransparentJumpAchievement jump10 = new TransparentJumpAchievement(
                "jump_10",
                "Air Acrobat",
                "You jumped 10 times!",
                10,
                jump5
        );

        TransparentAchievementComposite movementGroup = new TransparentAchievementComposite("MovementGroup");
        movementGroup.add(jump1);
        movementGroup.add(jump5);
        movementGroup.add(jump10);

        // root has two subtrees
        root.add(scoreGroup);
        root.add(movementGroup);

        // -------------------------
        // META achievement (unlocks when both subtrees are fully unlocked)
        // -------------------------
        TransparentMetaAchievement meta = new TransparentMetaAchievement(
                "meta_all",
                "Match Master",
                "You unlocked every achievement in this match!",
                List.of(scoreGroup, movementGroup)
        );

        root.add(meta);

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

    public TransparentAchievementComponent getRoot() {
        return root;
    }
}
