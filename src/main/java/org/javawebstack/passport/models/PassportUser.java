package org.javawebstack.passport.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;

import java.sql.Timestamp;

public interface PassportUser {
    String setPassword(String unhashedPassword);
    boolean checkPassword(String hashedPassword);
}
