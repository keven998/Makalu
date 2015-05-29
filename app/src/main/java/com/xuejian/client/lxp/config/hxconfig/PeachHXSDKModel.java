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
package com.xuejian.client.lxp.config.hxconfig;

import android.content.Context;

import com.xuejian.client.lxp.common.hxsdk.model.DefaultHXSDKModel;

public class PeachHXSDKModel extends DefaultHXSDKModel {

    public PeachHXSDKModel(Context ctx) {
        super(ctx);
        // TODO Auto-generated constructor stub
    }

    // demo will use HuanXin roster
    public boolean getUseHXRoster() {
        // TODO Auto-generated method stub
        return true;
    }
    
    // demo will switch on debug mode
    public boolean isDebugMode(){
        return true;
    }
    
}
