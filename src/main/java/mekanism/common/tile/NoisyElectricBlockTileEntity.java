package mekanism.common.tile;

import mekanism.api.MekanismConfig.client;
import mekanism.api.Pos3D;
import mekanism.client.HolidayManager;
import mekanism.client.sound.ISoundSource;
import mekanism.common.Upgrade;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IHasSound;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.base.SoundWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.ResourceLocation;

public abstract class NoisyElectricBlockTileEntity extends TileEntityElectricBlock implements IHasSound, ISoundSource, IActiveState
{
	/** The ResourceLocation of the machine's sound */
	public ResourceLocation soundURL;

	/** The bundled URL of this machine's sound effect */
	@SideOnly(Side.CLIENT)
	public SoundWrapper sound;

	/** The path of this machine's sound */
	public String soundPath;

	/**
	 * The base of all blocks that deal with electricity and make noise.
	 *
	 * @param sound     - the sound path of this block
	 * @param name      - full name of this block
	 * @param maxEnergy - how much energy this block can store
	 */
	public NoisyElectricBlockTileEntity(String sound, String name, double maxEnergy)
	{
		super(name, maxEnergy);

		soundPath = sound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SoundWrapper getSound()
	{
		return sound;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldPlaySound()
	{
		return getActive() && !isInvalid();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getSoundLocation()
	{
		return soundURL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getVolume()
	{
		if(this instanceof IUpgradeTile && ((IUpgradeTile)this).getComponent().supports(Upgrade.MUFFLING))
		{
			return Math.max(0.001F, 1F - (float)((IUpgradeTile)this).getComponent().getUpgrades(Upgrade.MUFFLING)/(float)Upgrade.MUFFLING.getMax());
		}
		return 1F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getPitch()
	{
		return 1F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Pos3D getSoundPosition()
	{
		return new Pos3D(xCoord+0.5, yCoord+0.5, zCoord+0.5);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRepeat()
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRepeatDelay()
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AttenuationType getAttenuation()
	{
		return AttenuationType.LINEAR;
	}

	@Override
	public void validate()
	{
		super.validate();

		if(worldObj.isRemote)
		{
			try {
				soundURL = HolidayManager.filterSound(new ResourceLocation("mekanism", "tile." + soundPath));
				initSounds();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void initSounds()
	{
		sound = new SoundWrapper(this, this);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(worldObj.isRemote)
		{
			updateSound();
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateSound()
	{
		if(shouldPlaySound() && getSound().canRestart() && client.enableMachineSounds)
		{
			getSound().reset();
			getSound().play();
		}
	}
}
