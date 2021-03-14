import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import com.edgin.around.api.actions.*
import com.edgin.around.api.geometry.*

class ConfigurationActionTest {
    @Test
    fun parseJson() {
        val message = ("""{"elevation_function": {""" +
            """"terrain": [""" +
                """{"origin": {"theta": 0.0, "phi": 1.0}, "type": "hills"}, """ +
                """{"origin": {"theta": 2.0, "phi": 3.0}, "type": "ranges"}, """ +
                """{"origin": {"theta": 4.0, "phi": 5.0}, "type": "continents"}""" +
            """], """ +
            """"radius": 100.0}, """ +
            """"hero_actor_id": 5146106004195521549, """ +
            """"type": "configuration"}""")

        val gson = Action.prepareGson()
        val parsedAction = gson.fromJson(message, Action::class.java)
        val configurationAction = parsedAction as ConfigurationAction

        val expectedElevation = Elevation(100.0f, arrayListOf(
            Terrain("hills", Point(0.0f, 1.0f)),
            Terrain("ranges", Point(2.0f, 3.0f)),
            Terrain("continents", Point(4.0f, 5.0f))
        ))
        val expectedAction = ConfigurationAction(5146106004195521549, expectedElevation)

        assertEquals(expectedAction, configurationAction)
    }
}

