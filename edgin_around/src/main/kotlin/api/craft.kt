package com.edgin.around.api.craft

import com.edgin.around.api.actors.ActorId

enum class Essence(val value: String) {
    // Raw materials
    ROCKS("rocks"),
    GOLD("gold"),
    MEAT("meat"),
    LOGS("log"),
    STICKS("sticks"),

    // Clothing
    HAT("hat"),
    COAT("coat"),
    GLOVES("gloves"),
    SHOES("shoes"),
    BELT("belt"),
    BOTTOM_WEAR("bottom_wear"),
    UPPER_WEAR("upper_wear"),
    BAG("bag"),

    // Other
    PLANT("plant"),
    HERO("hero"),
    TOOL("tool"),

    // Default category
    VOID("void")
}

data class Item(
    val actor_id: ActorId,
    val essence: Essence,
    val quantity: Int
)

data class Assembly(
    val recipe_codename: String,
    val sources: ArrayList<Item>
)
