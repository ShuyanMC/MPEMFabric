package net.shuyanmc.mpem;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraftforge.fml.config.ModConfig;
import net.shuyanmc.mpem.client.ItemCountRenderer;
import net.shuyanmc.mpem.config.CoolConfig;
import net.shuyanmc.mpem.events.EntitySyncHandler;
import net.shuyanmc.mpem.optimization.ChunkManager;
import net.shuyanmc.mpem.optimization.EntityActivator;
import net.shuyanmc.mpem.optimization.EntityOptimizer;
import net.shuyanmc.mpem.optimization.MemoryCleaner;
import net.shuyanmc.mpem.util.EntityCleaner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

//@Mod(value = "mpem")
public class MpemMod {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "mpem";
	public static final String VERSION = "2.1.3";
	private static final AtomicBoolean isInitialized = new AtomicBoolean(false);

	private final MemoryCleaner memoryCleaner = new MemoryCleaner();

	private final EntityOptimizer optimizer = new EntityOptimizer();
	private final EntityCleaner cleaner = new EntityCleaner();
	private final EntityActivator activator = new EntityActivator();

	private final EntitySyncHandler entitySyncHandler = new EntitySyncHandler();

	private ChunkManager chunkManager = new ChunkManager();

	private static final String PROTOCOL_VERSION = "1";
	/*public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
			new Identifier(MODID, MODID),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);*/

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
	public MpemMod() {
		LOGGER.info("MPEM Mod is Loading...");
		//IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		//IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		ForgeConfigRegistryImpl.INSTANCE.register("mpem",ModConfig.Type.COMMON, CoolConfig.SPEC);
		//MinecraftForge.EVENT_BUS.register(this);
		//modEventBus.addListener(this::setup);


		//DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			//MinecraftForge.EVENT_BUS.register(ItemCountRenderer.class);
		//});

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            if(minecraftServer.getOverworld()!=null){
                memoryCleaner.onServerTick();
				cleaner.onServerTick(minecraftServer);
            }
			activator.onServerTick(minecraftServer);
			chunkManager.onServerTick(minecraftServer);
        });

		ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
			optimizer.onEntityJoin(entity);
			entitySyncHandler.onEntityJoinLevel(entity);
		});

		ServerEntityEvents.ENTITY_UNLOAD.register((entity, serverWorld) -> optimizer.onEntityLeave(entity));

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> cleaner.onCommandRegister(commandDispatcher));

		//MixinBootstrap.init();
		//ModEventHandlers.register(modEventBus, forgeEventBus);
		//modEventBus.addListener(AsyncSystemInitializer::init);
		/*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			AsyncEventSystem.shutdown();
			AsyncParticleHandler.shutdown();
		}));*/

		LOGGER.info("Initializing MPEM MOD v{}", VERSION);
		LOGGER.info("MPEM Mod's other features loading...");
		System.gc();
		LOGGER.info("GC Firstly!");
		LOGGER.info("Please make sure there is an English half-width symbol 'before the mod name! Only in this way will the mod be the first to load! There will be better optimization!");
		LOGGER.info("Notice: Anti-cheating defaults to disabling class name detection. Server administrators who need it can enable it. After the 2.0.6 update, this mod basically does not send false alarms with common mods. You can use it with confidence. If there are still some mods with false alarms, you can provide feedback in the SYMC player exchange group!");
		LOGGER.info("SYMC player exchange group in QQ：372378451");

		/*ModLoadingContext.get().registerExtensionPoint(
				IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true)
		);*/
	}




	public static int getMaxWorkerThreads() {
		String propValue = System.getProperty("max.worker.threads");
		try {
			if (propValue != null) {
				int value = Integer.parseInt(propValue);
				return Math.max(1, Math.min(value, 32767));
			}
		} catch (NumberFormatException ignored) {}
		return Math.min(32767, Runtime.getRuntime().availableProcessors() * 2);
	}
	/*private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("MPEM Mod ?????");
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

		event.enqueueWork(() -> {
			AsyncEventSystem.initialize();
			ModEventProcessor.processModEvents();

		});
	}*/
	/*@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		LOGGER.info("????????");

	}
	public static void registerDynamicListener(
			Class<? extends Event> eventType,
			IEventListener listener,
			EventPriority priority,
			boolean receiveCancelled
	) {
		MinecraftForge.EVENT_BUS.addListener(
				priority,
				receiveCancelled,
				eventType,
				event -> {
					try {
						listener.invoke(event);
					} catch (ClassCastException e) {
						LOGGER.error("Event type mismatch for listener", e);
					} catch (Throwable t) {
						LOGGER.error("Error in optimized event handler", t);
						if (CoolConfig.DISABLE_ASYNC_ON_ERROR.get() && eventType.getSimpleName().contains("Async")) {
							LOGGER.warn("Disabling async for event type due to handler error: {}", eventType.getName());
							AsyncEventSystem.registerSyncEvent(eventType);
						}
					}
				}
		);
	}
	public static void executeSafeAsync(Runnable task, String taskName) {
		AsyncEventSystem.executeAsync(
				net.minecraftforge.event.TickEvent.ServerTickEvent.class,
				() -> {
					try {
						long start = System.currentTimeMillis();
						task.run();
						long duration = System.currentTimeMillis() - start;
						if (duration > 100) {
							LOGGER.debug("Async task '{}' completed in {}ms", taskName, duration);
						}
					} catch (Throwable t) {
						LOGGER.error("Async task '{}' failed", taskName, t);
						throw t;
					}
				}
		);
	}*/
}