package settingdust.calypsos_nightvision_goggles.item.nightvision_goggles

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGoggles
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGogglesItems
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGogglesKeyBindings
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGogglesSoundEvents
import settingdust.calypsos_nightvision_goggles.adapter.LoaderAdapter
import settingdust.calypsos_nightvision_goggles.item.nightvision_goggles.NightvisionGogglesModeHandler.Companion.mode
import settingdust.calypsos_nightvision_goggles.mixin.AbstractContainerScreenAccessor

object NightvisionGogglesItem {
    val properties = Item.Properties().stacksTo(1).durability(1800 + 1)

    const val duration = 2 * 20
    const val amplifier = 0
    const val ambient = false
    const val visible = false
    const val shouIcon = true

    private var expanded = false

    val DURABILITY_PROVIDERS = mapOf(
        Items.SPIDER_EYE to 180,
        Items.FERMENTED_SPIDER_EYE to 540,
        Items.GLOWSTONE to 540
    )

    init {
        LoaderAdapter.onKeyPressedInScreen(CalypsosNightVisionGogglesKeyBindings.ACCESSORY_MODE) { screen ->
            if (screen !is AbstractContainerScreen<*>) return@onKeyPressedInScreen
            val hoveredSlot = (screen as AbstractContainerScreenAccessor).hoveredSlot
            if (hoveredSlot == null
                || hoveredSlot is CreativeModeInventoryScreen.CustomCreativeSlot
                || !hoveredSlot.hasItem()
                || hoveredSlot.item.item !== CalypsosNightVisionGogglesItems.NIGHTVISION_GOGGLES
            ) return@onKeyPressedInScreen
            NightvisionGogglesNetworking.c2sSwitchMode(hoveredSlot)
            Minecraft.getInstance().soundManager.play(
                SimpleSoundInstance.forUI(CalypsosNightVisionGogglesSoundEvents.UI_MODE_SWITCH, 1f, 1f)
            )
        }

        LoaderAdapter.onLivingEntityTick { entity ->
            val stack = entity.getItemBySlot(EquipmentSlot.HEAD)
            if (!stack.`is`(CalypsosNightVisionGogglesItems.NIGHTVISION_GOGGLES)) return@onLivingEntityTick
            NightvisionGogglesAccessory.tick(stack, entity)
        }

        LoaderAdapter.onItemStackedOnOther { player, carriedItem, stackedOnItem, slot, clickAction ->
            if (clickAction !== ClickAction.SECONDARY) return@onItemStackedOnOther false
            if (!carriedItem.`is`(CalypsosNightVisionGogglesItems.NIGHTVISION_GOGGLES)) return@onItemStackedOnOther false
            val value = DURABILITY_PROVIDERS[stackedOnItem.item] ?: return@onItemStackedOnOther false
            carriedItem.damageValue -= value
            stackedOnItem.shrink(1)
            true
        }
    }

    @Suppress("SimplifyBooleanWithConstants")
    fun MobEffectInstance?.isFromAccessory() =
        this != null
                && amplifier == NightvisionGogglesItem.amplifier
                && endsWithin(NightvisionGogglesItem.duration)
                && isVisible == NightvisionGogglesItem.visible
                && isAmbient == NightvisionGogglesItem.ambient
                && showIcon() == NightvisionGogglesItem.shouIcon

    fun MutableList<Component>.appendTooltip(stack: ItemStack) {
        if (stack.mode == null) stack.mode = NightvisionGogglesModeHandler.Mode.AUTO
        val spiderEye = Component.translatable("item.minecraft.spider_eye").withStyle { it.withColor(0xC85A54) }
        add(
            Component.translatable(
                "item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.description",
                Component.translatable("effect.minecraft.night_vision").withStyle { it.withColor(0x658963) },
                spiderEye
            )
        )
        val modes = NightvisionGogglesModeHandler.Mode.entries
            .map { mode ->
                Component.translatable("item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.mode.${mode.name.lowercase()}")
                    .withStyle { style ->
                        if (stack.mode == mode) style.withColor(mode.color) else style.withColor(
                            ChatFormatting.GRAY
                        )
                    }
            }.toTypedArray()
        add(
            Component.translatable(
                "item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.mode",
                *modes,
                CalypsosNightVisionGogglesKeyBindings.ACCESSORY_MODE.translatedKeyMessage
            )
        )
        if (LoaderAdapter.isClient && !Screen.hasShiftDown()) {
            if (expanded) {
                Minecraft.getInstance().soundManager.play(
                    SimpleSoundInstance.forUI(
                        CalypsosNightVisionGogglesSoundEvents.UI_COLLAPSE,
                        1f,
                        1f
                    )
                )
            }
            expanded = false
            add(Component.translatable("tooltip.${CalypsosNightVisionGoggles.ID}.expand"))
        } else {
            if (LoaderAdapter.isClient && !expanded) {
                Minecraft.getInstance().soundManager.play(
                    SimpleSoundInstance.forUI(
                        CalypsosNightVisionGogglesSoundEvents.UI_EXPAND,
                        1f,
                        1f
                    )
                )
                expanded = true
            }
            add(Component.translatable("item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.expand.0"))
            add(
                Component.translatable(
                    "item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.expand.1",
                    spiderEye,
                    Component.translatable("item.minecraft.fermented_spider_eye").withStyle { it.withColor(0xD4696F) },
                    Component.translatable("block.minecraft.glowstone").withStyle { it.withColor(0xF4A460) }
                )
            )
            add(Component.translatable("item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.expand.2"))
            add(Component.translatable("item.${CalypsosNightVisionGoggles.ID}.nightvision_goggles.tooltip.expand.3"))
        }
    }
}