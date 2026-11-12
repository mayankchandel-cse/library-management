package com.library.storage;

import com.library.exceptions.InvalidInputException;
import com.library.exceptions.MemberNotFoundException;
import com.library.model.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * MemberDAO — data-access object for members.
 * Works on the in-memory list in DataStore (no SQL, no database).
 */
public class MemberDAO {

    private final DataStore store;

    public MemberDAO() {
        this.store = DataStore.getInstance();
    }

    MemberDAO(DataStore store) {
        this.store = store;
    }

    /** Adds a member after validating; email must be unique. */
    public Member add(Member member) throws InvalidInputException {
        validate(member);
        for (Member m : store.getMembers()) {
            if (m.getEmail().equalsIgnoreCase(member.getEmail())) {
                throw new InvalidInputException(
                        "A member with email " + member.getEmail() + " already exists.");
            }
        }
        if (member.getId() <= 0) {
            member.setId(store.nextMemberId());
        }
        store.getMembers().add(member);
        return member;
    }

    public Member getById(int id) throws MemberNotFoundException {
        for (Member m : store.getMembers()) {
            if (m.getId() == id) {
                return m;
            }
        }
        throw new MemberNotFoundException(id);
    }

    public Member getByEmail(String email) throws MemberNotFoundException {
        for (Member m : store.getMembers()) {
            if (m.getEmail().equalsIgnoreCase(email)) {
                return m;
            }
        }
        throw new MemberNotFoundException("Member not found with email: " + email);
    }

    public List<Member> getAll() {
        return new ArrayList<>(store.getMembers());
    }

    // ----- aliases used by the service layer (same behaviour) -----
    public List<Member> getAllMembers() { return getAll(); }
    public Member addMember(Member member) throws InvalidInputException { return add(member); }
    public Member updateMember(Member member)
            throws MemberNotFoundException, InvalidInputException {
        return update(member);
    }
    public void deleteMember(int id) throws MemberNotFoundException { delete(id); }

    /** Case-insensitive search in name or email. */
    public List<Member> search(String keyword) {
        List<Member> result = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return result;
        }
        String q = keyword.trim().toLowerCase();
        for (Member m : store.getMembers()) {
            if ((m.getName() != null && m.getName().toLowerCase().contains(q))
                    || (m.getEmail() != null && m.getEmail().toLowerCase().contains(q))) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Member> getByType(String memberType) {
        List<Member> result = new ArrayList<>();
        for (Member m : store.getMembers()) {
            if (m.getMemberType() != null
                    && m.getMemberType().equalsIgnoreCase(memberType)) {
                result.add(m);
            }
        }
        return result;
    }

    public Member update(Member member)
            throws MemberNotFoundException, InvalidInputException {
        validate(member);
        List<Member> list = store.getMembers();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == member.getId()) {
                list.set(i, member);
                return member;
            }
        }
        throw new MemberNotFoundException(member.getId());
    }

    public void delete(int id) throws MemberNotFoundException {
        Member member = getById(id); // throws if missing
        store.getMembers().remove(member);
    }

    private void validate(Member member) throws InvalidInputException {
        if (member == null) {
            throw new InvalidInputException("Member cannot be null.");
        }
        if (member.getName() == null || member.getName().isBlank()) {
            throw new InvalidInputException("Name is required.");
        }
        if (member.getEmail() == null || member.getEmail().isBlank()
                || !member.getEmail().contains("@")) {
            throw new InvalidInputException("A valid email is required.");
        }
        if (member.getMemberType() == null
                || !(Member.STUDENT.equalsIgnoreCase(member.getMemberType())
                || Member.FACULTY.equalsIgnoreCase(member.getMemberType())
                || Member.STAFF.equalsIgnoreCase(member.getMemberType()))) {
            throw new InvalidInputException(
                    "Member type must be Student, Faculty or Staff.");
        }
    }
}
