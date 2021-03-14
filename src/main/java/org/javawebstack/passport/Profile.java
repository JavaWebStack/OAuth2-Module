package org.javawebstack.passport;

import org.javawebstack.abstractdata.AbstractObject;

public class Profile extends AbstractObject {
    public String id;
    public String name;
    public String mail;
    public String avatar;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getAvatar() {
        return avatar;
    }
}
