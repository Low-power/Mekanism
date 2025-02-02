package mekanism.common;

import mekanism.api.EnumColor;
import mekanism.common.multipart.TransmitterType;
import mekanism.common.util.LangUtils;
import codechicken.lib.colour.ColourRGBA;
import net.minecraft.util.ResourceLocation;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

/**
 * Tier information for Mekanism.  This currently includes tiers for Energy Cubes and Smelting Factories.
 * @author aidancbrady
 *
 */
public final class Tier
{
	private static List<ITier> tierTypes = new ArrayList<ITier>();

	private static boolean initiated = false;

	/** The default tiers used in Mekanism.
	 * @author aidancbrady
	 */
	public static enum BaseTier
	{
		BASIC("Basic", EnumColor.BRIGHT_GREEN),
		ADVANCED("Advanced", EnumColor.DARK_RED),
		ELITE("Elite", EnumColor.DARK_BLUE),
		ULTIMATE("Ultimate", EnumColor.PURPLE),
		CREATIVE("Creative", EnumColor.BLACK);

		public String getName()
		{
			return name;
		}

		public String getLocalizedName()
		{
			return LangUtils.localize("tier." + getName());
		}

		public EnumColor getColor()
		{
			return color;
		}

		public boolean isObtainable()
		{
			return this != CREATIVE;
		}

		private String name;
		private EnumColor color;

		private BaseTier(String s, EnumColor c)
		{
			name = s;
			color = c;
		}
	}

	public static enum EnergyCubeTier implements ITier
	{
		BASIC(2000000, 800),
		ADVANCED(8000000, 3200),
		ELITE(32000000, 12800),
		ULTIMATE(128000000, 51200),
		CREATIVE(Double.MAX_VALUE, Double.MAX_VALUE);

		public double maxEnergy;
		private double baseMaxEnergy;

		public double output;
		private double baseOutput;

		private EnergyCubeTier(double max, double out)
		{
			baseMaxEnergy = maxEnergy = max;
			baseOutput = output = out;
		}

		public static EnergyCubeTier getFromName(String tierName)
		{
			for(EnergyCubeTier tier : values())
			{
				if(tierName.contains(tier.getBaseTier().getName()))
				{
					return tier;
				}
			}
			return BASIC;
		}

		@Override
		public void loadConfig()
		{
			if(this != CREATIVE)
			{
				maxEnergy = Mekanism.configuration.get("tier", getBaseTier().getName() + "EnergyCubeMaxEnergy", baseMaxEnergy).getDouble();
				output = Mekanism.configuration.get("tier", getBaseTier().getName() + "EnergyCubeOutput", baseOutput).getDouble();
			}
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			if(this != CREATIVE)
			{
				maxEnergy = dataStream.readDouble();
				output = dataStream.readDouble();
			}
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			if(this != CREATIVE)
			{
				dataStream.writeDouble(maxEnergy);
				dataStream.writeDouble(output);
			}
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}
	}

	public static enum InductionCellTier implements ITier
	{
		BASIC(1E9D),
		ADVANCED(8E9D),
		ELITE(64E9D),
		ULTIMATE(512E9D);

		public double maxEnergy;
		private double baseMaxEnergy;

		private InductionCellTier(double max)
		{
			baseMaxEnergy = maxEnergy = max;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			maxEnergy = Mekanism.configuration.get("tier", getBaseTier().getName() + "InductionCellMaxEnergy", baseMaxEnergy).getDouble();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			maxEnergy = dataStream.readDouble();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeDouble(maxEnergy);
		}
	}

	public static enum InductionProviderTier implements ITier
	{
		BASIC(64000),
		ADVANCED(512000),
		ELITE(4096000),
		ULTIMATE(32768000);

		public double output;
		private double baseOutput;

		private InductionProviderTier(double out)
		{
			baseOutput = output = out;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			output = Mekanism.configuration.get("tier", getBaseTier().getName() + "InductionProviderOutput", baseOutput).getDouble();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			output = dataStream.readDouble();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeDouble(output);
		}
	}

