package mekanism.common.tile;

import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.block.Machine.MachineType;
import mekanism.common.tile.component.ConfigTileComponent;
import mekanism.common.tile.component.EjectorTileComponent;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.InventoryUtils;

public class AdvancedFactoryTileEntity extends FactoryTileEntity
{
	public AdvancedFactoryTileEntity()
	{
		super(FactoryTier.ADVANCED, MachineType.ADVANCED_FACTORY);

		configComponent = new ConfigTileComponent(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);
		configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_RED, new int[] {5, 6, 7, 8, 9}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_BLUE, new int[] {10, 11, 12, 13, 14}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_GREEN, new int[] {1}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Extra", EnumColor.PURPLE, new int[] {4}));
		configComponent.setConfig(TransmissionType.ITEM, new byte[] {4, 0, 0, 3, 1, 2});
		configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
		configComponent.addOutput(TransmissionType.GAS, new SideData("Gas", EnumColor.DARK_RED, new int[] {0}));
		configComponent.fillConfig(TransmissionType.GAS, 1);
		configComponent.setCanEject(TransmissionType.GAS, false);
		configComponent.setInputConfig(TransmissionType.ENERGY);

		upgradeComponent = new TileComponentUpgrade(this, 0);
		upgradeComponent.setSupported(Upgrade.MUFFLING);
		ejectorComponent = new EjectorTileComponent(this);
		ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
	}
}
