package com.pi4j.fxgl.game.race;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class RaceFactory implements EntityFactory {

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.BLACK))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }


    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.0f));
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);
        bd.setType(BodyType.DYNAMIC);
        physics.setBodyDef(bd);

        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER)
                .bbox(new HitBox(BoundingShape.box(10, 18)))
                .view("playerRace.png")
                .with(physics)
                .with(new PlayerComponent())
                .collidable()
                .build();
    }


    @Spawns("Wall")
    public Entity spawnWall(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.WALL)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("Road")
    public Entity spawnRoad(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.ROAD)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .collidable()
                .build();
    }

    @Spawns("dust")
    public Entity newExplosion(SpawnData data) {
        FXGL.play("race/crash.wav");

        var emitter = ParticleEmitters.newExplosionEmitter(15);
        emitter.setMaxEmissions(1);
        emitter.setSize(1, 5);
        emitter.setStartColor(Color.ROSYBROWN);
        emitter.setEndColor(Color.BROWN);
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0));

        return entityBuilder(data)
                .with(new ExpireCleanComponent(Duration.seconds(0.66)))
                .with(new ParticleComponent(emitter))
                .build();
    }

}
