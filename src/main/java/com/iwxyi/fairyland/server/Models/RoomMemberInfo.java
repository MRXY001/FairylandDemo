package com.iwxyi.fairyland.server.Models;

import java.io.Serializable;

public class RoomMemberInfo implements Serializable {
    private static final long serialVersionUID = -6347911007178390219L;
    private User user;
    private RoomMember roomMember;
    
    public RoomMemberInfo(User user)
    {
        RoomMember member = new RoomMember();
        this.user = user;
        this.roomMember = member;
    }
    
    public RoomMemberInfo(RoomMember member)
    {
        User user = new User();
        this.user = user;
        this.roomMember = member;
    }
    
    public RoomMemberInfo(User user, RoomMember member)
    {
        this.user = user;
        this.roomMember = member;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setRoomMember(RoomMember member) {
        this.roomMember = member;
    }
    
    public User getUser() {
        return user;
    }
    
    public RoomMember getRoomMember() {
        return roomMember;
    }
    
}