	public static enum FactoryTier
	{
		BASIC(3, new ResourceLocation("mekanism", "gui/factory/BasicFactoryGui.png")),
		ADVANCED(5, new ResourceLocation("mekanism", "gui/factory/AdvancedFactoryGui.png")),
		ELITE(7, new ResourceLocation("mekanism", "gui/factory/EliteFactoryGui.png"));

		public int processes;
		public ResourceLocation guiLocation;

		public static FactoryTier getFromName(String tierName)
		{
			for(FactoryTier tier : values())
			{
				if(tierName.contains(tier.getBaseTier().getName()))
				{
					return tier;
				}
			}

			Mekanism.logger.error("Invalid tier identifier when retrieving with name.");
			return BASIC;
		}

		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		private FactoryTier(int process, ResourceLocation gui)
		{
			processes = process;
			guiLocation = gui;
		}
	}

	public static enum CableTier implements ITier
	{
		BASIC(3200, TransmitterType.UNIVERSAL_CABLE_BASIC),
		ADVANCED(12800, TransmitterType.UNIVERSAL_CABLE_ADVANCED),
		ELITE(64000, TransmitterType.UNIVERSAL_CABLE_ELITE),
		ULTIMATE(320000, TransmitterType.UNIVERSAL_CABLE_ULTIMATE);

		public int cableCapacity;
		private int baseCapacity;

		public TransmitterType type;

		private CableTier(int capacity, TransmitterType transmitterType)
		{
			baseCapacity = cableCapacity = capacity;
			type = transmitterType;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			cableCapacity = Mekanism.configuration.get("tier", getBaseTier().getName() + "CableCapacity", baseCapacity).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			cableCapacity = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(cableCapacity);
		}

		public static CableTier get(BaseTier tier)
		{
			for(CableTier transmitter : values())
			{
				if(transmitter.getBaseTier() == tier)
				{
					return transmitter;
				}
			}
			return BASIC;
		}
	}

	public static enum PipeTier implements ITier
	{
		BASIC(1000, 100, TransmitterType.MECHANICAL_PIPE_BASIC),
		ADVANCED(4000, 400, TransmitterType.MECHANICAL_PIPE_ADVANCED),
		ELITE(16000, 1600, TransmitterType.MECHANICAL_PIPE_ELITE),
		ULTIMATE(64000, 6400, TransmitterType.MECHANICAL_PIPE_ULTIMATE);

		public int pipeCapacity;
		private int baseCapacity;

		public int pipePullAmount;
		private int basePull;

		public TransmitterType type;

		private PipeTier(int capacity, int pullAmount, TransmitterType transmitterType)
		{
			baseCapacity = pipeCapacity = capacity;
			basePull = pipePullAmount = pullAmount;
			type = transmitterType;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			pipeCapacity = Mekanism.configuration.get("tier", getBaseTier().getName() + "PipeCapacity", baseCapacity).getInt();
			pipePullAmount = Mekanism.configuration.get("tier", getBaseTier().getName() + "PipePullAmount", basePull).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			pipeCapacity = dataStream.readInt();
			pipePullAmount = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(pipeCapacity);
			dataStream.writeInt(pipePullAmount);
		}

		public static PipeTier get(BaseTier tier)
		{
			for(PipeTier transmitter : values())
			{
				if(transmitter.getBaseTier() == tier)
				{
					return transmitter;
				}
			}
			return BASIC;
		}
	}

	public static enum TubeTier implements ITier
	{
		BASIC(256, 64, TransmitterType.PRESSURIZED_TUBE_BASIC),
		ADVANCED(1024, 256, TransmitterType.PRESSURIZED_TUBE_ADVANCED),
		ELITE(4096, 1024, TransmitterType.PRESSURIZED_TUBE_ELITE),
		ULTIMATE(16384, 4096, TransmitterType.PRESSURIZED_TUBE_ULTIMATE);

		public int tubeCapacity;
		private int baseCapacity;

		public int tubePullAmount;
		private int basePull;

		public TransmitterType type;

