package me.anisekai.toshiko.modules.library;

import me.anisekai.toshiko.events.FileImportedEvent;
import me.anisekai.toshiko.events.ImportStartedEvent;
import me.anisekai.toshiko.events.torrent.TorrentStatusUpdatedEvent;
import me.anisekai.toshiko.modules.library.entities.DiskFile;
import me.anisekai.toshiko.modules.library.utils.AnimeRenamer;
import me.anisekai.toshiko.modules.library.utils.FileSystemUtils;
import me.anisekai.toshiko.modules.library.video.SubtitleCodec;
import me.anisekai.toshiko.modules.library.video.VideoFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class ToshikoFileSystem {

    private final static List<String> SUPPORTED_EXTENSION = Arrays.asList("mkv", "mp4", "avi");

    private final static Logger LOGGER = LoggerFactory.getLogger(ToshikoFileSystem.class);

    private final ApplicationEventPublisher publisher;
    private final DiskService               diskService;
    private final FileManagerService        fileManagerService;
    private final Collection<DiskFile>      diskFileLocking = new HashSet<>();
    private final BlockingDeque<DiskFile>   automationQueue = new LinkedBlockingDeque<>();
    private       boolean                   hasFileWaiting  = false;

    public ToshikoFileSystem(ApplicationEventPublisher publisher, DiskService diskService, FileManagerService fileManagerService) {

        this.publisher          = publisher;
        this.diskService        = diskService;
        this.fileManagerService = fileManagerService;
    }

    private File getOutput(DiskFile diskFile, String destinationDirectory) {

        String relative  = this.getRelativeFsPath(diskFile.getFile());
        File   animeDir  = new File(destinationDirectory);
        File   output    = new File(animeDir, relative);
        File   container = output.getParentFile();
        //noinspection ResultOfMethodCallIgnored
        container.mkdirs();
        return output;
    }

    private void moveVideo(DiskFile mkv) throws IOException {

        File output = this.getOutput(mkv, this.diskService.getDiskConfiguration().getAnimesOutput());
        Files.move(mkv.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private void moveSubtitle(DiskFile subtitle) throws IOException {

        File output = this.getOutput(subtitle, this.diskService.getDiskConfiguration().getSubtitlesOutput());
        Files.move(subtitle.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private String getRelativeFsPath(File file) {

        return file.getAbsolutePath().replace(this.diskService.getAutomationPath().toString(), "");
    }

    public int getAmountInQueue() {

        return this.diskFileLocking.size();
    }

    public int checkForAutomation() {

        if (!this.diskService.isEnabled()) {
            return 0;
        }

        LOGGER.info("Checking automation directory...");

        List<DiskFile> supportedFiles = FileSystemUtils.find(this.diskService.getAutomationPath().toFile())
                                                       .stream()
                                                       .map(DiskFile::new)
                                                       .filter(diskFile -> SUPPORTED_EXTENSION.contains(diskFile.getExtension()))
                                                       .filter(diskFile -> !this.diskFileLocking.contains(diskFile))
                                                       .toList();

        List<DiskFile> manageableFiles = supportedFiles.stream().filter(DiskFile::isReady).toList();
        this.hasFileWaiting = supportedFiles.size() > manageableFiles.size();

        LOGGER.info("Queuing {} file(s) in automation directory.", supportedFiles.size());

        supportedFiles.forEach(diskFile -> {
            LOGGER.debug(" > Queuing '{}'...", this.getRelativeFsPath(diskFile.getFile()));
            this.diskFileLocking.add(diskFile);
            this.automationQueue.add(diskFile);
        });

        return supportedFiles.size();
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void handleNextFile() {

        if (!this.diskService.isEnabled()) {
            return;
        }

        DiskFile diskFile = this.automationQueue.poll();

        if (diskFile == null) {
            return;
        }

        this.publisher.publishEvent(new ImportStartedEvent(this, diskFile));

        try {
            LOGGER.info("Reading video data...");
            VideoFile videoData = this.fileManagerService.getVideoData(diskFile.getPath());
            LOGGER.info("Extracting subtitles...");
            List<DiskFile> subtitles = this.fileManagerService.extractSubtitles(videoData.getTracks());
            LOGGER.info("Converting subtitles");
            List<DiskFile> webSubtitles = this.fileManagerService.convertSubtitles(subtitles, SubtitleCodec.VTT);
            LOGGER.info("Submitting files to toshiko disk environment...");
            this.moveVideo(diskFile);
            for (DiskFile subtitle : webSubtitles) {
                this.moveSubtitle(subtitle);
            }
            LOGGER.info("Removing leftover files from automation directory...");
            //noinspection ResultOfMethodCallIgnored
            subtitles.forEach(subtitle -> subtitle.getFile().delete());
            this.diskFileLocking.remove(diskFile);
            LOGGER.info("The file '{}' was imported with success.", this.getRelativeFsPath(diskFile.getFile()));
            this.publisher.publishEvent(new FileImportedEvent(this, diskFile));

            if (!this.hasFileWaiting && this.automationQueue.isEmpty()) {
                LOGGER.info("Automation folder is empty, rebuilding cache...");
                this.diskService.cache();
                LOGGER.info("Cache refreshed.");
            } else if (this.getAmountInQueue() % 10 == 0) {
                this.diskService.cache();
                LOGGER.info("Cache refreshed.");
            }
        } catch (Exception e) {
            // Put it back in the queue (maybe it's a temporary failure ?)
            this.automationQueue.offer(diskFile);
            LOGGER.error("Failed to handle file {}: {}", this.getRelativeFsPath(diskFile.getFile()), e.getMessage());
            LOGGER.error("   > Failure reason:", e);
        }
    }

    @EventListener
    public void onTorrentStatusUpdated(TorrentStatusUpdatedEvent event) throws IOException {

        if (!event.getCurrent().isFinished()) {
            return;
        }

        // Try to find the file.
        File       torrentDirectory = this.diskService.getTorrentPath().toFile();
        List<File> content          = FileSystemUtils.files(torrentDirectory);

        Optional<File> optionalFile = content.stream()
                                             .filter(file -> file.getName().equals(event.getEntity().getFile()))
                                             .findFirst();

        if (optionalFile.isEmpty()) {
            LOGGER.warn("Could not finalize {}", event.getEntity().getName());
            return;
        }

        File episode     = optionalFile.get();
        File automation  = this.diskService.getAutomationPath().toFile();
        File destination = new File(automation, event.getEntity().getAnime().getDiskPath());
        if (!destination.mkdirs()) {
            LOGGER.warn("Unable to create destination folder for anime {}. Further file management could lead to errors.", event.getEntity().getAnime().getName());
        }

        AnimeRenamer.rename(episode, destination);
        this.checkForAutomation();
    }

}
