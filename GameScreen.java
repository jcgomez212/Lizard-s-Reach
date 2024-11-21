package io.github.lizardsreach;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture cityBackground;
    private TextureAtlas lizardAtlas;
    private Animation<TextureRegion> lizardAnimation;
    private TextureRegion currentFrame; // Current frame of animation
    private TextureAtlas flyAtlas;
    private Animation<TextureRegion> flyAnimation;
    private float stateTime;
    private float lizardX, lizardY;
    private float jumpVelocity;
    private boolean isJumping;
    private Array<Fly> flies; // List of flies on screen
    private boolean isAttacking; // To track if the Lizard Man is attacking

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load background texture
        cityBackground = new Texture("cityBackground.png");

        // Load Lizard Man's atlas and animation
        lizardAtlas = new TextureAtlas(Gdx.files.internal("LizardSprite.txt"));
        lizardAnimation = new Animation<>(0.1f, lizardAtlas.getRegions());
        lizardX = Gdx.graphics.getWidth() / 2f - 75; // Initial Lizard Man X position
        lizardY = 50; // Initial Lizard Man Y position
        stateTime = 0f;
        jumpVelocity = 0f;
        isJumping = false;
        isAttacking = false;

        // Load Fly's atlas and animation
        flyAtlas = new TextureAtlas(Gdx.files.internal("FlySprite.txt"));
        flyAnimation = new Animation<>(0.1f, flyAtlas.getRegions());
        flies = new Array<>();
    }

    @Override
    public void render() {
        // Clear the screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Update state time
        stateTime += Gdx.graphics.getDeltaTime();

        // Handle input for Lizard Man movement and jumping
        handleInput();

        // Update current frame based on state
        if (isAttacking) {
            // Use the last frame for attack animation
            currentFrame = lizardAtlas.getRegions().get(lizardAtlas.getRegions().size - 1);
        } else {
            // Update current frame for normal animation
            currentFrame = lizardAnimation.getKeyFrame(stateTime, true);
        }

        // Update flies
        updateFlies();

        // Draw everything
        batch.begin();
        drawBackground();
        drawLizardMan();
        drawFlies();
        batch.end();

        // Update jumping logic
        updateJump();
    }

    private void drawBackground() {
        // Proportionally scale the background
        float screenRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float backgroundRatio = (float) cityBackground.getWidth() / cityBackground.getHeight();

        float drawWidth, drawHeight;
        if (screenRatio > backgroundRatio) {
            drawWidth = Gdx.graphics.getWidth();
            drawHeight = drawWidth / backgroundRatio;
        } else {
            drawHeight = Gdx.graphics.getHeight();
            drawWidth = drawHeight * backgroundRatio;
        }

        batch.draw(cityBackground, 0, 0, drawWidth, drawHeight);
    }

    private void drawLizardMan() {
        TextureRegion currentFrame = lizardAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, lizardX, lizardY, currentFrame.getRegionWidth() * 3, currentFrame.getRegionHeight() * 3); // Scaled by 3x
    }

    private void drawFlies() {
        Iterator<Fly> iterator = flies.iterator();
        while (iterator.hasNext()) {
            Fly fly = iterator.next();
            TextureRegion currentFrame = flyAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, fly.x, fly.y, fly.size, fly.size);

            fly.x += fly.speed * Gdx.graphics.getDeltaTime(); // Move the fly

            if (fly.x > Gdx.graphics.getWidth()) {
                iterator.remove(); // Remove flies that exit the screen
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            lizardX -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            lizardX += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE) && !isJumping) {
            isJumping = true;
            jumpVelocity = 600f; // Adjusted for faster jumping
        }
        // Attack on mouse click
        if (Gdx.input.isTouched()) {
            isAttacking = true;
        } else {
            isAttacking = false;
        }
    }

    private void updateJump() {
        if (isJumping) {
            lizardY += jumpVelocity * Gdx.graphics.getDeltaTime();
            jumpVelocity -= 1200 * Gdx.graphics.getDeltaTime(); // Gravity effect

            if (lizardY <= 50) {
                lizardY = 50;
                isJumping = false;
            }
        }
    }

    private void updateFlies() {
        if (MathUtils.random(0, 100) < 2) { // Random chance to spawn a fly
            float flySize = MathUtils.random(30, 70);
            float flySpeed = MathUtils.random(150, 300);
            float flyY = MathUtils.random(50, Gdx.graphics.getHeight() - flySize - 50);
            flies.add(new Fly(0, flyY, flySize, flySpeed));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        cityBackground.dispose();
        lizardAtlas.dispose();
        flyAtlas.dispose();
    }

    // Inner class for managing fly properties
    private static class Fly {
        float x, y, size, speed;

        Fly(float x, float y, float size, float speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
        }
    }
}
