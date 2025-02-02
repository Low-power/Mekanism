package mekanism.generators.common.tile.turbine;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.BasicBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTurbineRotor extends BasicBlockTileEntity
{
	public List<Coord4D> rotors = new ArrayList<Coord4D>();
	
	public boolean hasComplex;
	
	public String multiblockUUID;
	
	//Total blades on server, housed blades on client
	public int blades = 0;
	
	//Client stuff
	public int clientIndex;
	
	public float rotationLower;
	public float rotationUpper;
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public void onNeighborChange(Block block)
	{
		if(!worldObj.isRemote)
		{
			updateRotors();
		}
	}
	
	public void updateRotors()
	{
		if(rotors.contains(Coord4D.get(this)))
		{
			rotors.add(Coord4D.get(this));
		}
		
		buildRotors();
		Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
	}
	
	private void buildRotors()
	{
		List<Coord4D> newRotors = new ArrayList<Coord4D>();
		int newBlades = 0;
		boolean complex = false;
		String id = null;
		
		Coord4D pointer = Coord4D.get(this);
		
		//Go to bottom rotor
		while(true)
		{
			if(isRotor(pointer.getFromSide(ForgeDirection.DOWN)))
			{
				pointer.step(ForgeDirection.DOWN);
				continue;
			}
			
			break;
		}
		
		//Put all rotors in new list, top to bottom
		while(true)
		{
			newRotors.add(pointer.clone());
			newBlades += ((TileEntityTurbineRotor)pointer.getTileEntity(worldObj)).getHousedBlades();
			
			if(isRotor(pointer.getFromSide(ForgeDirection.UP)))
			{
				pointer.step(ForgeDirection.UP);
				continue;
			}
			
			break;
		}
		
		if(isComplex(pointer.getFromSide(ForgeDirection.UP)))
		{
			id = ((TileEntityRotationalComplex)pointer.getFromSide(ForgeDirection.UP).getTileEntity(worldObj)).multiblockUUID;
			complex = true;
		}
		
		//Update all rotors, send packet if necessary
		for(Coord4D coord : newRotors)
		{
			TileEntityTurbineRotor rotor = (TileEntityTurbineRotor)coord.getTileEntity(worldObj);
			int prevHoused = rotor.getHousedBlades();
			int prevBlades = rotor.blades;
			
			rotor.rotors = newRotors;
			rotor.blades = newBlades;
			rotor.multiblockUUID = id;
			
			if(rotors.indexOf(coord) == rotors.size()-1)
			{
				rotor.hasComplex = complex;
			}
			else {
				rotor.hasComplex = false;
			}
			
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(coord, rotor.getNetworkedData(new ArrayList())), new Range4D(coord));
		}
	}
	
	public boolean editBlade(boolean add)
	{
		if(!rotors.contains(Coord4D.get(this)))
		{
			rotors.add(Coord4D.get(this));
		}
		
		if((add && (rotors.size()*2) - blades > 0) || (!add && (blades > 0)))
		{
			for(Coord4D coord : rotors)
			{
				TileEntityTurbineRotor rotor = (TileEntityTurbineRotor)coord.getTileEntity(worldObj);
				rotor.internalEditBlade(add);
			}
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public void internalEditBlade(boolean add)
	{
		int prev = getHousedBlades();
		
		blades += add ? 1 : -1;
		
		if(getHousedBlades() != prev)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
		}
	}
	
	public int getHousedBlades()
	{
		if(!worldObj.isRemote)
		{
			if(rotors.size() > 0)
			{
				return Math.max(0, Math.min(2, blades - (rotors.indexOf(Coord4D.get(this)))*2));
			}
			else {
				return blades;
			}
		}
		else {
			return blades;
		}
	}
	
	private boolean isRotor(Coord4D coord)
	{
		return coord.getTileEntity(worldObj) instanceof TileEntityTurbineRotor;
	}
	
	private boolean isComplex(Coord4D coord)
	{
		return coord.getTileEntity(worldObj) instanceof TileEntityRotationalComplex;
	}
	
	@Override
	public void onChunkLoad()
	{
		super.onChunkLoad();
		
		if(!worldObj.isRemote)
		{
			updateRotors();
		}
	}
	
	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
			int prevBlades = blades;
			int prevIndex = clientIndex;
			
			blades = dataStream.readInt();
			clientIndex = dataStream.readInt();
			
			if(dataStream.readBoolean())
			{
				multiblockUUID = PacketHandler.readString(dataStream);
			}
			else {
				multiblockUUID = null;
			}
			
			if(prevBlades != blades || prevIndex != clientIndex)
			{
				rotationLower = 0;
				rotationUpper = 0;
			}
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(getHousedBlades());
		data.add(rotors.indexOf(Coord4D.get(this)));
		
		if(multiblockUUID != null)
		{
			data.add(true);
			data.add(multiblockUUID);
		}
		else {
			data.add(false);
		}
		
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		blades = nbtTags.getInteger("blades");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setInteger("blades", getHousedBlades());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void onUpdate() {}
}
