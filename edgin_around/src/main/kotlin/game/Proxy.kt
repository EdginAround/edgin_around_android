package com.edgin.around.game

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.craft.Assembly
import com.edgin.around.api.enums.Hand
import com.edgin.around.api.enums.UpdateVariant
import com.edgin.around.api.moves.ConcludeMove
import com.edgin.around.api.moves.CraftMove
import com.edgin.around.api.moves.HandActivationMove
import com.edgin.around.api.moves.InventoryUpdateMove
import com.edgin.around.api.moves.Move
import com.edgin.around.api.moves.StartMotionMove
import com.edgin.around.api.moves.StopMove
import java.io.OutputStream

/** Tool class for sending messages (`moves`) to the server. */
class Proxy(val stream: OutputStream) {
    public fun sendConclude() {
        sendMove(ConcludeMove())
    }

    public fun sendCraft(assembly: Assembly) {
        sendMove(CraftMove(assembly))
    }

    public fun sendHandActivation(hand: Hand, objectId: ActorId?) {
        sendMove(HandActivationMove(hand, objectId))
    }

    public fun sendInventoryUpdate(hand: Hand, inventoryIndex: Int, updateVariant: UpdateVariant) {
        sendMove(InventoryUpdateMove(hand, inventoryIndex, updateVariant))
    }

    public fun sendMotion(bearing: Float) {
        sendMove(StartMotionMove(bearing))
    }

    public fun sendStop() {
        sendMove(StopMove())
    }

    protected fun sendMove(move: Move) {
        stream.write(move.serialize().toByteArray())
        stream.write("\n".toByteArray())
    }
}
