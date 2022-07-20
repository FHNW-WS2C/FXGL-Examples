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
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static java.lang.Double.min;
import static java.lang.Math.max;

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
        vars.put("score", 0.0);
        vars.put("speed", 0);
        vars.put("levelLabel", "Race: gain 50 points for Level 2");
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new RaceFactory());
        initLevel();
    }

    public static void initLevel() {
        FXGL.spawn("Background", new SpawnData(0, 0).put("width", WIDTH).put("height", HEIGHT));
        setLevelFromMapOrGameOver();
    }

    private static void setLevelFromMapOrGameOver() {
        try {
            FXGL.setLevelFromMap("race/level" + FXGL.geti("level") + ".tmx");
        } catch (IllegalArgumentException e) {
            gameOver(true);
        }
    }

    private static void gameOver(boolean reachedEndOfGame) {
        StringBuilder builder = new StringBuilder();
        builder.append("Game Over!\n\n");
        if (reachedEndOfGame) {
            builder.append("You have reached the end of the game!\n\n");
        }
        builder.append("Final score: ")
                .append(FXGL.getd("score"))
                .append("\nFinal level: ")
                .append(FXGL.geti("level"));
        FXGL.getDialogService().showMessageBox(builder.toString(), () -> FXGL.getGameController().gotoMainMenu());
    }

    @Override
    protected void initUI() {

        Label levelLabel = new Label();
        levelLabel.setTextFill(Color.BLACK);
        levelLabel.setFont(Font.font(20.0));
        levelLabel.textProperty().bind(FXGL.getsp("levelLabel"));
        FXGL.addUINode(levelLabel, 610, 50);

        Label scoreLabel = new Label();
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setFont(Font.font(20.0));
        scoreLabel.textProperty().bind(FXGL.getdp("score").asString("Score: %.0f"));
        FXGL.addUINode(scoreLabel, 610, 100);

        Label speedLabel = new Label();
        speedLabel.setTextFill(Color.BLACK);
        speedLabel.setFont(Font.font(20.0));
        speedLabel.textProperty().bind(FXGL.getip("speed").asString("Speed: %d"));
        FXGL.addUINode(speedLabel, 610, 130);

    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = FXGL.getPhysicsWorld();
        physics.setGravity(0, 0);

        physics.addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ROAD) {

            @Override
            protected void onCollisionBegin(Entity player, Entity road) {
                FXGL.set("speed",
                        (int) Math.round(min(FXGL.getip("speed").add(30).doubleValue(), 100.0))
                );
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity road) {
                FXGL.set("speed",
                        (int) Math.round(max(FXGL.getip("speed").add(-30).doubleValue(), 0.0))
                );
            }
        });


        physics.addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                Point2D collisionPoint;
                double rotation = Math.floorMod((int) player.getRotation(), 360); //with % you get the remainder, thus can be negative!
                if (rotation<=45 || rotation >=325) {
                    collisionPoint = player.getPosition().add(5, 0);
                } else if (rotation <135) { //rotation>45 && rotation <135
                    collisionPoint = player.getPosition().add(10, 10);
                } else if (rotation>225) { //rotation>225 && rotation <325
                    collisionPoint = player.getPosition().add(-10, 0);
                } else {
                    collisionPoint = player.getPosition().add(-5, 20);
                }
                FXGL.inc("score", -25.0);
                FXGL.spawn("dust", collisionPoint);
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
