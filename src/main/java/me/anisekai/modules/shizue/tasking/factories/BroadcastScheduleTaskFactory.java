package me.anisekai.modules.shizue.tasking.factories;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.globals.tasking.Task;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.interfaces.TaskExecutor;
import me.anisekai.globals.tasking.interfaces.TaskFactory;
import me.anisekai.modules.shizue.tasking.executors.BroadcastScheduleTaskExector;
import me.anisekai.modules.shizue.tasking.BroadcastTaskExecutor;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.modules.toshiko.JdaStore;
import org.springframework.stereotype.Component;

@Component
public class BroadcastScheduleTaskFactory implements TaskFactory<BroadcastScheduleTaskExector> {

    public static final String               NAME = "broadcast:schedule";
    private final       TaskingService       service;
    private final       BroadcastDataService broadcastService;
    private final       JdaStore             store;

    public BroadcastScheduleTaskFactory(TaskingService service, BroadcastDataService broadcastService, JdaStore store) {

        this.service          = service;
        this.broadcastService = broadcastService;
        this.store            = store;
    }

    public static Task queue(TaskingService service, IBroadcast broadcast) {

        String        taskName  = asTaskName(broadcast);
        BookshelfJson arguments = new BookshelfJson();
        arguments.put(BroadcastTaskExecutor.OPT_BROADCAST, broadcast.getId());

        return service.queue(NAME, taskName, arguments);
    }

    public static String asTaskName(IBroadcast broadcast) {

        return String.format("%s:%s", NAME, broadcast.getId());
    }

    @Override
    public Class<BroadcastScheduleTaskExector> getTaskClass() {

        return BroadcastScheduleTaskExector.class;
    }

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    @Override
    public String getName() {

        return NAME;
    }

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    @Override
    public BroadcastScheduleTaskExector create() {

        return new BroadcastScheduleTaskExector(this.broadcastService, this.store);
    }

    @PostConstruct
    private void postConstruct() {

        this.service.registerFactory(this);
    }

}