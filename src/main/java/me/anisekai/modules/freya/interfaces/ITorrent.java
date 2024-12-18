package me.anisekai.modules.freya.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.freya.enums.TorrentStatus;
import me.anisekai.modules.freya.events.torrent.TorrentPercentDoneUpdatedEvent;
import me.anisekai.modules.freya.events.torrent.TorrentStatusUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;

public interface ITorrent extends IEntity<Integer> {

    Anime getAnime();

    void setAnime(Anime anime);

    String getLink();

    void setLink(String link);

    String getName();

    void setName(String name);

    TorrentStatus getStatus();

    @TriggerEvent(TorrentStatusUpdatedEvent.class)
    void setStatus(TorrentStatus status);

    String getDownloadDir();

    void setDownloadDir(String downloadDir);

    double getPercentDone();

    @TriggerEvent(TorrentPercentDoneUpdatedEvent.class)
    void setPercentDone(double percentDone);

    String getInfoHash();

    void setInfoHash(String infoHash);

    String getFile();

    void setFile(String file);

}
