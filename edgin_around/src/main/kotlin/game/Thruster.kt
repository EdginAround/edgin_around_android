package com.edgin.around.game

import android.os.SystemClock
import com.edgin.around.api.actors.ActorId
import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class MotiveContext(
    var scene: Scene,
    var world: WorldExpositor,
    var gui: Gui
)

class Thruster(
    scene: Scene,
    world: WorldExpositor,
    gui: Gui
) {
    var generalMotives: MutableList<Motive> = mutableListOf()
    var actorMotives: MutableMap<ActorId, Motive> = mutableMapOf()
    var prevTick = SystemClock.uptimeMillis()
    var context = MotiveContext(scene, world, gui)
    var lock = ReentrantLock()

    fun add(motive: Motive) {
        lock.withLock {
            val actorId = motive.getActorId()
            if (actorId != null) {
                actorMotives.put(actorId, motive)
            } else {
                generalMotives.add(motive)
            }
            return@withLock
        }
    }

    fun thrust() {
        val now = SystemClock.uptimeMillis()
        val tickInterval = now - prevTick

        lock.withLock {
            removeExpiredMotives()

            for (motive in generalMotives) {
                motive.tick(tickInterval, context)
            }

            for (motive in actorMotives.values) {
                motive.tick(tickInterval, context)
            }
        }

        prevTick = now
    }

    private fun removeExpiredMotives() {
        generalMotives.removeAll { it.expired() }
        actorMotives.entries.removeAll { it.value.expired() }
    }
}
