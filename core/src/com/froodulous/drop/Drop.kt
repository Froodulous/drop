package com.froodulous.drop

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils

class Drop : ApplicationAdapter() {
    lateinit var dropImg: Texture
    lateinit var bucketImg: Texture
    lateinit var dropSound: Sound
    lateinit var rainMusic: Music
    lateinit var camera: OrthographicCamera
    lateinit var batch: SpriteBatch
    lateinit var bucket: Rectangle
    val touchPos: Vector3 = Vector3()
    val raindrops: Array<Rectangle> = Array()
    var lastDropTime = 0L

    override fun create() {

        dropImg = Texture("droplet.png")
        bucketImg = Texture("bucket.png")

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))

        rainMusic.isLooping = true
        rainMusic.play()

        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 480f)
        batch = SpriteBatch()

        bucket = Rectangle(800f / 2f - 64f / 2f, 20f, 64f, 64f)
        bucket.x = 800f / 2f - 64f / 2f
        bucket.y = 20f
        bucket.width = 64f
        bucket.height = 64f

        spawnRaindrop()

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bucketImg, bucket.x, bucket.y)
        raindrops.forEach { batch.draw(dropImg, it.x, it.y) }
        batch.end()

        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x, Gdx.input.y, 0)
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.deltaTime

        if (bucket.x < 0) bucket.x = 0f
        if (bucket.x > 800 - 64) bucket.x = 800f - 64f

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop()

        raindrops.forEachIndexed { index, raindrop ->
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) raindrops.removeIndex(index)
            else if (raindrop.overlaps(bucket)) {
                dropSound.play()
                raindrops.removeIndex(index)
            }
        }

    }

    override fun dispose() {
        dropSound.dispose()
        dropImg.dispose()
        rainMusic.dispose()
        bucketImg.dispose()
        batch.dispose()
    }
}

fun Drop.spawnRaindrop() {
    raindrops.add(Rectangle(MathUtils.random(0f, 800f - 64f), 480f, 64f, 64f))
    lastDropTime = TimeUtils.nanoTime()

}

private fun Vector3.set(x: Int, y: Int, z: Int) {
    this.set(x.toFloat(), y.toFloat(), z.toFloat())
}