		private TubeTier(int capacity, int pullAmount, TransmitterType transmitterType)
		{
			baseCapacity = tubeCapacity = capacity;
			basePull = tubePullAmount = pullAmount;
			type = transmitterType;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			tubeCapacity = Mekanism.configuration.get("tier", getBaseTier().getName() + "TubeCapacity", baseCapacity).getInt();
			tubePullAmount = Mekanism.configuration.get("tier", getBaseTier().getName() + "TubePullAmount", basePull).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			tubeCapacity = dataStream.readInt();
			tubePullAmount = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(tubeCapacity);
			dataStream.writeInt(tubePullAmount);
		}

		public static TubeTier get(BaseTier tier)
		{
			for(TubeTier transmitter : values())
			{
				if(transmitter.getBaseTier() == tier)
				{
					return transmitter;
				}
			}
			return BASIC;
		}
	}

	public static enum TransporterTier implements ITier
	{
		BASIC(1, 5, TransmitterType.LOGISTICAL_TRANSPORTER_BASIC),
		ADVANCED(16, 10, TransmitterType.LOGISTICAL_TRANSPORTER_ADVANCED),
		ELITE(32, 20, TransmitterType.LOGISTICAL_TRANSPORTER_ELITE),
		ULTIMATE(64, 50, TransmitterType.LOGISTICAL_TRANSPORTER_ULTIMATE);

		public int pullAmount;
		private int basePull;

		public int speed;
		private int baseSpeed;

		public TransmitterType type;

		private TransporterTier(int pull, int s, TransmitterType transmitterType)
		{
			basePull = pullAmount = pull;
			baseSpeed = speed = s;
			type = transmitterType;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			pullAmount = Mekanism.configuration.get("tier", getBaseTier().getName() + "TransporterPullAmount", basePull).getInt();
			speed = Mekanism.configuration.get("tier", getBaseTier().getName() + "TransporterSpeed", baseSpeed).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			pullAmount = dataStream.readInt();
			speed = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(pullAmount);
			dataStream.writeInt(speed);
		}

		public static TransporterTier get(BaseTier tier)
		{
			for(TransporterTier transmitter : values())
			{
				if(transmitter.getBaseTier() == tier)
				{
					return transmitter;
				}
			}
			return BASIC;
		}
	}

	public static enum ConductorTier implements ITier
	{
		BASIC(5, 1, 10, new ColourRGBA(0.2, 0.2, 0.2, 1), TransmitterType.THERMODYNAMIC_CONDUCTOR_BASIC),
		ADVANCED(5, 1, 400, new ColourRGBA(0.2, 0.2, 0.2, 1), TransmitterType.THERMODYNAMIC_CONDUCTOR_ADVANCED),
		ELITE(5, 1, 8000, new ColourRGBA(0.2, 0.2, 0.2, 1), TransmitterType.THERMODYNAMIC_CONDUCTOR_ELITE),
		ULTIMATE(5, 1, 100000, new ColourRGBA(0.2, 0.2, 0.2, 1), TransmitterType.THERMODYNAMIC_CONDUCTOR_ULTIMATE);

		public double inverseConduction;
		private double baseConduction;

		public double inverseHeatCapacity;
		private double baseHeatCapacity;

		public double inverseConductionInsulation;
		private double baseConductionInsulation;

		public ColourRGBA baseColour;

		public TransmitterType type;

		private ConductorTier(double inversek, double inverseC, double insulationInversek, ColourRGBA colour, TransmitterType transmitterType)
		{
			baseConduction = inverseConduction = inversek;
			baseHeatCapacity = inverseHeatCapacity = inverseC;
			baseConductionInsulation = inverseConductionInsulation = insulationInversek;
			baseColour = colour;
			type = transmitterType;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			inverseConduction = Mekanism.configuration.get("tier", getBaseTier().getName() + "ConductorInverseConduction", baseConduction).getDouble();
			inverseHeatCapacity = Mekanism.configuration.get("tier", getBaseTier().getName() + "ConductorHeatCapacity", baseHeatCapacity).getDouble();
			inverseConductionInsulation = Mekanism.configuration.get("tier", getBaseTier().getName() + "ConductorConductionInsulation", baseConductionInsulation).getDouble();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			inverseConduction = dataStream.readDouble();
			inverseHeatCapacity = dataStream.readDouble();
			inverseConductionInsulation = dataStream.readDouble();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeDouble(inverseConduction);
			dataStream.writeDouble(inverseHeatCapacity);
			dataStream.writeDouble(inverseConductionInsulation);
		}

