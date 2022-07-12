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
        Vec2 dir = Vec2.fromAngle(entity.getRotation() - 90)
                .mulLocal(FXGL.getip("speed").doubleValue());
        FXGL.set("score", FXGL.getdp("score").add(0.05).doubleValue());

        physics.setLinearVelocity(dir.x, dir.y);

    }


    @Override
    public void onUpdate(double tpf) {
        if (FXGL.getip("speed").doubleValue() > 0.0) {
            move();
            //TODO: loop car sound if moving
            //FXGL.play("race/car.wav");   plays on each frame multiple times
        }
    }

    public void straight() {
        angularVelocity = 0.0F;
        physics.getBody().setAngularVelocity(angularVelocity);
    }

    public void up() {
        FXGL.set("speed",
                (int) Math.round(min(FXGL.getip("speed").add(10).doubleValue(), 100.0))
        );
    }

    public void down() {
        FXGL.set("speed",
                (int) Math.round(max(FXGL.getip("speed").add(-10).doubleValue(), 0.0))
        );
        //FIXME: car still moves when speed reduced to 0
        if(FXGL.getip("speed").doubleValue() < 9 && FXGL.getip("speed").doubleValue() > -15){
            straight();
            FXGL.set("speed", 0);
        }
    }

    public void respawn() {
        entity.removeFromWorld();
        FXGL.spawn("Player", new SpawnData(100, 100));
    }
}
