package com.froodulous.drop.client

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.gwt.GwtApplication
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration
import com.froodulous.drop.Drop

class HtmlLauncher : GwtApplication() {

    override fun getConfig(): GwtApplicationConfiguration {
        return GwtApplicationConfiguration(800, 480)
    }

    override fun createApplicationListener(): ApplicationListener {
        return Drop()
    }
}