		public static ConductorTier get(BaseTier tier)
		{
			for(ConductorTier transmitter : values())
			{
				if(transmitter.getBaseTier() == tier)
				{
					return transmitter;
				}
			}
			return BASIC;
		}
	}

	public static enum FluidTankTier implements ITier
	{
		BASIC(14000, 400),
		ADVANCED(28000, 800),
		ELITE(56000, 1600),
		ULTIMATE(112000, 3200);

		public int storage;
		private int baseStorage;

		public int output;
		private int baseOutput;

		private FluidTankTier(int s, int o)
		{
			baseStorage = storage = s;
			baseOutput = output = o;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			storage = Mekanism.configuration.get("tier", getBaseTier().getName() + "FluidTankStorage", baseStorage).getInt();
			output = Mekanism.configuration.get("tier", getBaseTier().getName() + "FluidTankOutput", baseOutput).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			storage = dataStream.readInt();
			output = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(storage);
			dataStream.writeInt(output);
		}
	}

	public static enum GasTankTier implements ITier
	{
		BASIC(64000, 256),
		ADVANCED(128000, 512),
		ELITE(256000, 1028),
		ULTIMATE(512000, 2056);

		public int storage;
		private int baseStorage;

		public int output;
		private int baseOutput;

		private GasTankTier(int s, int o)
		{
			baseStorage = storage = s;
			baseOutput = output = o;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			storage = Mekanism.configuration.get("tier", getBaseTier().getName() + "GasTankStorage", baseStorage).getInt();
			output = Mekanism.configuration.get("tier", getBaseTier().getName() + "GasTankOutput", baseOutput).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			storage = dataStream.readInt();
			output = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(storage);
			dataStream.writeInt(output);
		}
	}

	public static enum BinTier implements ITier
	{
		BASIC(4096),
		ADVANCED(8192),
		ELITE(32768),
		ULTIMATE(262144);

		public int storage;
		private int baseStorage;

		private BinTier(int s)
		{
			baseStorage = storage = s;
		}

		@Override
		public BaseTier getBaseTier()
		{
			return BaseTier.values()[ordinal()];
		}

		@Override
		public void loadConfig()
		{
			storage = Mekanism.configuration.get("tier", getBaseTier().getName() + "BinStorage", baseStorage).getInt();
		}

		@Override
		public void readConfig(ByteBuf dataStream)
		{
			storage = dataStream.readInt();
		}

		@Override
		public void writeConfig(ByteBuf dataStream)
		{
			dataStream.writeInt(storage);
		}
	}

	public static void init()
	{
		if(initiated)
		{
			return;
		}
		for(Class c : Tier.class.getDeclaredClasses())
		{
			if(c.isEnum())
			{
				try {
					for(Object obj : c.getEnumConstants())
					{
						if(obj instanceof ITier)
						{
							tierTypes.add((ITier)obj);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		initiated = true;
	}

	public static void loadConfig()
	{
		for(ITier tier : tierTypes)
		{
			tier.loadConfig();
		}
	}

	public static void readConfig(ByteBuf dataStream)
	{
		for(ITier tier : tierTypes)
		{
			tier.readConfig(dataStream);
		}
	}

	public static void writeConfig(ByteBuf dataStream)
	{
		for(ITier tier : tierTypes)
		{
			tier.writeConfig(dataStream);
		}
	}

	public static interface ITier
	{
		public BaseTier getBaseTier();

		public void loadConfig();

		public void readConfig(ByteBuf dataStream);

		public void writeConfig(ByteBuf dataStream);
	}
}
