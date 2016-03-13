package io.github.tehstoneman.shipwright.client.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

public interface IGuiWrapper
{
	public void drawTexturedRect(int x, int y, int u, int v, int w, int h);

	public void displayTooltip(String s, int xAxis, int yAxis);

	public void displayTooltips(List<String> list, int xAxis, int yAxis);

	public FontRenderer getFont();
}
