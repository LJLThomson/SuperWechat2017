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
package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.adapter.NewFriendsMsgAdapter;
import cn.ucai.superwechat.db.InviteMessgeDao;
import cn.ucai.superwechat.domain.InviteMessage;

/**
 * Application and notification
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
	TextView newFriends;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_friends_msg);
		newFriends = (TextView)findViewById(R.id.newFriends);
		newFriends.setText("新的朋友");
		ListView listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();

		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
//		将申请和通知页面的小红点取消（count为0时，不显示）
		dao.saveUnreadMessageCount(0);
	}

	public void back(View view) {
		finish();
	}
}
