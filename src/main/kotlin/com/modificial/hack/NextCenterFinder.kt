package com.modificial.hack

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by modificial on 2018/1/1.
 */
class NextCenterFinder {

    private var bottleFinder = BottleFinder()

    fun find(image: BufferedImage?, myPos: IntArray?): IntArray? {
        if (image == null) {
            return null
        }

        val width = image.width
        val height = image.height
        var pixel = image.getRGB(0, 200)
        val r1 = pixel and 0xff0000 shr 16
        val g1 = pixel and 0xff00 shr 8
        val b1 = pixel and 0xff
        val map = HashMap<Int, Int>()
        for (i in 0 until width) {
            pixel = image.getRGB(i, height - 1)
            map[pixel] = (map as Map<Int, Int>).getOrDefault(pixel, 0) + 1
        }
        var max = 0
        for ((key, value) in map) {
            if (value > max) {
                pixel = key
                max = value
            }
        }
        val r2 = pixel and 0xff0000 shr 16
        val g2 = pixel and 0xff00 shr 8
        val b2 = pixel and 0xff

        val t = 16

        val minR = Integer.min(r1, r2) - t
        val maxR = Integer.max(r1, r2) + t
        val minG = Integer.min(g1, g2) - t
        val maxG = Integer.max(g1, g2) + t
        val minB = Integer.min(b1, b2) - t
        val maxB = Integer.max(b1, b2) + t

        println("$minR, $minG, $minB")
        println("$maxR, $maxG, $maxB")

        val ret = IntArray(6)
        var targetR = 0
        var targetG = 0
        var targetB = 0
        var found = false
        run {
            var j = height / 4
            while (j < myPos!![1]) {
                for (i in 0 until width) {
                    val dx = Math.abs(i - myPos[0])
                    val dy = Math.abs(j - myPos[1])
                    if (dy > dx) {
                        continue
                    }
                    pixel = image.getRGB(i, j)
                    val r = pixel and 0xff0000 shr 16
                    val g = pixel and 0xff00 shr 8
                    val b = pixel and 0xff
                    if (r < minR || r > maxR || g < minG || g > maxG || b < minB || b > maxB) {
                        j += 2
                        ret[0] = i
                        ret[1] = j
                        println("top, x: $i, y: $j")
                        for (k in 0..4) {
                            pixel = image.getRGB(i, j + k)
                            println(pixel)
                            targetR += pixel and 0xff0000 shr 16
                            targetG += pixel and 0xff00 shr 8
                            targetB += pixel and 0xff
                        }
                        targetR /= 5
                        targetG /= 5
                        targetB /= 5
                        found = true
                        break
                    }
                }
                if (found) {
                    break
                }
                j++
            }
        }

        if (targetR == BottleFinder.TARGET && targetG == BottleFinder.TARGET && targetB == BottleFinder.TARGET) {
            return bottleFinder.find(image, ret[0], ret[1])
        }

        val matchMap = Array(width) { BooleanArray(height) }
        val vMap = Array(width) { BooleanArray(height) }
        ret[2] = Integer.MAX_VALUE
        ret[3] = Integer.MAX_VALUE
        ret[4] = Integer.MIN_VALUE
        ret[5] = Integer.MAX_VALUE

        val queue = ArrayDeque<IntArray>()
        queue.add(ret)
        while (!queue.isEmpty()) {
            val item = queue.poll()
            val i = item!![0]
            val j = item[1]
            //            int dx = Math.abs(i - myPos[0]);
            //            int dy = Math.abs(j - myPos[1]);
            //            if (dy > dx) {
            //                continue;
            //            }
            if (j >= myPos!![1]) {
                continue
            }

            if (i < Integer.max(ret[0] - 300, 0) || i >= Integer.min(ret[0] + 300, width) || j < Integer.max(0, ret[1] - 400) || j >= Integer.max(height, ret[1] + 400) || vMap[i][j]) {
                continue
            }
            vMap[i][j] = true
            pixel = image.getRGB(i, j)
            val r = pixel and 0xff0000 shr 16
            val g = pixel and 0xff00 shr 8
            val b = pixel and 0xff
            matchMap[i][j] = ToleranceHelper.match(r, g, b, targetR, targetG, targetB, 16)
            //            if (i == ret[0] && j == ret[1]) {
            //                System.out.println(matchMap[i][j]);
            //            }
            if (matchMap[i][j]) {
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

        println("left, x: " + ret[2] + ", y: " + ret[3])
        println("right, x: " + ret[4] + ", y: " + ret[5])

        return ret
    }

    companion object {

        fun buildArray(i: Int, j: Int): IntArray {
            return intArrayOf(i, j)
        }

        @Throws(IOException::class)
        @JvmStatic
        fun main(strings: Array<String>) {
            //  int[] excepted = {0, 0};
            val t = NextCenterFinder()
            val root = t.javaClass.getResource("/").path
            println("root: $root")
            val imgsSrc = root + "imgs/src"
            val imgsDesc = root + "imgs/next_center"
            val srcDir = File(imgsSrc)
            println(srcDir)
            val myPosFinder = MyPosFinder()
            var cost: Long = 0
            for (file in srcDir.listFiles()!!) {
                println(file)
                val img = ImgLoader.load(file.absolutePath)
                val t1 = System.nanoTime()
                val myPos = myPosFinder.find(img)
                val pos = t.find(img, myPos)
                val t2 = System.nanoTime()
                cost += t2 - t1
                val desc = BufferedImage(img!!.width, img.height, BufferedImage.TYPE_INT_RGB)
                val g = desc.graphics
                g.drawImage(img, 0, 0, img.width, img.height, null)
                g.color = Color.RED
                g.fillRect(pos!![0] - 5, pos[1] - 5, 10, 10)
                g.fillRect(pos[2] - 5, pos[3] - 5, 10, 10)
                g.fillRect(pos[4] - 5, pos[5] - 5, 10, 10)
                if (pos[2] != Integer.MAX_VALUE && pos[4] != Integer.MIN_VALUE) {
                    g.fillRect((pos[2] + pos[4]) / 2 - 5, (pos[3] + pos[5]) / 2 - 5, 10, 10)
                } else {
                    g.fillRect(pos[0], pos[1] + 36, 10, 10)
                }
                val descFile = File(imgsDesc, file.name)
                if (!descFile.exists()) {
                    descFile.mkdirs()
                    descFile.createNewFile()
                }
                ImageIO.write(desc, "png", descFile)
            }
            println("avg time cost: " + cost / srcDir.listFiles()!!.size.toLong() / 1000000)

        }
    }

}
