package com.knox.server.Library.utilities;

import com.sun.istack.internal.NotNull;

class Owner {

    private String firstName = "";
    private String lastName = "";

    public Owner() {
    }

    public Owner(@NotNull String firstName, @NotNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this.firstName.equals(((Owner) obj).firstName) && this.lastName.equals(((Owner) obj).lastName)) {
            return true;
        }
        return false;
    }

    public boolean isInvalid() {
        return this.firstName.length() == 0 || this.lastName.length() == 0;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}