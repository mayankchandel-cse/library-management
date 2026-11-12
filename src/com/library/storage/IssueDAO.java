package com.library.storage;

import com.library.exceptions.InvalidInputException;
import com.library.exceptions.LibraryException;
import com.library.model.IssueRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * IssueDAO — data-access object for issue / return records.
 * Works on the in-memory list in DataStore (no SQL, no database).
 */
public class IssueDAO {

    private final DataStore store;

    public IssueDAO() {
        this.store = DataStore.getInstance();
    }

    IssueDAO(DataStore store) {
        this.store = store;
    }

    /** Adds a record; id is assigned if missing. */
    public IssueRecord add(IssueRecord record) throws InvalidInputException {
        if (record == null) {
            throw new InvalidInputException("Issue record cannot be null.");
        }
        if (record.getId() <= 0) {
            record.setId(store.nextIssueId());
        }
        store.getIssues().add(record);
        return record;
    }

    public IssueRecord getById(int id) throws LibraryException {
        for (IssueRecord r : store.getIssues()) {
            if (r.getId() == id) {
                return r;
            }
        }
        throw new LibraryException("Issue record not found with id: " + id);
    }

    public List<IssueRecord> getAll() {
        return new ArrayList<>(store.getIssues());
    }

    // ----- aliases used by the service layer (same behaviour) -----
    public List<IssueRecord> getAllIssues() { return getAll(); }
    public IssueRecord addIssue(IssueRecord record) throws InvalidInputException {
        return add(record);
    }
    public IssueRecord updateIssue(IssueRecord record) throws LibraryException {
        return update(record);
    }
    public void deleteIssue(int id) throws LibraryException { delete(id); }

    /** All records for one member, newest first by issue date. */
    public List<IssueRecord> getByMember(int memberId) {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : store.getIssues()) {
            if (r.getMemberId() == memberId) {
                result.add(r);
            }
        }
        result.sort((a, b) -> b.getIssueDate().compareTo(a.getIssueDate()));
        return result;
    }

    /** All records for one book. */
    public List<IssueRecord> getByBook(int bookId) {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : store.getIssues()) {
            if (r.getBookId() == bookId) {
                result.add(r);
            }
        }
        return result;
    }

    /** Books still out (not yet returned). */
    public List<IssueRecord> getActiveIssues() {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : store.getIssues()) {
            if ("Issued".equalsIgnoreCase(r.getStatus())
                    || "Overdue".equalsIgnoreCase(r.getStatus())) {
                result.add(r);
            }
        }
        return result;
    }

    /** Active issues for one member (used to enforce maxBooks). */
    public List<IssueRecord> getActiveIssuesByMember(int memberId) {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : getActiveIssues()) {
            if (r.getMemberId() == memberId) {
                result.add(r);
            }
        }
        return result;
    }

    /** Records whose due date has passed and are still out. */
    public List<IssueRecord> getOverdueIssues() {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : store.getIssues()) {
            if (r.isOverdue()) {
                result.add(r);
            }
        }
        return result;
    }

    /** Replaces the stored record with the same id. */
    public IssueRecord update(IssueRecord record) throws LibraryException {
        if (record == null) {
            throw new InvalidInputException("Issue record cannot be null.");
        }
        List<IssueRecord> list = store.getIssues();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == record.getId()) {
                list.set(i, record);
                return record;
            }
        }
        throw new LibraryException("Issue record not found with id: " + record.getId());
    }

    public void delete(int id) throws LibraryException {
        IssueRecord record = getById(id); // throws if missing
        store.getIssues().remove(record);
    }
}
