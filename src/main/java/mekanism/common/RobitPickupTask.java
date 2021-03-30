package mekanism.common;

import mekanism.common.entity.Robit;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import java.util.Iterator;
import java.util.List;

/*
 * 	Written by pixlepix (I'm in mekanism! Yay!)
 *	Boilerplate copied from RobitFollowTask
 */
public class RobitPickupTask extends EntityAIBase
{
	/** The robit entity. */
	private Robit robit;

	/** The world the robit is located in. */
	private World world;

	/** How fast the robit can travel. */
	private float moveSpeed;

	/** The robit's pathfinder. */
	private PathNavigate pathfinder;

	/** The ticker for updates. */
	private int ticker;

	/** Whether or not this robit avoids water. */
	private boolean avoidWater;
	private EntityItem closest;

	public RobitPickupTask(Robit robit, float speed)
	{
		this.robit = robit;
		world = robit.worldObj;
		moveSpeed = speed;
		pathfinder = robit.getNavigator();
	}

	@Override
	public boolean shouldExecute()
	{
		if(!robit.getDropPickup())
		{
			return false;
		}
		if(closest != null && closest.getDistanceSqToEntity(closest) > 100 && pathfinder.getPathToXYZ(closest.posX, closest.posY, closest.posZ) != null)
		{
			return true;
		}

		List items = robit.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(robit.posX-10, robit.posY-10, robit.posZ-10, robit.posX+10, robit.posY+10, robit.posZ+10));
		Iterator iter = items.iterator();
		//Cached for slight performance
		double closestDistance = -1;

		while(iter.hasNext())
		{
			EntityItem entity = (EntityItem)iter.next();

			double distance = robit.getDistanceToEntity(entity);

			if(distance <= 10)
			{
				if(closestDistance == -1 || distance < closestDistance)
				{
					if(pathfinder.getPathToXYZ(entity.posX, entity.posY, entity.posZ) != null)
					{
						closest = entity;
						closestDistance = distance;
					}
				}
			}
		}

		if(closest == null || closest.isDead)
		{
			//No valid items
			return false;
		}

		return true;

	}

	@Override
	public boolean continueExecuting()
	{
		return !closest.isDead && !pathfinder.noPath() && robit.getDistanceSqToEntity(closest) > 100 && robit.getDropPickup() && robit.getEnergy() > 0 && closest.worldObj.provider == robit.worldObj.provider;
	}

	@Override
	public void startExecuting()
	{
		ticker = 0;
		avoidWater = robit.getNavigator().getAvoidsWater();
		robit.getNavigator().setAvoidsWater(false);
	}

	@Override
	public void resetTask()
	{
		pathfinder.clearPathEntity();
		robit.getNavigator().setAvoidsWater(avoidWater);
	}

	@Override
	public void updateTask()
	{
		if(!robit.getDropPickup())
		{
			return;
		}
		robit.getLookHelper().setLookPositionWithEntity(closest, 6F, robit.getVerticalFaceSpeed()/10);

		if(--ticker <= 0)
		{
			ticker = 10;

			if(!pathfinder.tryMoveToEntityLiving(closest, moveSpeed))
			{
				if(robit.getDistanceSqToEntity(closest) >= 144D)
				{
					int x = MathHelper.floor_double(closest.posX) - 2;
					int y = MathHelper.floor_double(closest.posZ) - 2;
					int z = MathHelper.floor_double(closest.boundingBox.minY);

					for(int l = 0; l <= 4; ++l)
					{
						for(int i1 = 0; i1 <= 4; ++i1)
						{
							if((l < 1 || i1 < 1 || l > 3 || i1 > 3) && world.doesBlockHaveSolidTopSurface(world, x + l, z - 1, y + i1) && !world.getBlock(x + l, z, y + i1).isNormalCube() && !world.getBlock(x + l, z + 1, y + i1).isNormalCube())
							{
								robit.setLocationAndAngles((x + l) + 0.5F, z, (y + i1) + 0.5F, robit.rotationYaw, robit.rotationPitch);
								pathfinder.clearPathEntity();
								return;
							}
						}
					}
				}
			}
		}
	}
}
