package com.movieticket.service;

import com.movieticket.model.Ticket;
import com.movieticket.model.TicketStatus;
import com.movieticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class TicketService {

    private static final long LOCK_TIMEOUT_MINUTES = 5;

    @Autowired
    private TicketRepository ticketRepository;

    // Map to store idempotency keys and their results (in-memory for demo; use persistent store for production)
    private final Map<String, Boolean> idempotencyMap = new ConcurrentHashMap<>();

    // Thread for periodic cleanup of idempotency keys and unlocking expired tickets
    private Thread cleanupThread;
    private volatile boolean running = true;

    @PostConstruct
    public void startCleanupThread() {
        cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    unlockExpiredTickets();
                    idempotencyMap.clear(); // Clear idempotency keys every 10 minutes
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        running = false;
        if (cleanupThread != null) {
            cleanupThread.interrupt();
        }
        super.finalize();
    }

    public boolean lockTicket(Long ticketId, Long userId, String idempotencyKey) {
        if (idempotencyMap.containsKey(idempotencyKey)) {
            return idempotencyMap.get(idempotencyKey);
        }
        boolean result = false;
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            // Unlock if lock expired
            if (ticket.getStatus() == TicketStatus.LOCKED && isLockExpired(ticket)) {
                ticket.setStatus(TicketStatus.AVAILABLE);
                ticket.setLockTime(null);
                ticket.setLockedByUserId(null);
                ticketRepository.save(ticket);
            }
            if (ticket.getStatus() == TicketStatus.AVAILABLE) {
                ticket.setStatus(TicketStatus.LOCKED);
                ticket.setLockTime(LocalDateTime.now());
                ticket.setLockedByUserId(userId);
                ticketRepository.save(ticket);
                result = true;
            }
        }
        idempotencyMap.put(idempotencyKey, result);
        return result;
    }

    public boolean bookTicket(Long ticketId, Long userId, String idempotencyKey) {
        if (idempotencyMap.containsKey(idempotencyKey)) {
            return idempotencyMap.get(idempotencyKey);
        }
        boolean result = false;
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            if (ticket.getStatus() == TicketStatus.LOCKED &&
                userId.equals(ticket.getLockedByUserId()) &&
                !isLockExpired(ticket)) {
                ticket.setStatus(TicketStatus.BOOKED);
                ticket.setLockTime(null);
                ticket.setLockedByUserId(null);
                ticketRepository.save(ticket);
                result = true;
            }
        }
        idempotencyMap.put(idempotencyKey, result);
        return result;
    }

    public void unlockExpiredTickets() {
        // Example: unlock all expired tickets (should be optimized for your data source)
        for (Ticket ticket : ticketRepository.findAll()) {
            if (ticket.getStatus() == TicketStatus.LOCKED && isLockExpired(ticket)) {
                ticket.setStatus(TicketStatus.AVAILABLE);
                ticket.setLockTime(null);
                ticket.setLockedByUserId(null);
                ticketRepository.save(ticket);
            }
        }
    }

    private boolean isLockExpired(Ticket ticket) {
        if (ticket.getLockTime() == null) return false;
        return Duration.between(ticket.getLockTime(), LocalDateTime.now()).toMinutes() >= LOCK_TIMEOUT_MINUTES;
    }
}