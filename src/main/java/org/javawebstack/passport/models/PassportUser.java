package org.javawebstack.passport.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;

import java.sql.Timestamp;

@Dates
public class PassportUser extends Model {
    @Column
    public int id;

    @Column
    public String userId;

    @Column
    public String serviceUserId;

    @Column
    public String service;

    @Column
    public Timestamp createdAt;
    @Column
    public Timestamp updatedAt;
}
