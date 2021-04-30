package com.edgin.around.api.actions

import com.edgin.around.api.actors.Actor
import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.enums.DamageVariant
import com.edgin.around.api.enums.Hand
import com.edgin.around.api.geometry.Elevation
import com.edgin.around.api.geometry.Point
import com.edgin.around.api.inventory.Inventory
import com.edgin.around.api.stats.Stats
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

open class Action {
    companion object {
        fun prepareGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(Action::class.java, ActionDeserializer())
                .create()
        }
    }
}

data class ConfigurationAction(
    @SerializedName("hero_actor_id")
    val heroActorId: ActorId,

    @SerializedName("elevation")
    val elevation: Elevation
) : Action() {
    companion object {
        val NAME = "configuration"
    }
}

data class CreateActorsAction(
    @SerializedName("actors")
    val actors: Array<Actor>
) : Action() {
    companion object {
        val NAME = "create_actors"
    }
}

data class DeleteActorsAction(
    @SerializedName("actor_ids")
    val actorIds: Array<ActorId>
) : Action() {
    companion object {
        val NAME = "delete_actors"
    }
}

data class MovementAction(
    @SerializedName("actor_id")
    val actorId: ActorId,

    @SerializedName("speed")
    val speed: Float,

    @SerializedName("bearing")
    val bearing: Float,

    @SerializedName("duration")
    val duration: Float
) : Action() {
    companion object {
        val NAME = "movement"
    }

    fun getDurationMillis(): Long {
        return (1000.0 * duration).toLong()
    }
}

data class LocalizeAction(
    @SerializedName("actor_id")
    val actorId: ActorId,

    @SerializedName("position")
    val position: Point
) : Action() {
    companion object {
        val NAME = "localize"
    }
}

data class StatUpdateAction(
    @SerializedName("actor_id")
    val actorId: ActorId,

    @SerializedName("stats")
    val stats: Stats
) : Action() {
    companion object {
        val NAME = "stat_update"
    }
}

data class PickStartAction(
    @SerializedName("who")
    val who: ActorId,

    @SerializedName("what")
    val what: ActorId
) : Action() {
    companion object {
        val NAME = "pick_start"
    }
}

data class PickEndAction(
    @SerializedName("who")
    val who: ActorId
) : Action() {
    companion object {
        val NAME = "pick_end"
    }
}

data class UpdateInventoryAction(
    @SerializedName("owner_id")
    val ownerId: ActorId,

    @SerializedName("inventory")
    val inventory: Inventory
) : Action() {
    companion object {
        val NAME = "update_inventory"
    }
}

data class DamageAction(
    @SerializedName("dealer_id")
    val dealerId: ActorId,

    @SerializedName("receiver_id")
    val receiverId: ActorId,

    @SerializedName("variant")
    val variant: DamageVariant,

    @SerializedName("hand")
    val hand: Hand
) : Action() {
    companion object {
        val NAME = "damage"
    }
}

data class CraftStartAction(
    @SerializedName("crafter_id")
    val crafterId: ActorId
) : Action() {
    companion object {
        val NAME = "craft_start"
    }
}

data class CraftEndAction(
    @SerializedName("crafter_id")
    val crafterId: ActorId
) : Action() {
    companion object {
        val NAME = "craft_end"
    }
}

val ACTIONS: HashMap<String, Type> = hashMapOf(
    /* ktlint-disable no-multi-spaces dot-spacing */
    ConfigurationAction  .NAME to ConfigurationAction  ::class.java,
    CraftStartAction     .NAME to CraftStartAction     ::class.java,
    CraftEndAction       .NAME to CraftEndAction       ::class.java,
    CreateActorsAction   .NAME to CreateActorsAction   ::class.java,
    DeleteActorsAction   .NAME to DeleteActorsAction   ::class.java,
    MovementAction       .NAME to MovementAction       ::class.java,
    LocalizeAction       .NAME to LocalizeAction       ::class.java,
    StatUpdateAction     .NAME to StatUpdateAction     ::class.java,
    PickStartAction      .NAME to PickStartAction      ::class.java,
    PickEndAction        .NAME to PickEndAction        ::class.java,
    UpdateInventoryAction.NAME to UpdateInventoryAction::class.java,
    DamageAction         .NAME to DamageAction         ::class.java
    /* ktlint-enable no-multi-spaces dot-spacing */
)

class ActionDeserializer : JsonDeserializer<Action> {
    public override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): Action? {
        val jsonObject = json.getAsJsonObject()
        val variantName: String = jsonObject.get("type").getAsString()
        val variantClass: Type? = ACTIONS.get(variantName)
        return context.deserialize(json, variantClass)
    }
}
