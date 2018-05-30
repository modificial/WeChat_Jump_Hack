package com.modificial.hack

import java.awt.image.BufferedImage
import java.util.ArrayDeque
import java.util.Queue

/**
 * 瓶子的下一步位置计算
 * Created by chenliang on 2017/12/31.
 */
class BottleFinder {

    fun find(image: BufferedImage?, i: Int, j: Int): IntArray? {
        var i = i
        var j = j
        if (image == null) {
            return null
        }

        val ret = IntArray(6)
        ret[0] = i
        ret[1] = j
        ret[2] = Integer.MAX_VALUE
        ret[3] = Integer.MAX_VALUE
        ret[4] = Integer.MIN_VALUE
        ret[5] = Integer.MAX_VALUE

        val width = image.width
        val height = image.height

        val vMap = Array(width) { BooleanArray(height) }
        val queue = ArrayDeque<IntArray>()
        var pos: IntArray? = intArrayOf(i, j)
        queue.add(pos)

        while (!queue.isEmpty()) {
            pos = queue.poll()
            i = pos!![0]
            j = pos[1]
            if (i < 0 || i >= width || j < 0 || j > height || vMap[i][j]) {
                continue
            }
            vMap[i][j] = true
            val pixel = image.getRGB(i, j)
            val r = pixel and 0xff0000 shr 16
            val g = pixel and 0xff00 shr 8
            val b = pixel and 0xff
            if (r == TARGET && g == TARGET && b == TARGET) {
                //System.out.println("("+i+", "+j+")");
                if (i < ret[2]) {
                    ret[2] = i
                    ret[3] = j
                } else if (i == ret[2] && j < ret[3]) {
                    ret[2] = i
                    ret[3] = j
                }
                if (i > ret[4]) {
                    ret[4] = i
                    ret[5] = j
                } else if (i == ret[4] && j < ret[5]) {
                    ret[4] = i
                    ret[5] = j
                }
                if (j < ret[1]) {
                    ret[0] = i
                    ret[1] = j
                }
                queue.add(buildArray(i - 1, j))
                queue.add(buildArray(i + 1, j))
                queue.add(buildArray(i, j - 1))
                queue.add(buildArray(i, j + 1))
            }
        }

        return ret
    }

    companion object {

        val TARGET = 255

        fun buildArray(i: Int, j: Int): IntArray {
            return intArrayOf(i, j)
        }
    }


}
