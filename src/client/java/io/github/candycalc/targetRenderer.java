package io.github.candycalc;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class targetRenderer {

    public static void renderTargetOverlay(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;

        if (player == null || world == null) {
            return;
        }

        //HitResult hit = client.crosshairTarget;
        //  Using this because entities (like wind charges) will not block the raycast this way
        //  this is the method that projectiles use, btw, per the ProjectileUtil class.
        Vec3d pPos = player.getEyePos();
        double pInteractionRange = player.getBlockInteractionRange();
        Vec3d pLookPos = pPos.add(player.getRotationVec(tickDelta.getTickDelta(false)).multiply(pInteractionRange));
        HitResult hit = world.getCollisionsIncludingWorldBorder(new RaycastContext(
                pPos,
                pLookPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult block = (BlockHitResult) hit;
            if (world.getBlockState(block.getBlockPos()).getBlock() == Blocks.TARGET) {
                Window window = client.getWindow();
                int width = window.getScaledWidth();
                int height = window.getScaledHeight();

                //  Target block algorithm
                Direction direction = block.getSide();
                Vec3d pos = block.getPos();
                double x = Math.abs(MathHelper.fractionalPart(pos.x) - 0.5);
                double y = Math.abs(MathHelper.fractionalPart(pos.y) - 0.5);
                double z = Math.abs(MathHelper.fractionalPart(pos.z) - 0.5);
                Direction.Axis axis = direction.getAxis();
                double g;
                if (axis == Direction.Axis.Y) {
                    g = Math.max(x, z);
                } else if (axis == Direction.Axis.Z) {
                    g = Math.max(x, y);
                } else {
                    g = Math.max(y, z);
                }

                int power = Math.max(1, MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - g) / 0.5, 0.0, 1.0)));
                //  end target block algorithm

                //  render redstone icon
                context.drawItem(Items.REDSTONE.getDefaultStack(), (int) (width * 0.51), (int) (height * 0.51));
                //  render redstone power
                context.drawText(client.textRenderer, String.valueOf(power), (int) (width * 0.51) + 2*client.textRenderer.fontHeight, (int) (height * 0.51) + client.textRenderer.fontHeight, 0xFFFFFFFF, true);
            }
        }
    }
}
