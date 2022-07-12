package com.pi4j.fxgl.game.race;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class RaceApp extends GameApplication {

    private static final int HEIGHT = 640;
    private static final int WIDTH = 960;

    private static int startLevel = 1;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Race");
        settings.setVersion("chapter 1");
        settings.setMainMenuEnabled(true);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", startLevel);
        vars.put("lives", 3);
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new RaceFactory());
        initLevel();
    }

    private void initLevel() {
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        setLevelFromMapOrGameOver();
    }

    private void setLevelFromMapOrGameOver() {
        try {
            FXGL.setLevelFromMap("race/level" + FXGL.geti("level") + "-final.tmx");
        } catch (IllegalArgumentException e) {
            gameOver(true);
        }
    }

    private void gameOver(boolean reachedEndOfGame) {
        StringBuilder builder = new StringBuilder();
        builder.append("Game Over!\n\n");
        if (reachedEndOfGame) {
            builder.append("You have reached the end of the game!\n\n");
        }
        builder.append("Final score: ")
                .append(FXGL.geti("score"))
                .append("\nFinal level: ")
                .append(FXGL.geti("level"));
        FXGL.getDialogService().showMessageBox(builder.toString(), () -> FXGL.getGameController().gotoMainMenu());
    }

    @Override
    protected void initUI() {

        Label scoreLabel = new Label();
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setFont(Font.font(20.0));
        scoreLabel.textProperty().bind(FXGL.getip("score").asString("Score: %d"));
        FXGL.addUINode(scoreLabel, 610, 100);

    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = FXGL.getPhysicsWorld();
        physics.setGravity(0, 0);

        physics.addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                FXGL.play("race/crash.wav");
                FXGL.inc("score", -100);
            }
        });

    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addAction(turnLeft, KeyCode.A);
        input.addAction(turnRight, KeyCode.D);

        FXGL.onKeyDown(KeyCode.W, "Speed Up",    () -> getPlayer().getComponent(PlayerComponent.class).up());
        FXGL.onKeyDown(KeyCode.S, "Slow Down",  () -> getPlayer().getComponent(PlayerComponent.class).down());
    }

    UserAction turnLeft = new UserAction("turnLeft") {
        @Override
        protected void onActionBegin() {
            //nop
        }

        @Override
        protected void onAction() {
            getPlayer().getComponent(PlayerComponent.class).left();
        }

        @Override
        protected void onActionEnd() {
            getPlayer().getComponent(PlayerComponent.class).straight();
        }
    };

    UserAction turnRight = new UserAction("turnRight") {
        @Override
        protected void onActionBegin() {
            //nop
        }

        @Override
        protected void onAction() {
            getPlayer().getComponent(PlayerComponent.class).right();
        }

        @Override
        protected void onActionEnd() {
            getPlayer().getComponent(PlayerComponent.class).straight();
        }
    };

    private static Entity getPlayer() {
        return FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            startLevel = Integer.parseInt(args[0]);
        }
        launch(args);
    }
}
