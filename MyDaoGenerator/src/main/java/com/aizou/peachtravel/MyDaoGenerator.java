package com.aizou.peachtravel;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(2, "com.aizou.peachtravel");
        Entity user = schema.addEntity("IMUser");
        user.addLongProperty("userId");
        user.addStringProperty("username").primaryKey();
        user.addStringProperty("nick");
        user.addStringProperty("avatar");
        user.addStringProperty("avatarSmall");
        user.addStringProperty("gender");
        user.addStringProperty("signature");
        user.addStringProperty("tel");
        user.addStringProperty("memo");
        user.addIntProperty("unreadMsgCount");
        user.addStringProperty("header");
        user.addBooleanProperty("isMyFriends").notNull();

        Entity msg = schema.addEntity("InviteMessage");
        msg.addStringProperty("nickname");
        msg.addLongProperty("userId");
        msg.addStringProperty("avatar");
        msg.addStringProperty("gender");
        msg.addStringProperty("from").primaryKey();
        msg.addStringProperty("reason");
        msg.addLongProperty("time");
        msg.addIntProperty("status");
        msg.addStringProperty("groupId");
        msg.addStringProperty("groupName");
        new DaoGenerator().generateAll(schema, args[0]);
    }
}
