package moffy.ticex.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.vertex.PoseStack;

import moffy.ticex.client.ShaderProvider.RenderQuadArgsWrapper;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifiableItemCustomRenderer extends BlockEntityWithoutLevelRenderer{

    public ModifiableItemCustomRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher,
            EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderByItem(ItemStack pItemStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.renderer.entity.ItemRenderer defaultRenderer = mc.getItemRenderer();
        
        ToolStack tool = ToolStack.from(pItemStack);
        BakedModel pModel = defaultRenderer.getModel(pItemStack, mc.level, mc.player, 0);
        
        List<ShaderToolRenderUtils.RenderTask> renderQueue = new ArrayList<>();
        Set<PartPredicate> seen = new HashSet<>();

        pPoseStack.pushPose();
        //pModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pPoseStack, pModel, pDisplayContext, true);
        for (var model : pModel.getRenderPasses(pItemStack, true)) {
            for (var rendertype : model.getRenderTypes(pItemStack, true)) {
                RandomSource randomsource = RandomSource.create();

                for(Direction direction : Direction.values()) {
                    randomsource.setSeed(42L);
                    this.addShaderQuadToQueue(rendertype, pPoseStack, pBuffer, model.getQuads((BlockState)null, direction, randomsource), pItemStack, pCombinedLight, pCombinedOverlay, pDisplayContext, tool, renderQueue::add, seen);
                }

                randomsource.setSeed(42L);
                this.addShaderQuadToQueue(rendertype, pPoseStack, pBuffer, model.getQuads((BlockState)null, (Direction)null, randomsource), pItemStack, pCombinedLight, pCombinedOverlay, pDisplayContext, tool, renderQueue::add, seen);
            }
        }

        renderQueue.sort(Comparator.comparingInt(task -> ((ShaderToolRenderUtils.RenderTask)task).getPhase().getIndex()));
        for(ShaderToolRenderUtils.RenderTask task : renderQueue){
            task.renderQuad();
        }

        pPoseStack.popPose();
        
    }

    @Unique
    private void addShaderQuadToQueue(RenderType renderType, PoseStack pPoseStack, MultiBufferSource pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay, ItemDisplayContext pDisplayContext, IToolStackView tool, Consumer<ShaderToolRenderUtils.RenderTask> addTaskFn, Set<PartPredicate> seenList){
        boolean flag = !pItemStack.isEmpty();

        for(BakedQuad bakedquad : pQuads) {

            RenderQuadArgsWrapper defaultWrapper = new RenderQuadArgsWrapper(renderType, pPoseStack, bakedquad, 1.0f, 1.0f, 1.0f, 1.0f, pCombinedLight, pCombinedOverlay, flag, pBuffer, pDisplayContext, tool);

            if(bakedquad instanceof ShaderToolQuad){
                PartPredicate predicate = ((ShaderToolQuad)bakedquad).getPredicate();
                ShaderProvider<RenderQuadArgsWrapper> provider = TicEXRegistry.TOOL_SHADERS.getProvider(predicate);

                Consumer<RenderQuadArgsWrapper> renderMethod = (wrapper->{
                    wrapper.renderQuadsWithConsumer();
                });
                
                if(provider != null){
                    //underlay
                    if(!seenList.contains(predicate)){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.UNDERLAY, provider::renderUnderLayer, defaultWrapper));
                        seenList.add(predicate);
                    }

                    //overlay
                    if(predicate.isModifierId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.OVERLAY_MODIFIER, provider::renderOverLayer, defaultWrapper));
                        //addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.MODIFIER_WITH_OVERLAY, renderMethod, defaultWrapper));
                    } else if(predicate.isMaterialVariantId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.OVERLAY_MATERIAL, provider::renderOverLayer, defaultWrapper));
                        //addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.MATERIAL_WITH_OVERLAY, renderMethod, defaultWrapper));
                    }
                } else {
                    //normal items
                    if(predicate.isModifierId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.NORMAL_MODIFIER, renderMethod, defaultWrapper));
                    } else if(predicate.isMaterialVariantId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.NORMAL_MATERIAL, renderMethod, defaultWrapper));
                    }
                }
            }
        }
    }
}
