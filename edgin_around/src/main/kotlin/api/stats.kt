package com.edgin.around.api.stats

import com.google.gson.annotations.SerializedName

typealias Stat = Float

data class Stats(
    @SerializedName("hunger")
    val hunger: Stat,

    @SerializedName("max_hunger")
    val maxHunger: Stat
)
