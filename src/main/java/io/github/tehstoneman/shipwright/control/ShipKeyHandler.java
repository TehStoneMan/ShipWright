package io.github.tehstoneman.shipwright.control;

import io.github.tehstoneman.shipwright.ShipWright;
import io.github.tehstoneman.shipwright.entity.EntityShip;
import io.github.tehstoneman.shipwright.network.MsgClientOpenGUI;
import io.github.tehstoneman.shipwright.network.MsgClientShipAction;
import io.github.tehstoneman.shipwright.util.ModSettings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShipKeyHandler
{
	private ModSettings	config;
	private boolean				kbShipGuiPrevState, kbDisassemblePrevState;
	
	public ShipKeyHandler(ModSettings cfg)
	{
		config = cfg;
		kbShipGuiPrevState = kbDisassemblePrevState = false;
		
	}
	
	@SubscribeEvent
	public void keyPress(InputEvent.KeyInputEvent e)
	{
	}
	
	@SubscribeEvent
	public void updateControl(PlayerTickEvent e)
	{
		if (e.phase == TickEvent.Phase.START && e.side == Side.CLIENT && e.player == FMLClientHandler.instance().getClientPlayerEntity() && e.player.ridingEntity instanceof EntityShip)
		{
			if (config.kbShipInv.getIsKeyPressed() && !kbShipGuiPrevState)
			{
				MsgClientOpenGUI msg = new MsgClientOpenGUI(2);
				ShipWright.instance.pipeline.sendToServer(msg);
			}
			kbShipGuiPrevState = config.kbShipInv.getIsKeyPressed();
			
			if (config.kbDisassemble.getIsKeyPressed() && !kbDisassemblePrevState)
			{
				MsgClientShipAction msg = new MsgClientShipAction((EntityShip) e.player.ridingEntity, 1);
				ShipWright.instance.pipeline.sendToServer(msg);
			}
			kbDisassemblePrevState = config.kbDisassemble.getIsKeyPressed();
			
			int c = getHeightControl();
			EntityShip ship = (EntityShip) e.player.ridingEntity;
			if (c != ship.getController().getShipControl())
			{
				ship.getController().updateControl(ship, e.player, c);
			}
		}
	}
	
	public int getHeightControl()
	{
		if (config.kbAlign.getIsKeyPressed()) return 4;
		if (config.kbBrake.getIsKeyPressed()) return 3;
		int vert = 0;
		if (config.kbUp.getIsKeyPressed()) vert++;
		if (config.kbDown.getIsKeyPressed()) vert--;
		return vert == 0 ? 0 : vert < 0 ? 1 : vert > 0 ? 2 : 0;
	}
}
