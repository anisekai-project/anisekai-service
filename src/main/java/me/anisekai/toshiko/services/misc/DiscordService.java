package me.anisekai.toshiko.services.misc;

import fr.alexpado.jda.interactions.InteractionExtension;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.impl.interactions.autocomplete.AutocompleteInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.button.ButtonInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.slash.SlashInteractionTargetImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionPreprocessor;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import me.anisekai.toshiko.annotations.InteractAt;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.enums.InteractionType;
import me.anisekai.toshiko.helpers.InteractionBean;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DiscordService {

    private final DiscordCompletionService completionService;
    private final DiscordInjectionService  injectionService;
    private final ListableBeanFactory      beanFactory;
    private final InteractionExtension     extension;

    public DiscordService(DiscordCompletionService completionService, DiscordInjectionService injectionService, ListableBeanFactory beanFactory) {

        this.completionService = completionService;
        this.injectionService  = injectionService;
        this.beanFactory       = beanFactory;
        this.extension         = new InteractionExtension();

        this.extension.getSlashContainer().addClassMapping(DiscordUser.class, this.injectionService.entityUserMapper());
        this.extension.getButtonContainer()
                      .addClassMapping(DiscordUser.class, this.injectionService.entityUserMapper());
    }

    private void hook(JDA jda, Supplier<CommandListUpdateAction> action) {

        this.beanFactory.getBeansWithAnnotation(InteractionBean.class)
                        .values()
                        .forEach(this::register);

        jda.addEventListener(this.extension);
        this.extension.useDefaultMapping();
        this.extension.getSlashContainer().upsertCommands(action.get()).complete();
    }

    public void hook(JDA jda) {

        this.hook(jda, jda::updateCommands);
    }

    public void hook(Guild guild) {

        this.hook(guild.getJDA(), guild::updateCommands);

        this.extension.registerPreprocessor(new InteractionPreprocessor() {

            @Override
            public <T extends Interaction> boolean mayContinue(@NotNull DispatchEvent<T> dispatchEvent) {

                Guild g = dispatchEvent.getInteraction().getGuild();
                return g == null || guild.getIdLong() == g.getIdLong();
            }

            @Override
            public <T extends Interaction> Optional<Object> preprocess(@NotNull DispatchEvent<T> dispatchEvent) {

                return Optional.empty();
            }
        });
    }

    public void register(Object obj) {

        for (Method method : obj.getClass().getMethods()) {
            if (method.isAnnotationPresent(Interact.class)) {
                Interact annotation = method.getAnnotation(Interact.class);

                List<InteractionType> types = new ArrayList<>();

                if (method.isAnnotationPresent(InteractAt.class)) {
                    InteractAt at = method.getAnnotation(InteractAt.class);
                    types.addAll(Arrays.asList(at.value()));
                } else {
                    types.addAll(Arrays.asList(InteractionType.values()));
                }

                List<OptionMeta> options = Arrays.stream(annotation.options()).map(OptionMeta::new).toList();

                if (types.contains(InteractionType.SLASH)) {
                    InteractionMeta slashMeta = new InteractionMeta(
                            annotation.name(),
                            annotation.description(),
                            annotation.target(),
                            options,
                            annotation.hideAsSlash(),
                            annotation.defer(),
                            annotation.shouldReply()
                    );

                    SlashInteractionTarget slash = new SlashInteractionTargetImpl(obj, method, slashMeta);
                    this.extension.getSlashContainer().register(slash);

                    AutocompleteInteractionTarget completion = new AutocompleteInteractionTargetImpl(slashMeta);

                    completion.addCompletionProvider("anime", this.completionService::animeCompletion);
                    completion.addCompletionProvider("seasonal", this.completionService::seasonalSelectionCompletion);
                    completion.addCompletionProvider("interest", this.completionService::interestLevelCompletion);
                    completion.addCompletionProvider("status", this.completionService::animeStatusCompletion);

                    this.extension.getAutocompleteContainer().register(completion);
                }

                if (types.contains(InteractionType.BUTTON)) {
                    InteractionMeta buttonMeta = new InteractionMeta(
                            annotation.name(),
                            annotation.description(),
                            annotation.target(),
                            options,
                            annotation.hideAsButton(),
                            annotation.defer(),
                            annotation.shouldReply()
                    );
                    ButtonInteractionTarget button = new ButtonInteractionTargetImpl(obj, method, buttonMeta);
                    this.extension.getButtonContainer().register(button);
                }
            }
        }
    }
}