package mekanism.common.tile;

import java.util.HashSet;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.common.integration.IComputerIntegration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityThermalEvaporationBlock extends ContainerTileEntity implements IComputerIntegration
{
	public Coord4D master;
	
	public boolean attempted;

	public TileEntityThermalEvaporationBlock()
	{
		super("ThermalEvaporationBlock");

		inventory = new ItemStack[0];
	}

	public TileEntityThermalEvaporationBlock(String fullName)
	{
		super(fullName);

		inventory = new ItemStack[0];
	}

	@Override
	public void onUpdate() 
	{
		if(!worldObj.isRemote && ticker == 5 && !attempted && master == null)
		{
			updateController();
		}
		
		attempted = false;
	}

	public void addToStructure(Coord4D controller)
	{
		master = controller;
	}

	public void controllerGone()
	{
		master = null;
	}

	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();

		if(master != null)
		{
			TileEntityThermalEvaporationController tile = getController();
			
			if(tile != null)
			{
				((TileEntityThermalEvaporationController)tile).refresh();
			}
		}
	}

	@Override
	public void onNeighborChange(Block block)
	{
		super.onNeighborChange(block);

		if(!worldObj.isRemote)
		{
			TileEntityThermalEvaporationController tile = getController();
			
			if(tile != null)
			{
				((TileEntityThermalEvaporationController)tile).refresh();
			}
			else {
				updateController();
			}
		}
	}
	
	public void updateController()
	{
		if(!(this instanceof TileEntityThermalEvaporationController))
		{
			TileEntityThermalEvaporationController found = new ControllerFinder().find();
			
			if(found != null)
			{
				found.refresh();
			}
		}
	}
	
	public TileEntityThermalEvaporationController getController()
	{
		if(master != null)
		{
			TileEntity tile = master.getTileEntity(worldObj);
			
			if(tile instanceof TileEntityThermalEvaporationController)
			{
				return (TileEntityThermalEvaporationController)tile;
			}
		}
		
		return null;
	}
	
	public class ControllerFinder
	{
		public TileEntityThermalEvaporationController found;
		
		public Set<Coord4D> iterated = new HashSet<Coord4D>();
		
		public void loop(Coord4D pos)
		{
			if(iterated.size() > 512 || found != null)
			{
				return;
			}
			
			iterated.add(pos);
			
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				Coord4D coord = pos.getFromSide(side);
				
				if(!iterated.contains(coord) && coord.getTileEntity(worldObj) instanceof TileEntityThermalEvaporationBlock)
				{
					((TileEntityThermalEvaporationBlock)coord.getTileEntity(worldObj)).attempted = true;
					
					if(coord.getTileEntity(worldObj) instanceof TileEntityThermalEvaporationController)
					{
						found = (TileEntityThermalEvaporationController)coord.getTileEntity(worldObj);
						return;
					}
					
					loop(coord);
				}
			}
		}
		
		public TileEntityThermalEvaporationController find()
		{
			loop(Coord4D.get(TileEntityThermalEvaporationBlock.this));
			
			return found;
		}
	}
	
	private static final String[] methods = new String[] {"getTemperature", "getHeight", "isFormed", "getInput", "getOutput"};

	@Override
	public String[] getMethods() 
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] args) throws Exception 
	{
		TileEntityThermalEvaporationController controller = getController();
		
		if(controller == null)
		{
			return new Object[] {"Unformed."};
		}
		
		switch(method)
		{
			case 0:
				return new Object[] {controller.temperature};
			case 1:
				return new Object[] {controller.height};
			case 2:
				return new Object[] {controller.structured};
			case 3:
				return new Object[] {controller.inputTank.getFluidAmount()};
			case 4:
				return new Object[] {controller.outputTank.getFluidAmount()};
			default:
				throw new NoSuchMethodException();
		}
	}
}
