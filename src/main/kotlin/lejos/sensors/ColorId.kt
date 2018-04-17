package lejos.sensors

import lejos.robotics.Color

import java.util.Arrays.asList

enum class ColorId constructor(var id: Int) {

    NONE(Color.NONE),
    BLACK(Color.BLACK),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    RED(Color.RED),
    WHITE(Color.WHITE),
    BROWN(Color.BROWN);

    companion object {
        fun colorId(id: Float): ColorId {
            return colorId(id.toInt())
        }

        fun colorId(id: Int): ColorId {
            return asList(*values())
                    .stream()
                    .filter { color -> color.id == id }
                    .findFirst()
                    .orElse(NONE)
        }
    }
}
