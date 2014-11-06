/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aizou.peachtravel.config;

public class Constant {
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";

    public static final String EXT_TYPE="tzType";
    public static final String MSG_CONTENT="content";
    public static final String FROM_USER="fromUser";

    public static class ExtType{
        public static final int GUIDE=1;
        public static final int CITY=2;
        public static final int TRAVELS=3;
        public static final int SPOT=4;
        public static final int FOOD=5;
        public static final int SHOPPING=6;
        public static final int HOTEL=7;
        //提示消息
        public static final int TIPS=100;
    }
}
