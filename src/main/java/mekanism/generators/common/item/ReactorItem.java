package mekanism.generators.common.item;

import mekanism.api.EnumColor;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.block.Reactor.ReactorBlockType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.input.Keyboard;
import java.util.List;

public class ReactorItem extends ItemBlock
{
	public Block metaBlock;

	public ReactorItem(Block block)
	{
		super(block);
		metaBlock = block;
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}

	@Override
	public IIcon getIconFromDamage(int i)
	{
		return metaBlock.getIcon(2, i);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return getUnlocalizedName() + "." + ReactorBlockType.get(itemstack).name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
	{
		ReactorBlockType type = ReactorBlockType.get(itemstack);

		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO + "shift" + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails") + ".");
		}
		else {
			list.addAll(MekanismUtils.splitTooltip(type.getDescription(), itemstack));
		}
	}
}
