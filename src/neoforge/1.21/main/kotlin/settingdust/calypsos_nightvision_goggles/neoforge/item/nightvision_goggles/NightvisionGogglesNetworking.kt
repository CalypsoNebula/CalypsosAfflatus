package settingdust.calypsos_nightvision_goggles.neoforge.item.nightvision_goggles

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.inventory.Slot
import net.neoforged.neoforge.network.PacketDistributor
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGoggles
import settingdust.calypsos_nightvision_goggles.adapter.LoaderAdapter
import settingdust.calypsos_nightvision_goggles.item.nightvision_goggles.NightvisionGogglesNetworking
import settingdust.calypsos_nightvision_goggles.util.ContainerType
import settingdust.calypsos_nightvision_goggles.v1_21.util.CalypsosNightVisionGogglesItemsStreamCodecs

class NightvisionGogglesNetworking : NightvisionGogglesNetworking {
    override fun c2sSwitchMode(slot: Slot) {
        require(LoaderAdapter.isClient)
        val index =
            if (slot is CreativeModeInventoryScreen.SlotWrapper) {
                slot.containerSlot
            } else {
                slot.index
            }
        PacketDistributor.sendToServer(
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
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<C2SSwitchModePacket>(CalypsosNightVisionGoggles.id("switch_mode"))
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            C2SSwitchModePacket::slotIndex,
            ByteBufCodecs.STRING_UTF8,
            C2SSwitchModePacket::containerType,
            CalypsosNightVisionGogglesItemsStreamCodecs.CONTAINER_TYPE_DATA,
            C2SSwitchModePacket::data,
            ::C2SSwitchModePacket
        )
    }

    override fun type() = TYPE
}