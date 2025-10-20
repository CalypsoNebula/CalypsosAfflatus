package settingdust.calypsos_nightvision_goggles.forge.item.nightvision_goggles

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.inventory.Slot
import net.minecraftforge.network.NetworkEvent
import settingdust.calypsos_nightvision_goggles.adapter.LoaderAdapter
import settingdust.calypsos_nightvision_goggles.forge.CalypsosNightVisionGogglesNetworking
import settingdust.calypsos_nightvision_goggles.item.nightvision_goggles.NightvisionGogglesNetworking
import settingdust.calypsos_nightvision_goggles.util.ContainerType
import java.util.function.Supplier

class NightvisionGogglesNetworking : NightvisionGogglesNetworking {
    override fun c2sSwitchMode(slot: Slot) {
        require(LoaderAdapter.isClient)
        val index =
            if (slot is CreativeModeInventoryScreen.SlotWrapper) {
                slot.containerSlot
            } else {
                slot.index
            }
        CalypsosNightVisionGogglesNetworking.CHANNEL.sendToServer(
            C2SSwitchModePacket(
                index,
                ContainerType.NORMAL,
                ContainerType.Data.Normal
            )
        )
    }
}

data class C2SSwitchModePacket(
    val slotIndex: Int,
    val containerType: String,
    val data: ContainerType.Data
) {
    companion object {
        fun decode(buf: FriendlyByteBuf): C2SSwitchModePacket {
            return C2SSwitchModePacket(buf.readVarInt(), buf.readUtf(), buf)
        }

        fun handle(packet: C2SSwitchModePacket, context: Supplier<NetworkEvent.Context>) {
            context.get().enqueueWork {
                NightvisionGogglesNetworking.handleSwitchMode(
                    packet.slotIndex,
                    ContainerType.ALL.getValue(packet.containerType),
                    context.get().sender!!,
                    packet.data
                )
            }
        }
    }

    constructor(slotIndex: Int, containerType: String, buf: FriendlyByteBuf) : this(
        slotIndex,
        containerType,
        ContainerType.ALL[containerType]!!.dataSerializer(buf)
    )

    fun encode(buf: FriendlyByteBuf) {
        buf.writeVarInt(slotIndex)
        buf.writeUtf(containerType)
    }
}