package com.kishiqq.plugins

import android.content.Context

import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.Logger
import com.aliucord.entities.Plugin
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.Utils.createCommandChoice
import com.aliucord.api.CommandsAPI.CommandResult

import com.discord.api.commands.ApplicationCommandType
import com.discord.api.message.embed.MessageEmbed

class Result(
        val images: List<Images>
) {
    data class Images(
            val width: String,
            val height: String,
            val url: String
    )
}

private fun request(tag: String, isNsfw: Boolean, isAnimated: Boolean, log: Logger): Result {
    val url = StringBuilder("https://api.waifu.im/images?IsAnimated=$isAnimated&IncludedTags=$tag&IsNsfw=$isNsfw")
    if (isNsfw) {
        url.append("&is_nsfw=true")
    }
    return Http.simpleJsonGet(url.toString(), Result::class.java)
}

private fun createEmbed(url: String, height: String, width: String): MessageEmbed {
    return MessageEmbedBuilder()
            .setRandomColor()
            .setImage(url, null, height.toInt(), width.toInt())
            .setAuthor("waifu.im")
            .build()
}


@AliucordPlugin
class Waifu : Plugin() {

    private val log = Logger("Waifu.im")

    override fun start(ctx: Context) {
        // https://api.waifu.im/tags
        // https://docs.waifu.im/docs/api/list-all-tags-with-optional-filtering
        val Choices = listOf(
            createCommandChoice("waifu", "waifu"),
            createCommandChoice("ero", "ero"),
            createCommandChoice("ecchi", "ecchi"),
            createCommandChoice("oppai", "oppai"),
            createCommandChoice("hentai", "hentai"),
            createCommandChoice("milf", "milf"),
            createCommandChoice("uniform", "uniform"),
            createCommandChoice("ass", "ass"),
            createCommandChoice("maid", "maid"),
            createCommandChoice("selfies", "selfies"),
            createCommandChoice("paizuri", "paizuri"),
            createCommandChoice("oral", "oral"),
            createCommandChoice("genshin-impact", "genshin-impact"),
            createCommandChoice("raiden-shogun", "raiden-shogun"),
            createCommandChoice("marin-kitagawa", "marin-kitagawa"),
            createCommandChoice("mori-calliope", "mori-calliope"),
            createCommandChoice("kamisato-ayaka", "kamisato-ayaka"),
            createCommandChoice("arknights", "arknights"),
            createCommandChoice("black-clover", "black-clover")
        )

        val args = listOf(
                Utils.createCommandOption(
                        ApplicationCommandType.STRING,
                        "tags",
                        "Choose a tag - Default: waifu",
                        choices = Choices
                ),
                Utils.createCommandOption(
                        ApplicationCommandType.BOOLEAN,
                        "is_nsfw",
                        "Whether the image should be NSFW - Default: false",
                ),
                Utils.createCommandOption(
                        ApplicationCommandType.BOOLEAN,
                        "is_animated",
                        "self-explanatory - Default: false"
                ),
                Utils.createCommandOption(
                        ApplicationCommandType.BOOLEAN,
                        "send",
                        "Send image to chat - Default: false"
                )
        )

        commands.registerCommand("waifu", "Get images from waifu.im API", args) {
            val tag = it.getStringOrDefault("tags", "waifu")
            val isNsfw = it.getBoolOrDefault("is_nsfw", false)
            val isAnimated = it.getBoolOrDefault("is_animated", false)
            val send = it.getBoolOrDefault("send", false)
            
            val result = request(tag, isNsfw, isAnimated, log).images[0]

            if (!send) {
                val embed = createEmbed(result.url, result.height, result.width)
                return@registerCommand CommandResult(null, mutableListOf(embed), false)
            }

            CommandResult(result.url, null, send)
        }
    }

    override fun stop(ctx: Context) = commands.unregisterAll()

}
