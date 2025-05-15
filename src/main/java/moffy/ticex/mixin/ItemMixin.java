package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import moffy.ticex.client.ModifiableItemCustomRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = Item.class, remap = false)
public class ItemMixin{
    @Inject(
        at = @At("head"),
        method = "initializeClient"
    )
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer, CallbackInfo cb) {
        if(this instanceof IModifiable){
            consumer.accept(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return new ModifiableItemCustomRenderer(null, null);
                }
            });
        }
    }
}
