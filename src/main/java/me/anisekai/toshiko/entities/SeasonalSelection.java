package me.anisekai.toshiko.entities;

import jakarta.persistence.*;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Set;

@Entity
public class SeasonalSelection {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Anime> animes;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SeasonalVoter> voters;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SeasonalVote> votes;

    private boolean closed;

    public SeasonalSelection() {

    }

    public Long getId() {

        return this.id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Set<Anime> getAnimes() {

        return this.animes;
    }

    public void setAnimes(Set<Anime> animes) {

        this.animes = animes;
    }

    public Set<SeasonalVoter> getVoters() {

        return this.voters;
    }

    public void setVoters(Set<SeasonalVoter> voters) {

        this.voters = voters;
    }

    public Set<SeasonalVote> getVotes() {

        return this.votes;
    }

    public void setVotes(Set<SeasonalVote> votes) {

        this.votes = votes;
    }

    public boolean isClosed() {

        return this.closed;
    }

    public void setClosed(boolean closed) {

        this.closed = closed;
    }

    public Command.Choice asChoice() {

        if (this.getName().length() > 100) {
            return new Command.Choice(String.format("%s...", this.getName().substring(0, 90)), this.getId());
        }
        return new Command.Choice(this.getName(), this.getId());
    }
}