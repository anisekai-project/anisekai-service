package me.anisekai.toshiko.modules.discord;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Texts {

    // <editor-fold desc="@ anime/announce">
    public static final String ANIME_NOTIFY_ANNOUNCE__DESCRIPTION  = "\uD83D\uDD12 Pour un ou plusieurs animes, envoi ou met à jour le message d'annonce.";
    public static final String ANIME_NOTIFY_ANNOUNCE__OPTION_ANIME = "Nom de l'anime";
    // </editor-fold>

    // <editor-fold desc="@ anime/about">
    public static final String ANIME_ABOUT__DESCRIPTION  = "Affiche la fiche de détails d'un anime.";
    public static final String ANIME_ABOUT__OPTION_ANIME = "Nom de l'anime";
    // </editor-fold>

    // <editor-fold desc="@ anime/status">
    public static final String ANIME_STATUS__DESCRIPTION   = "\uD83D\uDD12 Change le statut d'un anime.";
    public static final String ANIME_STATUS__OPTION_ANIME  = "Nom de l'anime";
    public static final String ANIME_STATUS__OPTION_STATUS = "Nouveau statut de l'anime";
    // </editor-fold>

    // <editor-fold desc="@ anime/interest">
    public static final String ANIME_INTEREST__DESCRIPTION                         = "\uD83E\uDE99 Change ton niveau d'intérêt pour un anime";
    public static final String ANIME_INTEREST__OPTION_NAME                         = "Nom de l'anime";
    public static final String ANIME_INTEREST__OPTION_LEVEL                        = "Niveau de l'intérêt";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED     = "Je suis intéressé";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL        = "Je suis suisse ! NEUTRE !";
    public static final String ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED = "J'ai vraiment pas envie de voir ça";
    // </editor-fold>

    // <editor-fold desc="@ anime/progress">
    public static final String ANIME_PROGRESS__DESCRIPTION    = "\uD83D\uDD12 Change la progression de visionnage d'un anime";
    public static final String ANIME_PROGRESS__OPTION_ANIME   = "Nom de l'anime";
    public static final String ANIME_PROGRESS__OPTION_WATCHED = "Nombre d'épisodes visionnés";
    public static final String ANIME_PROGRESS__OPTION_AMOUNT  = "Nombre total d'épisodes";
    // </editor-fold>

    // <editor-fold desc="@ anime/import">
    public static final String ANIME_IMPORT__DESCRIPTION = "\uD83E\uDE99 Importe un anime depuis une chaine JSON.";
    public static final String ANIME_IMPORT__OPTION_JSON = "JSON à importer";
    // </editor-fold>

    // <editor-fold desc="@ top/anime">
    public static final String TOP_ANIME__DESCRIPTION               = "Affiche un classement des animes basé sur les votes.";
    public static final String TOP_ANIME__OPTION_ORDER              = "Ordre de tri";
    public static final String TOP_ANIME__OPTION_ORDER__CHOICE_ASC  = "Top des moins votés";
    public static final String TOP_ANIME__OPTION_ORDER__CHOICE_DESC = "Top des plus votés";
    public static final String TOP_ANIME__OPTION_LIMIT              = "Nombre d'éléments à afficher dans le classement.";
    // </editor-fold>

    // <editor-fold desc="@ top/user">
    public static final String TOP_USER__DESCRIPTION = "Affiche un classement des utilisateurs par puissance de vote.";
    // </editor-fold>

    // <editor-fold desc="@ schedule/daily">
    public static final String SCHEDULE_DAILY__DESCRIPTION     = "\uD83D\uDD12 Programme des soirées anime de façon journalière";
    public static final String SCHEDULE_DAILY__OPTION_ANIME    = "Anime à programmer";
    public static final String SCHEDULE_DAILY__OPTION_AT       = "Heure à laquelle les soirées anime doivent être programmées tous les jours (HH:MM)";
    public static final String SCHEDULE_DAILY__OPTION_AMOUNT   = "Nombre d'épisode à regarder à chaque séance";
    public static final String SCHEDULE_DAILY__OPTION_STARTING = "A partir de quel jour la programmation devrait commencer (JJ/MM/AAAA)";
    // </editor-fold>

    // <editor-fold desc="@ schedule/weekly">
    public static final String SCHEDULE_WEEKLY__DESCRIPTION     = "\uD83D\uDD12 Programme des soirées anime de façon hebdomadaire";
    public static final String SCHEDULE_WEEKLY__OPTION_ANIME    = "Anime à programmer";
    public static final String SCHEDULE_WEEKLY__OPTION_AT       = "Heure à laquelle les soirées anime doivent être programmées toutes les semaines (HH:MM)";
    public static final String SCHEDULE_WEEKLY__OPTION_AMOUNT   = "Nombre d'épisode à regarder à chaque séance";
    public static final String SCHEDULE_WEEKLY__OPTION_STARTING = "A partir de quel jour la programmation devrait commencer (JJ/MM/AAAA)";
    // </editor-fold>

    // <editor-fold desc="@ schedule/anime">
    public static final String SCHEDULE_ANIME__DESCRIPTION   = "\uD83D\uDD12 Programme une soirée anime";
    public static final String SCHEDULE_ANIME__OPTION_ANIME  = "Anime à programmer";
    public static final String SCHEDULE_ANIME__OPTION_AT     = "Heure à laquelle la soirée anime doit être programmée (HH:MM)";
    public static final String SCHEDULE_ANIME__OPTION_AMOUNT = "Nombre d'épisode à regarder pour la séance";
    public static final String SCHEDULE_ANIME__OPTION_DATE   = "Pour quel jour la programmation devrait être faite (JJ/MM/AAAA)";
    // </editor-fold>

    // <editor-fold desc="@ schedule/calibrate">
    public static final String SCHEDULE_CALIBRATE__DESCRIPTION = "\uD83D\uDD12 Recalibre tous les évènements programmés";
    // </editor-fold>

    // <editor-fold desc="@ schedule/delay">
    public static final String SCHEDULE_DELAY__DESCRIPTION  = "\uD83D\uDD12 Décale les évènements des 6 prochaines heures";
    public static final String SCHEDULE_DELAY__OPTION_DELAY = "Temps (en minute) de décalage à appliquer aux évènements";
    // </editor-fold>

    // </editor-fold>

    // <editor-fold desc="@ refresh">
    public static final String REFRESH__DESCRIPTION                     = "\uD83D\uDD12 Lance une actualisation sur une partie spécifique.";
    public static final String REFRESH__OPTION_TARGET                   = "Cible de l'actualisation";
    public static final String REFRESH__OPTION_TARGET__CHOICE_WATCHLIST = "Listes de visionnage";
    public static final String REFRESH__OPTION_TARGET__CHOICE_ANNOUNCE  = "Message d'annonces (déjà envoyés uniquement)";
    public static final String REFRESH__OPTION_TARGET__CHOICE_SCHEDULE  = "Évènements du serveur";
    public static final String REFRESH__OPTION_FORCE                    = "(Listes de visionnage uniquement) Force l'actualisation des messages, même s'ils n'existent pas.";
    // </editor-fold>

    // <editor-fold desc="@ profile">
    public static final String PROFILE_DESCRIPTION    = "Mettre à jour les données d'un profil utilisateur";
    public static final String PROFILE__OPTION_USER   = "\uD83D\uDD12 Utilisateur pour lequel le profil sera actualisé.";
    public static final String PROFILE__OPTION_ICON   = "Change l'icône de vote";
    public static final String PROFILE__OPTION_ACTIVE = "\uD83D\uDD12 Change le statut d'activité";
    public static final String PROFILE__OPTION_ADMIN  = "\uD83D\uDD12 Change le statut administrateur";
    public static final String PROFILE__OPTION_WEB    = "\uD83D\uDD12 Change le droit d'accès web";
    // </editor-fold>

    private Texts() {}

    public static String unslugify(String str) {

        List<String> romanNumbers = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");

        String[]           split = str.split("-");
        Collection<String> parts = new ArrayList<>();

        for (String s : split) {

            if (romanNumbers.contains(s.toUpperCase())) {
                parts.add(s.toUpperCase());
            } else {
                parts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
            }
        }

        return Strings.join(parts, ' ');
    }

    public static String truncate(String str, int maxLength) {

        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

}