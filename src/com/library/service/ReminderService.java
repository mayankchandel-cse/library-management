package com.library.service;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * ReminderService — background thread that reminds the librarian about
 * overdue books.
 *
 * <p>Thread lifecycle: NEW (after construction) → RUNNABLE (after
 * {@link #start()}) → TIMED_WAITING (while sleeping between scans) →
 * TERMINATED (after {@link #stop()}). The scan reads through
 * {@link LibraryService}, whose methods are {@code synchronized}, so the
 * reminder thread can never see a half-updated issue list.</p>
 *
 * <p>The scan interval is 5 seconds in this demo build so the effect is
 * visible in a viva; a real library would use hours or a daily run.</p>
 */
public class ReminderService implements Runnable {

    /** Demo scan interval: 5 seconds between scans. */
    public static final long SCAN_INTERVAL_MS = 5_000L;

    private final LibraryService service;
    private final long intervalMs;
    private volatile boolean running = false;
    private Thread thread;

    /** Creates the service for the given library service (5 s interval). */
    public ReminderService(LibraryService service) {
        this(service, SCAN_INTERVAL_MS);
    }

    /** Creates the service with a custom scan interval (ms). */
    public ReminderService(LibraryService service, long intervalMs) {
        this.service = service;
        this.intervalMs = intervalMs;
    }

    /** Starts the background thread (no-op if it is already running). */
    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "Overdue-Reminder-Thread");
        thread.setDaemon(true); // never blocks the JVM from exiting
        thread.start();
        System.out.println("[Reminder] Started — scanning for overdue books every "
                + (intervalMs / 1000) + " seconds.");
    }

    /** Stops the background thread and waits for it to finish. */
    public synchronized void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
            try { thread.join(2000); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[Reminder] Stopped.");
    }

    /** True while the reminder thread is running. */
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (running) {
            try {
                scanOnce();
                Thread.sleep(intervalMs); // TIMED_WAITING
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /** One scan: prints a reminder line for every overdue issue. */
    public void scanOnce() {
        List<IssueRecord> overdue = service.getOverdueRecords();
        if (overdue.isEmpty()) {
            System.out.println("[Reminder] No overdue books right now.");
            return;
        }
        LocalDate today = LocalDate.now();
        for (IssueRecord record : overdue) {
            LocalDate due = record.getDueDate(); // LocalDate in Part A
            long daysLate = ChronoUnit.DAYS.between(due, today);
            Book book = service.getBookById(record.getBookId());
            Member member = service.getMemberById(record.getMemberId());
            System.out.println("[Reminder] OVERDUE: '"
                    + (book == null ? "book " + record.getBookId() : book.getTitle())
                    + "' — " + (member == null ? "member " + record.getMemberId() : member.getName())
                    + " — " + daysLate + " day(s) late, fine so far Rs "
                    + FineCalculator.calculateFine(due, today));
        }
    }
}
