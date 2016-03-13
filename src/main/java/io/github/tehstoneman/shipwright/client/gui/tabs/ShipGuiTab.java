package io.github.tehstoneman.shipwright.client.gui.tabs;

import io.github.tehstoneman.shipwright.client.gui.IGuiWrapper;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class ShipGuiTab
{
	public static Minecraft mc = Minecraft.getMinecraft();
	public ResourceLocation resourceLocation;
	public IGuiWrapper guiObj;
	public ResourceLocation defaultLocation;

	public ShipGuiTab(ResourceLocation resource, IGuiWrapper gui, ResourceLocation def)
	{
		resourceLocation = resource;
		guiObj = gui;
		defaultLocation = def;
	}

	public void displayTooltip(String s, int xAxis, int yAxis)
	{
		guiObj.displayTooltip(s, xAxis, yAxis);
	}

	public void displayTooltips(List<String> list, int xAxis, int yAxis)
	{
		guiObj.displayTooltips(list, xAxis, yAxis);
	}

	public void offsetX(int xSize)
	{
		if(guiObj instanceof GuiContainer)
		{
		}
	}

	public void offsetY(int ySize)
	{
		if(guiObj instanceof GuiContainer)
		{
		}
	}
	
	public void offsetLeft(int guiLeft)
	{
		if(guiObj instanceof GuiContainer)
		{
		}
	}
	
	public void offsetTop(int guiTop)
	{
		if(guiObj instanceof GuiContainer)
		{
		}
	}
	
	public void renderScaledText(String text, int x, int y, int color, int maxX)
	{
		int length = getFontRenderer().getStringWidth(text);
		
		if(length <= maxX)
		{
			getFontRenderer().drawString(text, x, y, color);
		}
		else {
			float scale = (float)maxX/length;
			float reverse = 1/scale;
			float yAdd = 4-(scale*8)/2F;
			
			GL11.glPushMatrix();
			
			GL11.glScalef(scale, scale, scale);
			getFontRenderer().drawString(text, (int)(x*reverse), (int)((y*reverse)+yAdd), color);
			
			GL11.glPopMatrix();
		}
	}

	public FontRenderer getFontRenderer()
	{
		return guiObj.getFont();
	}
	
	public void mouseClickMove(int mouseX, int mouseY, int button, long ticks) {}

	public void mouseMovedOrUp(int x, int y, int type) {}
	
	public abstract void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight);

	public abstract void renderForeground(int xAxis, int yAxis);

	public abstract void preMouseClicked(int xAxis, int yAxis, int button);

	public abstract void mouseClicked(int xAxis, int yAxis, int button);
}