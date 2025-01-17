package mezz.jei.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.input.IMouseHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;

import mezz.jei.Internal;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.gui.textures.Textures;
import net.minecraft.util.text.StringTextComponent;

/**
 * A gui button that has an {@link IDrawable} instead of a string label.
 */
public class GuiIconButton extends Button implements IMouseHandler {
	private final IDrawable icon;

	public GuiIconButton(IDrawable icon, IPressable pressable) {
		super(0, 0, 0, 0, StringTextComponent.EMPTY, pressable);
		this.icon = icon;
	}

	public void updateBounds(Rectangle2d area) {
		this.x = area.getX();
		this.y = area.getY();
		this.width = area.getWidth();
		this.height = area.getHeight();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Textures textures = Internal.getTextures();
			Minecraft minecraft = Minecraft.getInstance();
			DrawableNineSliceTexture texture = textures.getButtonForState(this.active, hovered);
			texture.draw(matrixStack, this.x, this.y, this.width, this.height);
			this.renderBg(matrixStack, minecraft, mouseX, mouseY);

			int color = 14737632;
			if (packedFGColor != 0) {
				color = packedFGColor;
			} else if (!this.active) {
				color = 10526880;
			} else if (hovered) {
				color = 16777120;
			}
			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			float red = (float) (color >> 16 & 255) / 255.0F;
			float blue = (float) (color >> 8 & 255) / 255.0F;
			float green = (float) (color & 255) / 255.0F;
			float alpha = (float) (color >> 24 & 255) / 255.0F;
			RenderSystem.color4f(red, blue, green, alpha);

			double xOffset = x + (width - icon.getWidth()) / 2.0;
			double yOffset = y + (height - icon.getHeight()) / 2.0;
			matrixStack.push();
			matrixStack.translate(xOffset, yOffset, 0);
			icon.draw(matrixStack);
			matrixStack.pop();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton, boolean doClick) {
		if (!this.active || !this.visible || !isMouseOver(mouseX, mouseY)) {
			return false;
		}
		if (!this.isValidClickButton(mouseButton)) {
			return false;
		}
		boolean flag = this.clicked(mouseX, mouseY);
		if (!flag) {
			return false;
		}
		if (doClick) {
			this.playDownSound(Minecraft.getInstance().getSoundHandler());
			this.onClick(mouseX, mouseY);
		}
		return true;
	}
}
