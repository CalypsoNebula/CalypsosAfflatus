package settingdust.calypsos_nightvision_goggles.neoforge.adapter

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import settingdust.calypsos_nightvision_goggles.CalypsosNightVisionGogglesItems
import settingdust.calypsos_nightvision_goggles.adapter.AccessoryIntegration
import settingdust.calypsos_nightvision_goggles.adapter.LoaderAdapter
import settingdust.calypsos_nightvision_goggles.item.nightvision_goggles.NightvisionGogglesAccessory
import settingdust.calypsos_nightvision_goggles.util.AccessoryRenderer
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.CuriosRendererRegistry
import top.theillusivec4.curios.api.client.ICurioRenderer
import top.theillusivec4.curios.api.type.capability.ICurioItem
import kotlin.jvm.optionals.getOrNull

class CuriosAccessoryIntegration : AccessoryIntegration {
    object Renderer : ICurioRenderer {
        override fun <T : LivingEntity, M : EntityModel<T>> render(
            stack: ItemStack,
            slotContext: SlotContext,
            poseStack: PoseStack,
            renderLayerParent: RenderLayerParent<T, M>,
            multiBufferSource: MultiBufferSource,
            light: Int,
            limbSwing: Float,
            limbSwingAmount: Float,
            partialTicks: Float,
            ageInTicks: Float,
            netHeadYaw: Float,
            headPitch: Float
        ) {
            AccessoryRenderer.render(
                stack,
                slotContext.entity(),
                poseStack,
                multiBufferSource,
                light
            )
        }
    }

    override val modId = CuriosApi.MODID
    override fun init() {
        CuriosApi.registerCurio(CalypsosNightVisionGogglesItems.NIGHTVISION_GOGGLES, object : ICurioItem {
            override fun curioTick(slotContext: SlotContext, stack: ItemStack) {
                NightvisionGogglesAccessory.tick(stack, slotContext.entity)
            }
        })

        if (LoaderAdapter.isClient) {
            CuriosRendererRegistry.register(CalypsosNightVisionGogglesItems.NIGHTVISION_GOGGLES) {
                Renderer
            }
        }
    }

    override fun getEquipped(entity: LivingEntity, item: Item) =
        CuriosApi.getCuriosInventory(entity).flatMap { it.findFirstCurio(item) }.getOrNull()?.stack
}