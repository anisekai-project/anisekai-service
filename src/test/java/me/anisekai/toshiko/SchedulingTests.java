package me.anisekai.toshiko;

import me.anisekai.toshiko.data.BookedAnimeNight;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.helpers.AnimeNightScheduler;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import me.anisekai.toshiko.utils.FakeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@DisplayName("Scheduling")
@Tag("slow-test")
public class SchedulingTests {

    @Test
    @DisplayName("Scheduling: Ensure Today")
    public void testScheduleNow() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime  now        = ZonedDateTime.now();
        ZonedDateTime  scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);
        OffsetDateTime expected   = scheduleAt.toOffsetDateTime();

        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isPresent());
        BookedAnimeNight night  = bookedAnimeNight.get();
        OffsetDateTime   actual = night.getStartDateTime();

        Assertions.assertEquals(expected.getHour(), actual.getHour(), "Scheduled hour doesn't match");
        Assertions.assertEquals(expected.getMinute(), actual.getMinute(), "Scheduled minute doesn't match");
        Assertions.assertEquals(expected.getDayOfYear(), actual.getDayOfYear(), "Scheduled day doesn't match");
    }

    @Test
    @DisplayName("Scheduling: Ensure Tomorrow")
    public void testScheduleTomorrow() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime  now        = ZonedDateTime.now();
        ZonedDateTime  scheduleAt = now.withHour(now.getHour() + 1).withMinute(0).plusDays(1);
        OffsetDateTime expected   = scheduleAt.toOffsetDateTime();

        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isPresent());
        BookedAnimeNight night  = bookedAnimeNight.get();
        OffsetDateTime   actual = night.getStartDateTime();

        Assertions.assertEquals(expected.getHour(), actual.getHour(), "Scheduled hour doesn't match");
        Assertions.assertEquals(expected.getMinute(), actual.getMinute(), "Scheduled minute doesn't match");
        Assertions.assertEquals(expected.getDayOfYear(), actual.getDayOfYear(), "Scheduled day doesn't match");
    }

    @Test
    @DisplayName("Follow: Ensure right episode count (from 0)")
    public void testFollowNow() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isPresent());
        BookedAnimeNight night = bookedAnimeNight.get();

        Assertions.assertEquals(3, night.getAmount(), "Scheduled amount doesn't match");
        Assertions.assertEquals(1, night.getFirstEpisode(), "Scheduled first episode doesn't match");
        Assertions.assertEquals(3, night.getLastEpisode(), "Scheduled last episode doesn't match");
    }

    @Test
    @DisplayName("Follow: Ensure right episode count (from x)")
    public void testFollowNextNow() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);
        fakeAnimeOne.setWatched(3);

        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isPresent());
        BookedAnimeNight night = bookedAnimeNight.get();

        Assertions.assertEquals(3, night.getAmount(), "Scheduled amount doesn't match");
        Assertions.assertEquals(4, night.getFirstEpisode(), "Scheduled first episode doesn't match");
        Assertions.assertEquals(6, night.getLastEpisode(), "Scheduled last episode doesn't match");
    }

    @Test
    @DisplayName("Follow: Ensure right episode count (from previous)")
    public void testFollowPreviousNow() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);
        scheduleAt = scheduleAt.plusDays(1);
        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isPresent());
        BookedAnimeNight night = bookedAnimeNight.get();

        Assertions.assertEquals(3, night.getAmount(), "Scheduled amount doesn't match");
        Assertions.assertEquals(4, night.getFirstEpisode(), "Scheduled first episode doesn't match");
        Assertions.assertEquals(6, night.getLastEpisode(), "Scheduled last episode doesn't match");
    }

    @Test
    @DisplayName("Scheduling: Ensure scheduling refusal when overlap")
    public void testScheduleOverlap() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        // Get very close time to ensure not switching hour/day.
        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);
        Optional<BookedAnimeNight> bookedAnimeNight = scheduler.scheduleAt(fakeAnimeOne, 3, scheduleAt, booking -> booking);

        Assertions.assertTrue(bookedAnimeNight.isEmpty());
    }

    @Test
    @DisplayName("Scheduling: Ensure daily scheduling")
    public void testDailyScheduling() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Set<BookedAnimeNight> events = new HashSet<>();
        scheduler.scheduleAllStartingAt(fakeAnimeOne, 3, scheduleAt, booking -> {
            events.add(booking);
            return booking;
        }, date -> date.plusDays(1));

        Assertions.assertEquals(4, events.size(), "Wrong amount of scheduled event");

        // Check for event date & episodes
        List<BookedAnimeNight> sortedList = events.stream()
                                                  .sorted(Comparator.comparing(BookedAnimeNight::getStartDateTime))
                                                  .toList();

        int              loop                 = 1;
        int              expectedAmount       = 3;
        int              expectedFirstEpisode = 1;
        int              expectedLastEpisode  = 3;
        BookedAnimeNight latest               = null;

        for (BookedAnimeNight night : sortedList) {

            Assertions.assertEquals(expectedAmount, night.getAmount(), "Wrong amount in event " + loop);
            Assertions.assertEquals(expectedFirstEpisode, night.getFirstEpisode(), "Wrong first first ep in event " + loop);
            Assertions.assertEquals(expectedLastEpisode, night.getLastEpisode(), "Wrong first last ep in event " + loop);

            expectedFirstEpisode += expectedAmount;
            expectedLastEpisode += expectedAmount;

            if (latest != null) {
                Assertions.assertEquals(latest.getStartDateTime()
                                              .plusDays(1), night.getStartDateTime(), "Wrong follow up on starting date");
                Assertions.assertEquals(latest.getEndDateTime()
                                              .plusDays(1), night.getEndDateTime(), "Wrong follow up on ending date");
            }

            latest = night;
        }
    }

    @Test
    @DisplayName("Scheduling: Ensure weekly scheduling")
    public void testWeeklyScheduling() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Set<BookedAnimeNight> events = new HashSet<>();
        scheduler.scheduleAllStartingAt(fakeAnimeOne, 1, scheduleAt, booking -> {
            events.add(booking);
            return booking;
        }, date -> date.plusDays(7));

        Assertions.assertEquals(fakeAnimeOne.getTotal(), events.size(), "Wrong amount of scheduled event");

        // Check for event date & episodes
        List<BookedAnimeNight> sortedList = events.stream()
                                                  .sorted(Comparator.comparing(BookedAnimeNight::getStartDateTime))
                                                  .toList();

        int              loop                 = 1;
        int              expectedAmount       = 1;
        int              expectedFirstEpisode = 1;
        int              expectedLastEpisode  = 1;
        BookedAnimeNight latest               = null;

        for (BookedAnimeNight night : sortedList) {

            Assertions.assertEquals(expectedAmount, night.getAmount(), "Wrong amount in event " + loop);
            Assertions.assertEquals(expectedFirstEpisode, night.getFirstEpisode(), "Wrong first first ep in event " + loop);
            Assertions.assertEquals(expectedLastEpisode, night.getLastEpisode(), "Wrong first last ep in event " + loop);

            expectedFirstEpisode += expectedAmount;
            expectedLastEpisode += expectedAmount;

            if (latest != null) {
                Assertions.assertEquals(latest.getStartDateTime()
                                              .plusDays(7), night.getStartDateTime(), "Wrong follow up on starting date");
                Assertions.assertEquals(latest.getEndDateTime()
                                              .plusDays(7), night.getEndDateTime(), "Wrong follow up on ending date");
            }

            latest = night;
        }
    }

    @Test
    @DisplayName("Scheduling: Ensure scheduling calibration")
    public void testScheduleCalibration() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime now        = ZonedDateTime.now();
        ZonedDateTime scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Set<BookedAnimeNight> events = new HashSet<>();
        scheduler.scheduleAllStartingAt(fakeAnimeOne, 1, scheduleAt, booking -> {
            events.add(booking);
            return booking;
        }, date -> date.plusDays(7));

        // Remove first event
        events.removeIf(night -> night.getFirstEpisode() == 1);

        Assertions.assertEquals(fakeAnimeOne.getTotal() - 1, events.size(), "Wrong amount of scheduled event");

        // Check for event date & episodes
        scheduler = new AnimeNightScheduler<>(events);
        events.clear();
        scheduler.calibrate(fakeAnimeOne, events::add);

        List<BookedAnimeNight> sortedList = events.stream()
                                                  .sorted(Comparator.comparing(BookedAnimeNight::getStartDateTime))
                                                  .toList();

        int              loop                 = 1;
        int              expectedAmount       = 1;
        int              expectedFirstEpisode = 1;
        int              expectedLastEpisode  = 1;
        BookedAnimeNight latest               = null;

        for (BookedAnimeNight night : sortedList) {

            Assertions.assertEquals(expectedAmount, night.getAmount(), "Wrong amount in event " + loop);
            Assertions.assertEquals(expectedFirstEpisode, night.getFirstEpisode(), "Wrong first first ep in event " + loop);
            Assertions.assertEquals(expectedLastEpisode, night.getLastEpisode(), "Wrong first last ep in event " + loop);

            expectedFirstEpisode += expectedAmount;
            expectedLastEpisode += expectedAmount;

            if (latest != null) {
                Assertions.assertEquals(latest.getStartDateTime()
                                              .plusDays(7), night.getStartDateTime(), "Wrong follow up on starting date");
                Assertions.assertEquals(latest.getEndDateTime()
                                              .plusDays(7), night.getEndDateTime(), "Wrong follow up on ending date");
            }

            latest = night;
        }
    }

    @Test
    @DisplayName("Scheduling: Ensure scheduling delay")
    public void testScheduleDelay() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime  now        = ZonedDateTime.now();
        OffsetDateTime delayLimit = now.plusHours(5).toOffsetDateTime();
        ZonedDateTime  scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Set<BookedAnimeNight> events = new HashSet<>();
        scheduler.scheduleAllStartingAt(fakeAnimeOne, 3, scheduleAt, booking -> {
            events.add(booking);
            return booking;
        }, date -> date.plusDays(1));

        Assertions.assertEquals(4, events.size(), "Wrong amount of scheduled event");

        Set<BookedAnimeNight> delayed     = new HashSet<>();
        boolean               delayResult = scheduler.delay(10, TimeUnit.MINUTES, item -> item.isBefore(delayLimit), delayed::add);

        Assertions.assertTrue(delayResult, "The delaying was not successful.");
        Assertions.assertTrue(delayed.stream().allMatch(item -> item.getStartDateTime().getMinute() == 10));
    }

    @Test
    @DisplayName("Scheduling: Ensure scheduling delay (negative)")
    public void testScheduleNegative() {

        Anime                                 fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        AnimeNightScheduler<BookedAnimeNight> scheduler    = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime  now        = ZonedDateTime.now();
        OffsetDateTime delayLimit = now.plusHours(5).toOffsetDateTime();
        ZonedDateTime  scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);

        Set<BookedAnimeNight> events = new HashSet<>();
        scheduler.scheduleAllStartingAt(fakeAnimeOne, 3, scheduleAt, booking -> {
            events.add(booking);
            return booking;
        }, date -> date.plusDays(1));

        Assertions.assertEquals(4, events.size(), "Wrong amount of scheduled event");

        Set<BookedAnimeNight> delayed     = new HashSet<>();
        boolean               delayResult = scheduler.delay(-10, TimeUnit.MINUTES, item -> item.isBefore(delayLimit), delayed::add);

        Assertions.assertTrue(delayResult, "The delaying was not successful.");
        // TODO: Check the date, but with day change and all meh
        Assertions.assertTrue(delayed.stream().allMatch(item -> item.getStartDateTime().getMinute() == 50));
    }

    @Test
    @DisplayName("Scheduling: Ensure scheduling delay not overlapping")
    public void testScheduleNoDelay() {

        Anime fakeAnimeOne = FakeService.FAKE_ANIME_ONE.get();
        Anime fakeAnimeTwo = FakeService.FAKE_ANIME_TWO.get();

        AnimeNightScheduler<BookedAnimeNight> scheduler = new AnimeNightScheduler<>(Collections.emptyList());

        ZonedDateTime  now        = ZonedDateTime.now();
        ZonedDateTime  scheduleAt = now.withHour(now.getHour() + 1).withMinute(0);
        OffsetDateTime delayLimit = scheduleAt.plusMinutes(5).toOffsetDateTime();

        Set<AnimeNightMeta> events = new HashSet<>();

        scheduler.scheduleAt(fakeAnimeOne, 1, scheduleAt, item -> {
            events.add(item);
            return item;
        });

        scheduler.scheduleAt(fakeAnimeTwo, 1, scheduleAt.plusMinutes(30), item -> {
            events.add(item);
            return item;
        });

        Assertions.assertEquals(2, events.size(), "Wrong amount of scheduled event");

        Set<BookedAnimeNight> delayed = new HashSet<>();
        boolean delayResult = scheduler.delay(10, TimeUnit.MINUTES, item -> item.isBefore(delayLimit), delayed::add);

        Assertions.assertFalse(delayResult, "The delay was executed");
        Assertions.assertEquals(0, delayed.size(), "The event has been delayed.");
    }

}