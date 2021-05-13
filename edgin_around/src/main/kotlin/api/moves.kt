package com.edgin.around.api.moves

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.craft.Assembly
import com.edgin.around.api.enums.Hand
import com.edgin.around.api.enums.UpdateVariant
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

abstract class Move {
    abstract fun getName(): String

    fun serialize(): String {
        val gson = Gson()
        val jsonElement = gson.toJsonTree(this)
        jsonElement.getAsJsonObject().addProperty("type", getName())
        return jsonElement.toString()
    }
}

data class CraftMove(
    @SerializedName("assembly")
    val assembly: Assembly
) : Move() {
    override fun getName() = "craft"
}

data class HandActivationMove(
    @SerializedName("hand")
    val hand: Hand,

    @SerializedName("object_id")
    val objectId: ActorId?
) : Move() {
    override fun getName() = "hand_activation"
}

data class InventoryUpdateMove(
    @SerializedName("hand")
    val hand: Hand,

    @SerializedName("inventory_index")
    val inventoryIndex: Int,

    @SerializedName("update_variant")
    val updateVariant: UpdateVariant
) : Move() {
    override fun getName() = "inventory_update"
}

data class MotionStartMove(
    @SerializedName("bearing")
    val bearing: Float
) : Move() {
    override fun getName() = "motion_start"
}

class MotionStopMove : Move() {
    override fun getName() = "motion_stop"
}
