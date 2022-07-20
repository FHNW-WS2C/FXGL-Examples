package com.pi4j.fxgl.game.race;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.application.Platform;

import static java.lang.Double.max;
import static java.lang.Double.min;

public class PlayerComponent extends Component {

    private PhysicsComponent physics;

    private static float angularVelocity = 0.0F;

    @Override
    public void onAdded() {
        super.onAdded();
        physics = entity.getComponent(PhysicsComponent.class);
        entity.getTransformComponent().setScaleOrigin(entity.getCenter());
    }

    public void left() {
        angularVelocity = (float) min(angularVelocity+0.2F, 2.0F);
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void right() {
        angularVelocity = (float) max(angularVelocity-0.2F, -2.0F);
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void move() {
        //move
        Vec2 dir = Vec2.fromAngle(entity.getRotation() - 90)
                .mulLocal(FXGL.getip("speed").doubleValue());
        physics.setLinearVelocity(dir.x, dir.y);
        //Score
        FXGL.set("score", FXGL.getdp("score").add(0.05).doubleValue());
        if (FXGL.getd("score")>50.0 && FXGL.geti("level")==1){
            FXGL.set("level", 2);
            FXGL.set("levelLabel", "Level 2 reached: drive faster on the road");
            Platform.runLater(RaceApp::initLevel);
        }
    }


    @Override
    public void onUpdate(double tpf) {
        if (FXGL.getip("speed").doubleValue() > 0.0) {
            move();
        } else {
            stop();
        }
    }

    public void straight() {
        angularVelocity = 0.0F;
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void stop() {
        physics.setLinearVelocity(0, 0);
    }

    public void up() {
        if(FXGL.getip("speed").doubleValue() < 5){
            FXGL.play("race/car.wav");
        }
        FXGL.set("speed",
                (int) Math.round(min(FXGL.getip("speed").add(10).doubleValue(), 100.0))
        );
    }

    public void down() {
        if(FXGL.getip("speed").doubleValue() < 5){
            FXGL.getAudioPlayer().stopAllSounds();
        }
        FXGL.set("speed",
                (int) Math.round(max(FXGL.getip("speed").add(-10).doubleValue(), 0.0))
        );
        if(FXGL.getip("speed").doubleValue() < 5){
            stop();
        }
    }

    public void respawn() {
        entity.removeFromWorld();
        FXGL.spawn("Player", new SpawnData(100, 100));
    }
}
