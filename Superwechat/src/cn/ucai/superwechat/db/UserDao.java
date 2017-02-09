/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
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
package cn.ucai.superwechat.db;

import android.content.Context;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.domain.RobotUser;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	
	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

	public static final String ROBOT_TABLE_NAME = "robots";
	public static final String ROBOT_COLUMN_NAME_ID = "username";
	public static final String ROBOT_COLUMN_NAME_NICK = "nick";
	public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

	public static final String USER_TABLE_NAME = "t_superwechat_user";
	public static final String USER_COLUMN_NAME = "m_user_name";
	public static final String USER_COLUMN_NICK = "m_user_nick";
	public static final String USER_COLUMN_AVATAR = "m_user_avatar_id";
	public static final String USER_COLUMN_AVATAR_PATH = "m_user_avatar_path";
	public static final String USER_COLUMN_AVATAR_SUFFIX = "m_user_avatar_SUFFIX";
	public static final String USER_COLUMN_AVATAR_TYPE = "m_user_avatar_type";
	public static final String USER_COLUMN_AVATAR_UPDATA_TIME = "m_user_update_time";
	
	public UserDao(Context context) {
	}

	/**
	 * save contact list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<EaseUser> contactList) {
	    SuperWechatDBManager.getInstance().saveContactList(contactList);
	}

	/**
	 * get contact list
	 * 
	 * @return
	 */
	public Map<String, EaseUser> getContactList() {
		
	    return SuperWechatDBManager.getInstance().getContactList();
	}
	
	/**
	 * delete a contact
	 * @param username
	 */
	public void deleteContact(String username){
	    SuperWechatDBManager.getInstance().deleteContact(username);
	}

	/**
	 * save a contact
	 * @param user
	 */
	public void saveContact(EaseUser user){
		SuperWechatDBManager.getInstance().saveContact(user);
	}

	/**
	 * save a contact
	 * @param user
	 */
	public void saveAppContact(User user){
	    SuperWechatDBManager.getInstance().saveAppContact(user);
	}

	/**
	 * save contact list
	 *
	 * @param contactList
	 */
	public void saveAppContactList(List<User> contactList) {
		SuperWechatDBManager.getInstance().saveAppContactList(contactList);
	}

	/**
	 * get contact list
	 *
	 * @return
	 */
	public Map<String, User> getAppContactList() {
		return SuperWechatDBManager.getInstance().getAppContactList();
	}

	/**
	 * delete a contact
	 * @param username
	 */
	public void deleteAppContact(String username){
		SuperWechatDBManager.getInstance().deleteAppContact(username);
	}

	public void setDisabledGroups(List<String> groups){
	    SuperWechatDBManager.getInstance().setDisabledGroups(groups);
    }
    
    public List<String>  getDisabledGroups(){       
        return SuperWechatDBManager.getInstance().getDisabledGroups();
    }
    
    public void setDisabledIds(List<String> ids){
        SuperWechatDBManager.getInstance().setDisabledIds(ids);
    }
    
    public List<String> getDisabledIds(){
        return SuperWechatDBManager.getInstance().getDisabledIds();
    }
    
    public Map<String, RobotUser> getRobotUser(){
    	return SuperWechatDBManager.getInstance().getRobotList();
    }
    
    public void saveRobotUser(List<RobotUser> robotList){
    	SuperWechatDBManager.getInstance().saveRobotList(robotList);
    }
}
