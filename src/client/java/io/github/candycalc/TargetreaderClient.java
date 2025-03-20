package io.github.candycalc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TargetreaderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// fabric wiki says it's deprecated but doesn't say what is intended to replace it. So I'm using it
		HudRenderCallback.EVENT.register(targetRenderer::renderTargetOverlay);
	}
}