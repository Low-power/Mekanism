package mekanism.common.tile;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.MekanismConfig.usage;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;

public class SeismicVibratorTileEntity extends TileEntityElectricBlock implements IActiveState, IRedstoneControl, IBoundingBlock
{
	public boolean isActive;

	public boolean clientActive;

	public int updateDelay;

	public int clientPiston;

	public RedstoneControl controlType = RedstoneControl.DISABLED;

	public SeismicVibratorTileEntity()
	{
		super("SeismicVibrator", MachineType.SEISMIC_VIBRATOR.baseEnergy);

		inventory = new ItemStack[1];
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(worldObj.isRemote)
		{
			if(isActive)
			{
				clientPiston++;
			}

			if(updateDelay > 0)
			{
				updateDelay--;

				if(updateDelay == 0 && clientActive != isActive)
				{
					isActive = clientActive;
					MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
				}
			}
		}
		else {
			if(updateDelay > 0)
			{
				updateDelay--;

				if(updateDelay == 0 && clientActive != isActive)
				{
					Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
				}
			}

			ChargeUtils.discharge(0, this);

			if(MekanismUtils.canFunction(this) && getEnergy() >= usage.seismicVibratorUsage)
			{
				setActive(true);
				setEnergy(getEnergy()- usage.seismicVibratorUsage);
			}
			else {
				setActive(false);
			}
		}

		if(getActive())
		{
			Mekanism.activeVibrators.add(Coord4D.get(this));
		}
		else {
			Mekanism.activeVibrators.remove(Coord4D.get(this));
		}
	}

	@Override
	public void invalidate()
	{
		super.invalidate();

		Mekanism.activeVibrators.remove(Coord4D.get(this));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setBoolean("isActive", isActive);
		nbtTags.setInteger("controlType", controlType.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		clientActive = isActive = nbtTags.getBoolean("isActive");
		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);

		if(worldObj.isRemote)
		{
			clientActive = dataStream.readBoolean();
			controlType = RedstoneControl.values()[dataStream.readInt()];
			if(updateDelay == 0 && clientActive != isActive)
			{
				updateDelay = general.UPDATE_DELAY;
				isActive = clientActive;
				MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(Boolean.valueOf(isActive));
		data.add(Integer.valueOf(controlType.ordinal()));
		return data;
	}

	@Override
	public void setActive(boolean active)
	{
		isActive = active;

		if(clientActive != active && updateDelay == 0)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));

			updateDelay = 10;
			clientActive = active;
		}
	}

	@Override
	public boolean getActive()
	{
		return isActive;
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return true;
	}

	@Override
	public boolean canSetFacing(int facing)
	{
		return facing != 0 && facing != 1;
	}

	@Override
	public RedstoneControl getControlType()
	{
		return controlType;
	}

	@Override
	public EnumSet<ForgeDirection> getConsumingSides()
	{
		return EnumSet.of(ForgeDirection.getOrientation(facing).getOpposite());
	}

	@Override
	public void setControlType(RedstoneControl type)
	{
		controlType = type;
		MekanismUtils.saveChunk(this);
	}

	@Override
	public boolean canPulse()
	{
		return false;
	}

	@Override
	public void onPlace() {
		MekanismUtils.makeBoundingBlock(worldObj, Coord4D.get(this).getFromSide(ForgeDirection.UP), Coord4D.get(this));
	}

	@Override
	public void onBreak() {
		worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}
