package com.modificial.hack

import java.awt.image.BufferedImage
import java.util.*

/**
 * Created by modificial on 2017/12/31.
 */
@Suppress("NAME_SHADOWING")
class WhitePointFinder {

    fun find(image: BufferedImage?, x1: Int, y1: Int, x2: Int, y2: Int): IntArray? {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        if (image == null) {
            return null
        }

        val width = image.width
        val height = image.height

        x1 = Integer.max(x1, 0)
        x2 = Integer.min(x2, width - 1)
        y1 = Integer.max(y1, 0)
        y2 = Integer.min(y2, height - 1)

        for (i in x1..x2) {
            for (j in y1..y2) {
                var pixel = image.getRGB(i, j)
                var r = pixel and 0xff0000 shr 16
                var g = pixel and 0xff00 shr 8
                var b = pixel and 0xff
                if (r == TARGET && g == TARGET && b == TARGET) {
                    val vMap = Array(width) { BooleanArray(height) }
                    val queue = ArrayDeque<IntArray>()
                    var pos: IntArray? = intArrayOf(i, j)
                    queue.add(pos)
                    var maxX = Integer.MIN_VALUE
                    var minX = Integer.MAX_VALUE
                    var maxY = Integer.MIN_VALUE
                    var minY = Integer.MAX_VALUE
                    while (!queue.isEmpty()) {
                        pos = queue.poll()
                        val x = pos!![0]
                        val y = pos[1]
                        if (x < x1 || x > x2 || y < y1 || y > y2 || vMap[x][y]) {
                            continue
                        }
                        vMap[x][y] = true
                        pixel = image.getRGB(x, y)
                        r = pixel and 0xff0000 shr 16
                        g = pixel and 0xff00 shr 8
                        b = pixel and 0xff
                        if (r == TARGET && g == TARGET && b == TARGET) {
                            maxX = Integer.max(maxX, x)
                            minX = Integer.min(minX, x)
                            maxY = Integer.max(maxY, y)
                            minY = Integer.min(minY, y)
                            queue.add(buildArray(x - 1, y))
                            queue.add(buildArray(x + 1, y))
                            queue.add(buildArray(x, y - 1))
                            queue.add(buildArray(x, y + 1))
                        }
                    }

                    println("whitePoint: $maxX, $minX, $maxY, $minY")
                    return if (maxX - minX in 35..45 && maxY - minY <= 30 && maxY - minY >= 20) {
                        intArrayOf((minX + maxX) / 2, (minY + maxY) / 2)
                    } else {
                        null
                    }

                }
            }
        }
        return null
    }

    companion object {

        const val TARGET = 245

        fun buildArray(i: Int, j: Int): IntArray {
            return intArrayOf(i, j)
        }
    }


}
