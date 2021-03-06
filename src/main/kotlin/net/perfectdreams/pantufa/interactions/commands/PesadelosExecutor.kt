package net.perfectdreams.pantufa.interactions.commands

import net.perfectdreams.discordinteraktions.common.context.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.options.CommandOptions
import net.perfectdreams.pantufa.PantufaBot
import net.perfectdreams.pantufa.api.commands.SilentCommandException
import net.perfectdreams.pantufa.dao.CashInfo
import net.perfectdreams.pantufa.network.Databases
import net.perfectdreams.pantufa.utils.Constants
import net.perfectdreams.pantufa.utils.PantufaReply
import org.jetbrains.exposed.sql.transactions.transaction

class PesadelosExecutor(pantufa: PantufaBot) : PantufaInteractionCommand(
    pantufa
) {
    companion object : SlashCommandExecutorDeclaration(PesadelosExecutor::class) {
        object Options : CommandOptions() {
            val playerName = optionalString("player_name", "Nome do Player")
                .register()
        }

        override val options = Options
    }

    override suspend fun executePantufa(context: PantufaCommandContext, args: SlashCommandArguments) {
        val playerName = args[MoneyExecutor.options.playerName]

        if (playerName != null) {
            val playerData = pantufa.retrieveMinecraftUserFromUsername(playerName) ?: run {
                context.reply(
                    PantufaReply(
                        message = "Player desconhecido!",
                        prefix = Constants.ERROR
                    )
                )
                throw SilentCommandException()
            }
            val playerUniqueId = playerData.id.value

            val cash = transaction(Databases.sparklyPower) {
                CashInfo.findById(playerUniqueId)
            }?.cash ?: 0

            context.reply(
                PantufaReply(
                    message = "**`${playerData.username}`** possui **${cash} Pesadelos**!",
                    prefix = "\uD83D\uDCB5"
                )
            )
        } else {
            val accountInfo = context.retrieveConnectedMinecraftAccountOrFail()
            val playerUniqueId = accountInfo.uniqueId

            val cash = transaction(Databases.sparklyPower) {
                CashInfo.findById(playerUniqueId)
            }?.cash ?: 0

            context.reply(
                PantufaReply(
                    message = "Você possui **${cash} Pesadelos**!",
                    prefix = "\uD83D\uDCB5"
                )
            )
        }
    }
}