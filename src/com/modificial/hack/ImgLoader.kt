package com.modificial.hack

import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Created by chenliang on 2017/12/31.
 */
object ImgLoader {

    @Throws(IOException::class)
    fun load(path: String): BufferedImage? {
        var image: BufferedImage? = null
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(path))
            image = ImageIO.read(`is`)
        } finally {
            if (`is` != null) {
                `is`.close()
            }
        }
        return image
    }
}
