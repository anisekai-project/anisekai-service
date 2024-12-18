package me.anisekai.modules.linn.repositories;

import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.enums.AnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {

    Optional<Anime> findByName(String name);

    List<Anime> findAllByStatusIn(Collection<AnimeStatus> status);

    List<Anime> findAllByStatus(AnimeStatus status);

    @Query("SELECT a FROM Anime a WHERE a.rssMatch IS NOT NULL AND a.diskPath IS NOT NULL")
    List<Anime> findAllAutoDownloadReady();

}
