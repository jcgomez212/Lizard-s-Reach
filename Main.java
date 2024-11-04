package io.github.lizardsreach;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture playerTexture;
    private float playerX, playerY;
    private float velocityY;
    private final float gravity = -0.5f;
    private final float jumpStrength = 10f;
    private boolean isJumping;

    @Override
    public void create() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("background.png"); // Get city background
        playerTexture = new Texture("player.png"); // Get player character here 
        playerX = 100; // Starting X position of player
        playerY = 100; // Starting Y position (ground level)
        velocityY = 0;
        isJumping = false;
    }

    @Override
    public void render() {
        handleInput();

        // Apply gravity
        if (playerY > 0 || isJumping) {
            velocityY += gravity;
            playerY += velocityY;
        } else {
            playerY = 0; // Reset to ground level when landing
            isJumping = false;
            velocityY = 0;
        }

        // Clear screen, draw the background and player
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0); // Draw background at position (0, 0)
        batch.draw(playerTexture, playerX, playerY); // Draw player at current position
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= 5; // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += 5; // Move right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
            velocityY = jumpStrength; // Jump
            isJumping = true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();
    }
}
