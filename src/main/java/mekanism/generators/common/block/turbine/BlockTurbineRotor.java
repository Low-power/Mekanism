package mekanism.generators.common.block.turbine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.generators.common.GeneratorsItem;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockTurbineRotor extends BlockMekanismContainer implements IHasTileEntity<TileEntityTurbineRotor> {

    private static final AxisAlignedBB ROTOR_BOUNDS = new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);

    public BlockTurbineRotor() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(8F);
        setRegistryName(new ResourceLocation(MekanismGenerators.MODID, "turbine_rotor"));
    }

    @Override
    @Deprecated
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            final TileEntity tileEntity = MekanismUtils.getTileEntity(world, pos);
            if (tileEntity instanceof TileEntityMekanism) {
                ((TileEntityMekanism) tileEntity).onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    @Deprecated
    public float getPlayerRelativeBlockHardness(BlockState state, @Nonnull PlayerEntity player, @Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return SecurityUtils.canAccess(player, tile) ? super.getPlayerRelativeBlockHardness(state, player, world, pos) : 0.0F;
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntityMekanism tileEntity = (TileEntityMekanism) world.getTileEntity(pos);
        if (!world.isRemote && tileEntity instanceof TileEntityTurbineRotor) {
            //TODO: Evaluate
            int amount = ((TileEntityTurbineRotor) tileEntity).getHousedBlades();
            if (amount > 0) {
                spawnAsEntity(world, pos, GeneratorsItem.TURBINE_BLADE.getItemStack(amount));
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity entityplayer, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntityMekanism tileEntity = (TileEntityMekanism) world.getTileEntity(pos);
        if (tileEntity.tryWrench(state, entityplayer, hand, () -> new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos)) != WrenchResult.PASS) {
            return true;
        }

        ItemStack stack = entityplayer.getHeldItem(hand);
        TileEntityTurbineRotor rod = (TileEntityTurbineRotor) tileEntity;
        if (!entityplayer.isSneaking()) {
            if (!stack.isEmpty() && stack.getItem() == GeneratorsItem.TURBINE_BLADE.getItem()) {
                if (rod.addBlade()) {
                    if (!entityplayer.isCreative()) {
                        stack.shrink(1);
                        if (stack.getCount() == 0) {
                            entityplayer.setHeldItem(hand, ItemStack.EMPTY);
                        }
                    }
                }
                return true;
            }
        } else if (stack.isEmpty()) {
            if (rod.removeBlade()) {
                if (!entityplayer.isCreative()) {
                    entityplayer.setHeldItem(hand, GeneratorsItem.TURBINE_BLADE.getItemStack());
                    entityplayer.inventory.markDirty();
                }
            }
        } else if (stack.getItem() == GeneratorsItem.TURBINE_BLADE.getItem()) {
            if (stack.getCount() < stack.getMaxStackSize()) {
                if (rod.removeBlade()) {
                    if (!entityplayer.isCreative()) {
                        stack.grow(1);
                        entityplayer.inventory.markDirty();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull BlockState state) {
        return new TileEntityTurbineRotor();
    }

    @OnlyIn(Dist.CLIENT)
    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(BlockState state, IWorldReader world, BlockPos pos) {
        return ROTOR_BOUNDS;
    }

    @Override
    @Deprecated
    public boolean isSideSolid(BlockState state, @Nonnull IWorldReader world, @Nonnull BlockPos pos, Direction side) {
        //TODO
        return false;
    }

    @Nullable
    @Override
    public Class<? extends TileEntityTurbineRotor> getTileClass() {
        return TileEntityTurbineRotor.class;
    }
}