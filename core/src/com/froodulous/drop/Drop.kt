package com.froodulous.drop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.app.use
import ktx.math.vec3

class MenuScreen(val game: Drop) : KtxScreen {
    val camera = OrthographicCamera().apply {
        setToOrtho(false, 800f, 480f)
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0.2f, 1f)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        game.font.draw(game.batch, "Welcome to Drop", 100f, 150f)
        game.font.draw(game.batch, "Click anywhere to begin", 100f, 100f)
        game.batch.end()

        if (Gdx.input.isTouched) {
            game.addScreen(GameScreen(game))
            game.setScreen<GameScreen>()
            dispose()
        }
    }
}

class GameScreen(val game: Drop) : KtxScreen {

    val dropImage = Texture(Gdx.files.internal("droplet.png"))
    val bucketImage = Texture(Gdx.files.internal("bucket.png"))

    val dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))!!
    val rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))!!.apply {
        isLooping = true
    }

    val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    val bucket = Rectangle().apply {
        x = 800F / 2F - 64F / 2F
        y = 20F
        width = 64F
        height = 64F
    }

    val raindrops = Array<Rectangle>()

    private var dropsGathered = 0

    private var lastDropTime = 0L

    init {
        spawnRaindrop()
    }

    private fun spawnRaindrop() {
        raindrops.add(Rectangle().apply {
            x = MathUtils.random(0F, 800F - 64F)
            y = 480F
            width = 64F
            height = 64F
            lastDropTime = TimeUtils.nanoTime()
        })
    }

    override fun render(delta: Float) {
        // Clear the screen with a dark blue colour.
        clearScreen(0F, 0F, 0.2F, 1F)

        // Update the camera's matrices in case it has moved.
        camera.update()

        // Tell the sprite batch to render in the camera's coordinate  system.
        game.batch.projectionMatrix = camera.combined

        // Use the sprite batch to draw things.
        game.batch.use {
            game.font.draw(it, "Drops Collected: $dropsGathered", 0F, 480F)
            it.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height)
            raindrops.forEach { drop ->
                it.draw(dropImage, drop.x, drop.y, drop.width, drop.height)
            }
        }

        // Process user input.
        if (Gdx.input.isTouched) {
            val touchPos = vec3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 200 * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 200 * delta
        }

        // Keep the bucket on the screen
        if (bucket.x < 0) {
            bucket.x = 0F
        }
        if (bucket.x > 800 - 64) {
            bucket.x = 800F - 64
        }

        // Check if we need a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop()
        }

        // Move the raindrops
        val iterator = raindrops.iterator()
        while (iterator.hasNext()) {
            val drop = iterator.next()
            drop.y -= 200 * delta
            if (drop.y + 64 < 0) {
                iterator.remove()
            }
            if (drop.overlaps(bucket)) {
                dropsGathered++
                dropSound.play()
                iterator.remove()
            }
        }
    }

    override fun show() {
        rainMusic.play()
    }

    override fun dispose() {
        dropSound.dispose()
        bucketImage.dispose()
        dropImage.dispose()
        rainMusic.dispose()
    }
}

class Drop : KtxGame<Screen>() {
    lateinit var font: BitmapFont
    lateinit var batch: SpriteBatch

    override fun create() {
        font = BitmapFont()
        batch = SpriteBatch().apply {
            color = Color.WHITE
        }
        addScreen(MenuScreen(this))
        setScreen<MenuScreen>()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        font.dispose()
    }
}
