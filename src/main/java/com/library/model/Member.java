package com.library.model;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String memberType;
    private int maxBooks;

    public Member() {
    }

    public Member(int id, String name, String email, String phone, String memberType, int maxBooks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memberType = memberType;
        this.maxBooks = maxBooks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + '\'' + ", email='" + email + '\''
                + ", phone='" + phone + '\'' + ", memberType='" + memberType + '\''
                + ", maxBooks=" + maxBooks + '}';
    }
}
