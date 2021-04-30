package com.edgin.around.api.enums

import com.google.gson.annotations.SerializedName

enum class DamageVariant {
    @SerializedName("hit")
    HIT,

    @SerializedName("chop")
    CHOP,

    @SerializedName("smash")
    SMASH,

    @SerializedName("attack")
    ATTACK
}

enum class Hand(val value: Int) {
    LEFT(0),
    RIGHT(1)
}

enum class UpdateVariant(val value: Int) {
    SWAP(0),
    MERGE(1)
}
