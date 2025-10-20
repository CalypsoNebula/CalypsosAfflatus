package settingdust.calypsos_nightvision_goggles.mixin;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ModelPart.class})
public interface ModelPartAccessor {
    @Accessor("cubes")
    List<ModelPart.Cube> getCubes();

    @Accessor("children")
    Map<String, ModelPart> getChildren();
}
