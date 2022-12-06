

package com.beckati.antighost;

import java.io.File;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(modid = AntiGhost.MODID, name = AntiGhost.NAME, version = AntiGhost.VERSION)
public class AntiGhost
{
	public static final String MODID = "antighost";
	public static final String NAME = "AntiGhost";
	public static final String VERSION = "1.3";

	public static File config;


	private int ticksToAutoRun;

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		ConfigHandler.registerConfig(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		ticksToAutoRun = 0;
		this.logger.log(Level.INFO, "AntiGhost Initialized");
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (++ticksToAutoRun > ConfigHandler.ticksPerAutoRun) {
				ticksToAutoRun = 0;
				this.execute(null, Minecraft.getMinecraft().player, null);
			}
		}
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		// this.logger.log(Level.INFO, "Requesting nearby blocks from server");
		Minecraft mc=Minecraft.getMinecraft();
		NetHandlerPlayClient conn = mc.getConnection();
		if (conn==null)
			return;
		BlockPos pos=sender.getPosition();
		for (int dy=-1; dy<=0; dy++)
			for (int dx=-1; dx<=1; dx++)
				for (int dz=-1; dz<=1; dz++) {
					if (dx != 0 && dz != 0) {
						BlockPos blockPos = new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz);
						IBlockState block = mc.world.getBlockState(blockPos);
						if (block.equals(Blocks.AIR)) {
							CPacketPlayerDigging packet = new CPacketPlayerDigging(
									CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
									new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz),
									EnumFacing.UP       // with ABORT_DESTROY_BLOCK, this value is unused
							);
							conn.sendPacket(packet);
						}
					}
				}
	}

}
