package mekanism.common;

import mekanism.common.entity.Robit;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class RobitFollowTask extends EntityAIBase
{
	/** The robit entity. */
	private Robit robit;

	/** The robit's owner. */
	private EntityPlayer owner;

	/** The world the robit is located in. */
	private World world;

	/** How fast the robit can travel. */
	private float moveSpeed;

	/** The robit's pathfinder. */
	private PathNavigate pathfinder;

	/** The ticker for updates. */
	private int ticker;

	/** The distance between the owner the robit must be at in order for the protocol to begin. */
	private float maxDist;

	/** The distance between the owner the robit must reach before it stops the protocol. */
	private float minDist;

	/** Whether or not this robit avoids water. */
	private boolean avoidWater;

	public RobitFollowTask(Robit robit, float speed, float min, float max)
	{
		this.robit = robit;
		world = robit.worldObj;
		moveSpeed = speed;
		pathfinder = robit.getNavigator();
		minDist = min;
		maxDist = max;
	}

	@Override
	public boolean shouldExecute()
	{
		EntityPlayer player = robit.getOwner();

		if(player == null)
		{
			return false;
		}
		else if(robit.worldObj.provider.dimensionId != player.worldObj.provider.dimensionId)
		{
			return false;
		}
		else if(!robit.getFollowing())
		{
			//Still looks up at the player if on chargepad or not following

			robit.getLookHelper().setLookPositionWithEntity(player, 6F, robit.getVerticalFaceSpeed()/10);
			return false;
		}
		else if(robit.getDistanceSqToEntity(player) < (minDist * minDist))
		{
			return false;
		}
		else if(robit.getEnergy() == 0)
		{
			return false;
		}
		else {
			owner = player;
			return true;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return !pathfinder.noPath() && robit.getDistanceSqToEntity(owner) > (maxDist * maxDist) && robit.getFollowing() && robit.getEnergy() > 0 && owner.worldObj.provider == robit.worldObj.provider;
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
		owner = null;
		pathfinder.clearPathEntity();
		robit.getNavigator().setAvoidsWater(avoidWater);
	}

	@Override
	public void updateTask()
	{
		robit.getLookHelper().setLookPositionWithEntity(owner, 6F, robit.getVerticalFaceSpeed()/10);

		if(robit.getFollowing())
		{
			if(--ticker <= 0)
			{
				ticker = 10;

				if(!pathfinder.tryMoveToEntityLiving(owner, moveSpeed))
				{
					if(robit.getDistanceSqToEntity(owner) >= 144D)
					{
						int x = MathHelper.floor_double(owner.posX) - 2;
						int y = MathHelper.floor_double(owner.posZ) - 2;
						int z = MathHelper.floor_double(owner.boundingBox.minY);

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
}
