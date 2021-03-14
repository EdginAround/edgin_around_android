package com.edgin.around.api.inventory

import com.google.gson.annotations.SerializedName

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.enums.Hand

data class EntityInfo (
    @SerializedName("id")
    val id: ActorId,

    @SerializedName("essence")
    val essence: String,

    @SerializedName("current_quantity")
    val currentQuantity: Int,

    @SerializedName("item_volume")
    val itemVolume: Int,

    @SerializedName("max_volume")
    val maxVolume: Int,

    @SerializedName("codename")
    val codename: String
)

data class Inventory (
    @SerializedName("left_hand")
    val leftHand: EntityInfo?,

    @SerializedName("right_hand")
    val rightHand: EntityInfo?,

    @SerializedName("entities")
    val entities: Array<EntityInfo?>
) {
    fun getHand(hand: Hand): EntityInfo? {
        return when (hand.value) {
            Hand.LEFT.value -> leftHand
            Hand.RIGHT.value -> rightHand
            else -> null
        }
    }

    fun getAllIds(): ArrayList<ActorId> {
        var result: ArrayList<ActorId> = arrayListOf()

        if (leftHand != null) {
            result.add(leftHand.id)
        }

        if (rightHand != null) {
            result.add(rightHand.id)
        }

        for (entity in entities) {
            if (entity != null) {
                result.add(entity.id)
            }
        }

        return result
    }
}

