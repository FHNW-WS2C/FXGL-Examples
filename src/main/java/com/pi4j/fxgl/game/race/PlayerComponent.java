package com.pi4j.fxgl.game.race;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static java.lang.Double.max;
import static java.lang.Double.min;

public class PlayerComponent extends Component {

    private PhysicsComponent physics;
    private static double Speed = 0.0;

    private static float angularVelocity = 0.0F;

    @Override
    public void onAdded() {
        super.onAdded();
        physics = entity.getComponent(PhysicsComponent.class);
        entity.getTransformComponent().setScaleOrigin(entity.getCenter());
    }

    public void rotateLeft() {
        angularVelocity = (float) min(angularVelocity+0.2F, 2.0F);
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void rotateRight() {
        angularVelocity = (float) max(angularVelocity-0.2F, -2.0F);
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void move() {
        Vec2 dir = Vec2.fromAngle(entity.getRotation() - 90)
                .mulLocal(Speed);
        FXGL.inc("score", 1);


        physics.setLinearVelocity(dir.x, dir.y);

    }


    @Override
    public void onUpdate(double tpf) {
        if (Speed > 0.0) {
            move();
         //   FXGL.play("race/car.wav");
        }
    }

    public void left() {
        rotateLeft();
    }

    public void right() {
        rotateRight();
    }

    public void straight() {
        angularVelocity = 0.0F;
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void up() {
        Speed = min(Speed+10, 100.0);
    }

    public void down() {
        Speed = max(Speed-10, 0.0);
    }

    public void respawn() {
        entity.removeFromWorld();
        FXGL.spawn("Player", new SpawnData(100, 100));
    }
}
