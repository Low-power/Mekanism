package mekanism.common.base;

import mekanism.client.sound.IResettableSound;
import mekanism.client.sound.ISoundSource;
import mekanism.client.sound.SoundHandler;
import mekanism.client.sound.TileSound;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundWrapper
{
	@SideOnly(Side.CLIENT)
	public IResettableSound sound;
	
	public SoundWrapper(IHasSound tile, ISoundSource source)
	{
		try {
			sound = new TileSound(tile, source);
		} catch(Exception e) {
		}
	}
	
	public SoundWrapper(IHasSound tile, ISoundSource source, ResourceLocation location)
	{
		try {
			sound = new TileSound(tile, source, location);
		} catch(Exception e) {
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void reset()
	{
		sound.reset();
	}
	
	@SideOnly(Side.CLIENT)
	public void play()
	{
		SoundHandler.playSound(sound);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean canRestart()
	{
		return SoundHandler.canRestartSound(sound);
	}
}
