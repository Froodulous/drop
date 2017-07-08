package com.froodulous.drop.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.froodulous.drop.Drop

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.title = "Drop"
    config.width = 800
    config.height = 480
    LwjglApplication(Drop(), config)

}
