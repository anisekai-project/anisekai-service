package me.anisekai.api.plannifier.data;

import java.time.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class TestData {

    public static final long EPISODE_DURATION_MINUTES = 24;

    public static final ZonedDateTime BASE_DATETIME = ZonedDateTime.of(
            LocalDate.of(2024, 1, 1),
            LocalTime.of(0, 0),
            ZoneId.systemDefault()
    );

    // <editor-fold desc="Factory">
    private final AtomicLong    autoTargetId = new AtomicLong(1);
    private final AtomicLong    autoPartyId  = new AtomicLong(1);
    private       ZonedDateTime baseTime     = BASE_DATETIME;

    private TestWatchTarget createTarget() {
        return new TestWatchTarget(this.autoTargetId.getAndIncrement(), 0, 12);
    }

    private TestWatchParty createParty(TestWatchTarget target, long firstEpisode, Function<ZonedDateTime, ZonedDateTime> timeSlider) {
        TestWatchParty party = new TestWatchParty(this.autoPartyId.getAndIncrement(), target, firstEpisode, this.baseTime, 2, true);
        this.baseTime = timeSlider.apply(this.baseTime);
        return party;
    }
    // </editor-fold>

    public final TestWatchTarget target1 = this.createTarget();
    public final TestWatchTarget target2 = this.createTarget();

    public final TestWatchParty partyA1 = this.createParty(this.target1, 1, time -> time.plusYears(1));
    public final TestWatchParty partyB1 = this.createParty(this.target1, 3, time -> time.plusMinutes(75));
    public final TestWatchParty partyB2 = this.createParty(this.target1, 5, time -> time.plusMinutes(50));

    public List<TestWatchParty> dataBank() {

        return List.of(this.partyA1, this.partyB1, this.partyB2);
    }

}